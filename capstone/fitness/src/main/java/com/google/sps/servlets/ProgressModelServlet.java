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
import com.google.sps.fit.*;
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
    List<JsonExercise> display = null;
    if(goalStepsJson != null) {
      // Replicate existing model and stored version based on session in DataHandler.
      ProgressModel model = new ProgressModel.Builder()
                  .setJsonGoalSteps(goalStepsJson)
                  .build();
      if(model.updateModel()) {
        DataHandler.setGoalSteps(model.toJson());
      }
      display = getProgressDisplays(paginate(model.toArray(), request.getSession()));
    }
    response.setContentType("application/json");
    response.getWriter().println(getJson(display));
  }

  private GoalStep[] paginate(GoalStep[] goalSteps, HttpSession session) {
    // Fetch metadata in session or use default if not found.
    Metadata metadata = (Metadata) session.getAttribute("metadata");
    if(metadata == null) {
      metadata = new Metadata();
    }

    // Get split array based on sorting strategy.
    int[] pos = getPositions(goalSteps, metadata.getSort());
    GoalStep[] splitGoalSteps = getSplitGoalSteps(goalSteps, pos); 
    
    // Modify where the split array starts based on page number and goal step count per page.
    int startingIndex = metadata.getPage() * metadata.getCount();
    while(startingIndex >= splitGoalSteps.length) {
      metadata.setPage(metadata.getPage() - 1);
      startingIndex -= metadata.getCount();
    }

    // Update metadata parameters based on the goal steps array.
    metadata.setMaxPages(getMaxPages(metadata.getCount(), splitGoalSteps.length));
    metadata.setGoalSteps(goalSteps.length);
    metadata.setStartIndex(pos[0] + startingIndex);
    session.setAttribute("metadata", metadata);

    // Add visible goal steps based on metadata parameters.
    List<GoalStep> trueGoalSteps = new ArrayList<>();
    for(int i = startingIndex; i < startingIndex + metadata.getCount() && i < splitGoalSteps.length; i++) {
      trueGoalSteps.add(splitGoalSteps[i]);
    }
    return trueGoalSteps.toArray(new GoalStep[trueGoalSteps.size()]);
  }

  private int[] getPositions(GoalStep[] goalSteps, Sort sortingStrategy) {
    // Returns array of the start and end of goal step array based on the sorting strategy. 
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
    // Returns subarray of goal step array based on position tuple (start, end).
    GoalStep[] subArr = new GoalStep[pos[1] - pos[0] + 1];
    System.arraycopy(goalSteps, pos[0], subArr, 0, subArr.length);
    return subArr;
  }

  private int getMaxPages(int countPerPage, int totalSize) {
    // Returns the max number of pages an array can produce given the count per page and total size.
    int remainder = totalSize % countPerPage != 0 ? 1 : 0;
    int pageCount = (int)Math.floor((float)totalSize/countPerPage);
    int maxPages = pageCount + remainder;
    maxPages = maxPages == 0 ? 1 : maxPages;
    return maxPages;
  }

  private List<JsonExercise> getProgressDisplays(GoalStep[] goalSteps) {
    // Converts goal step array into simple, readable display objects list.
    List<JsonExercise> display = new ArrayList<>();
    for(GoalStep goalStep : goalSteps) {
      display.add(new JsonExercise("GoalStep", goalStep));
    }
    return display;
  }

  private String getJson(List<JsonExercise> display) {
    Gson gson = new Gson();
    String json = display != null ? gson.toJson(display) : gson.toJson(new ArrayList<>());
    return json;
  }
}