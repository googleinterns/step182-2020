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
    List<JsonExercise> display = Collections.emptyList();
    if(goalStepsJson != null) {
      // Replicate existing model and stored version based on session in DataHandler.
      ProgressModel model = new ProgressModel.Builder()
                  .setJsonGoalSteps(goalStepsJson)
                  .build();
      if(model.updateModel()) {
        DataHandler.setGoalSteps(model.toJson());
      }
      display = getProgressDisplays(model.toArray());
    }
    response.setContentType("application/json");
    response.getWriter().println(getJson(display));
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
    String json = gson.toJson(display);
    return json;
  }
}