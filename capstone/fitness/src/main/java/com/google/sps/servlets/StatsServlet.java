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
import com.google.sps.util.Metadata.Sort;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/stats")
public class StatsServlet extends HttpServlet {

  private static String SESSION = "Session";
  private static String NEXT_STEP = "Next Step";
  private static String GOAL = "Goal";
  private static String VIEWING_STEP = "Viewing";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String goalStepsJson = DataHandler.getGoalSteps();
    ProgressModel model = null;
    if(goalStepsJson != null) {
      model = new ProgressModel.Builder()
                  .setJsonGoalSteps(goalStepsJson)
                  .build();
    }
    HttpSession session = request.getSession();
    Integer insertion = (Integer) session.getAttribute("insertion");
    GoalStep viewing = insertion == null ? null : model.toArray()[insertion];
    List<JsonExercise> display = getStatItems(getLastSession(), model.getCurrentMainGoalStep(), model.getLast(), viewing);
    response.setContentType("application/json");
    response.getWriter().println(getJson(display));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession();
    session.setAttribute("insertion", updateInsertion(request));
    response.sendRedirect("/progress.html");
  }

  private Integer updateInsertion(HttpServletRequest request) {
    String insertionString = request.getParameter("insertion");
    if(insertionString != null  && !insertionString.isEmpty()) {
      int parsedInsertion = Integer.parseInt(insertionString);
      return parsedInsertion < 0 ? null : parsedInsertion;
    }
    return null;
  }

  private String getJson(List<JsonExercise> display) {
    Gson gson = new Gson();
    String json = gson.toJson(display);
    return json;
  }

  private List<JsonExercise> getStatItems(Session session, GoalStep next, GoalStep goal, GoalStep viewing) {
    List<JsonExercise> exercises = new ArrayList<>();
    if(session != null) {
      for(Exercise e : session.getWorkout()) {
        exercises.add(new JsonExercise(SESSION, e));
      }
    }

    exercises.add(new JsonExercise(NEXT_STEP, next));
    exercises.add(new JsonExercise(GOAL, goal));
    if(viewing != null) {
      exercises.add(new JsonExercise(VIEWING_STEP, viewing));
    }
    
    return exercises;
  } 

  private Session getLastSession() {
    String sessionsJson = DataHandler.getData(DataHandler.PROGRESS_PROPERTY, DataHandler.getUser());
    if(sessionsJson != null) {
      ArrayList<MarathonSession> sessions = new Gson().fromJson(sessionsJson, new TypeToken<List<MarathonSession>>(){}.getType());
      if(sessions.isEmpty()) {
        return null;
      }
      return new Session(sessions.get(sessions.size() - 1));
    }
    return null;
  }
}