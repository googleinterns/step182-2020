package com.google.sps.servlets;

import com.google.sps.util.DataHandler;
import com.google.sps.util.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.Entity;
import org.json.*;

/**
 This servelet is used to easily retrieve an workout's data from datastore 
 in JSON format.
*/
@WebServlet("/get-workout-data")
public class WorkoutDataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

    response.setContentType("application/json");

    String workoutName = request.getReader().readLine();
    Entity workout = DataHandler.getWorkout(workoutName);

    if(workout == null) {
      response.getWriter().println("{}");
      return;      
    }

    JSONObject json = new JSONObject();

    for(String property : DataHandler.WORKOUT_PROPERTIES) {
        if(DataHandler.isNumber(property)) {
            json.put(property, Float.parseFloat(DataHandler.getWorkoutData(property, workout)));
        }
        else {
            json.put(property, DataHandler.getWorkoutData(property, workout));
        }
    }

    if(DataHandler.getWorkoutData(DataHandler.TYPE_PROPERTY, workout).equals("lifting")) {
      
      for(String property : DataHandler.LIFTING_PROPERTIES) {
        if(DataHandler.isNumber(property)) {
          json.put(property, Float.parseFloat(DataHandler.getWorkoutData(property, workout)));
        }
        else {
          json.put(property, DataHandler.getWorkoutData(property, workout));
        }
      }

    }
    else {
      
      for(String property : DataHandler.MARATHON_PROPERTIES) {
        if(DataHandler.isNumber(property)) {
          json.put(property, Float.parseFloat(DataHandler.getWorkoutData(property, workout)));
        }
        else {
          json.put(property, DataHandler.getWorkoutData(property, workout));
        }
      }

    }

    response.getWriter().println(json.toString());
  }
}
