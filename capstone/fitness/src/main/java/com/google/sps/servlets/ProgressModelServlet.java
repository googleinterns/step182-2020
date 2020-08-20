// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.sps.progress.*;
import com.google.sps.util.*;
import com.google.sps.util.Metadata.Sort;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/pro")
public class ProgressModelServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String goalStepsJson = DataHandler.getGoalSteps();
    ProgressModel model;
    if(goalStepsJson == null) {
      model = new ProgressModel.Builder()
                  .build();
    }
    else {
      model = new ProgressModel.Builder()
                  .setJsonGoalSteps(goalStepsJson)
                  .build();
      if(model.updateModel()) {
        DataHandler.setGoalSteps(model.toJson());
      }
    }
    List<ProgressDisplay> display = getProgressDisplays(paginate(model.toArray(), request.getSession()));
    response.setContentType("application/json");
    response.getWriter().println(getJson(display));
  }

  private List<ProgressDisplay> getProgressDisplays(GoalStep[] goalSteps) {
    List<ProgressDisplay> display = new ArrayList<>();
    for(GoalStep goalStep : goalSteps) {
      display.add(new ProgressDisplay(goalStep));
    }
    return display;
  }

  private String getJson(List<ProgressDisplay> display) {
    Gson gson = new Gson();
    String json = gson.toJson(display);
    return json;
  }

  private GoalStep[] paginate(GoalStep[] goalSteps, HttpSession session) {
    Metadata metadata = (Metadata) session.getAttribute("metadata");
    if(metadata == null) {
      metadata = new Metadata();
    }
    int[] pos = getPositions(goalSteps, metadata.getSort());
    GoalStep[] splitGoalSteps = getSplitGoalSteps(goalSteps, pos); 
    int startingIndex = metadata.getPage() * metadata.getCount();

    while(startingIndex >= splitGoalSteps.length) {
      metadata.setPage(metadata.getPage() - 1);
      startingIndex -= metadata.getCount();
    }

    metadata.setMaxPages(getMaxPages(metadata.getCount(), splitGoalSteps.length));
    metadata.setGoalSteps(goalSteps.length);
    metadata.setStartIndex(pos[0] + startingIndex);
    List<GoalStep> trueGoalSteps = new ArrayList<>();
    for(int i = startingIndex; i < startingIndex + metadata.getCount() && i < splitGoalSteps.length; i++) {
      trueGoalSteps.add(splitGoalSteps[i]);
    }
    session.setAttribute("metadata", metadata);
    return trueGoalSteps.toArray(new GoalStep[trueGoalSteps.size()]);
  }

  private int[] getPositions(GoalStep[] goalSteps, Sort sortingStrategy) {
    int start = 0;
    int end = goalSteps.length - 1;
    switch(sortingStrategy) {
      case UNCOMPLETE:
        start = end;
        while(start > 0 && !goalSteps[start].isComplete()) {
          start--;
        }
        start++;
        break;
      case COMPLETE:
        end = start;
        while(end < goalSteps.length - 1 && goalSteps[end].isComplete()) {
          end++;
        }
        end--;
        break;
      default:
        break;
    }
    return new int[] {start, end};
  }

  private GoalStep[] getSplitGoalSteps(GoalStep[] goalSteps, int[] pos) {
    GoalStep[] subArr = new GoalStep[pos[1] - pos[0] + 1];
    System.arraycopy(goalSteps, pos[0], subArr, 0, subArr.length);
    return subArr;
  }

  private int getMaxPages(int batchSize, int size) {
    int remainder = size % batchSize != 0 ? 1 : 0;
    int pageCount = (int)Math.floor((float)size/batchSize);
    int maxPages = pageCount + remainder;
    maxPages = maxPages == 0 ? 1 : maxPages;
    return maxPages;
  }
}