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
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
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
    List<ProgressDisplay> display = getProgressDisplays(model.toArray());
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
}