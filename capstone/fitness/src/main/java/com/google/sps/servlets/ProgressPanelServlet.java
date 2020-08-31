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
import com.google.gson.reflect.TypeToken;
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

@WebServlet("/panels")
public class ProgressPanelServlet extends HttpServlet {

  private static final String SESSION = "Last Session";
  private static final String NEXT_STEP = "Next Step";
  private static final String GOAL = "Goal";
  private static final String VIEWING_STEP = "Viewing Step";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession();
    String workoutName = (String) session.getAttribute("workoutName");
    String goalStepsJson = DataHandler.getGoalSteps(DataHandler.getWorkout(workoutName));
    List<JsonExercise> panelDisplay = Collections.emptyList();
    if(goalStepsJson != null) {
      ProgressModel model = new ProgressModel.Builder()
                            .setJsonGoalSteps(goalStepsJson)
                            .build();
      Integer insertion = (Integer) session.getAttribute("insertion");
      GoalStep viewing = insertion == null ? null : model.toArray()[insertion];
      panelDisplay = getPanelItems(DataHandler.getLastSession(workoutName), model.getCurrentMainGoalStep(), model.getLast(), viewing);
    }
    response.setContentType("application/json");
    response.getWriter().println(getJson(panelDisplay));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession();
    session.setAttribute("insertion", updateInsertion(request));
    response.sendRedirect("/progress.html");
  }

  private List<JsonExercise> getPanelItems(Session session, GoalStep next, GoalStep goal, GoalStep viewing) {
    List<JsonExercise> exercises = new ArrayList<>();
    // Do not get extra info if there's no session.
    if(session == null) {
      return exercises;
    }

    // Add last session exercises.
    for(Exercise e : session.getWorkout()) {
      exercises.add(new JsonExercise(SESSION, e));
    }

    // Add panel comparison exercises.
    if(next.getMarker() != null) {
      exercises.add(new JsonExercise(NEXT_STEP, next));
    }
    else {
      exercises.add(new JsonExercise(NEXT_STEP, goal));
    }

    exercises.add(new JsonExercise(GOAL, goal));
    if(viewing != null) {
      exercises.add(new JsonExercise(VIEWING_STEP, viewing));
    }
    
    return exercises;
  }

  private String getJson(List<JsonExercise> panelDisplay) {
    Gson gson = new Gson();
    String json = gson.toJson(panelDisplay);
    return json;
  }

  private Integer updateInsertion(HttpServletRequest request) {
    // Insertion index refers to actual index of step being viewed in viewing panel.
    String insertionString = request.getParameter("insertion");
    if(insertionString != null  && !insertionString.isEmpty()) {
      int parsedInsertion = Integer.parseInt(insertionString);
      return parsedInsertion < 0 ? null : parsedInsertion;
    }
    return null;
  }
}