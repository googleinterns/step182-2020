package com.google.sps.servlets;

import com.google.sps.progress.*;
import com.google.sps.util.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import org.json.*;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.*;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/create-workout")
public class CreateWorkoutServlet extends HttpServlet { 

  private static final int DAYS_PER_WEEK = 3;
  static Gson gson = new Gson();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Get the JSON string that we created in JavaScript from the request's body
    String requestBody = request.getReader().readLine();
    JSONObject workoutJSON = new JSONObject(requestBody);
    
    Entity user = DataHandler.getUser();

    // Get the user's current workout list
    ArrayList<String> userWorkouts = gson.fromJson(DataHandler.getUserData(DataHandler.WORKOUT_LIST_PROPERTY,
                                                                           user), new TypeToken<List<String>>(){}.getType());

    // Add this new workout to their list
    String workoutName = (String) workoutJSON.get(DataHandler.WORKOUT_NAME_PROPERTY); 
    userWorkouts.add(workoutName);
    user.setProperty(DataHandler.WORKOUT_LIST_PROPERTY, gson.toJson(userWorkouts));

    String workoutType = (String) workoutJSON.get(DataHandler.TYPE_PROPERTY);
    int weeksToTrain = Integer.parseInt((String) workoutJSON.get(DataHandler.WEEKS_TO_TRAIN_PROPERTY));

    Entity newWorkout = new Entity(DataHandler.WORKOUT_ENTITY, workoutName);
    // Set the properties that all workouts share
    newWorkout.setProperty(DataHandler.PROGRESS_PROPERTY, "[]");
    newWorkout.setProperty(DataHandler.GOAL_STEPS_PROPERTY, "[]");
    newWorkout.setProperty(DataHandler.TYPE_PROPERTY, workoutType);
    newWorkout.setProperty(DataHandler.WEEKS_TO_TRAIN_PROPERTY, weeksToTrain);

    // Assign the correct properties depending on the type
    if(workoutType.equals("lifting")) {
      int repsStart = Integer.parseInt((String) workoutJSON.get(DataHandler.INITIAL_REPS_PROPERTY));
      int repsGoal = Integer.parseInt((String) workoutJSON.get(DataHandler.GOAL_REPS_PROPERTY));
      float weightStart = Float.parseFloat((String) workoutJSON.get(DataHandler.INITIAL_WEIGHT_PROPERTY));
      float weightGoal = Float.parseFloat((String) workoutJSON.get(DataHandler.GOAL_WEIGHT_PROPERTY));

      newWorkout.setProperty(DataHandler.INITIAL_WEIGHT_PROPERTY, weightStart);
      newWorkout.setProperty(DataHandler.INITIAL_REPS_PROPERTY, repsStart);
      newWorkout.setProperty(DataHandler.GOAL_WEIGHT_PROPERTY, weightGoal);
      newWorkout.setProperty(DataHandler.GOAL_REPS_PROPERTY, repsGoal);
      newWorkout.setProperty(DataHandler.GOAL_STEPS_PROPERTY, new Text(new ProgressModel.Builder()
                                                            .setDaysAvailable(weeksToTrain, DAYS_PER_WEEK)
                                                            .setStrengthTrainingStart(workoutName, repsStart, weightStart)
                                                            .setStrengthTrainingGoal(workoutName, repsGoal, weightGoal)
                                                            .build()
                                                            .toJson()));
    }
    else {
      float lengthOfMarathon = Float.parseFloat((String) workoutJSON.get(DataHandler.MARATHON_LENGTH_PROPERTY));
      float initialTime = Float.parseFloat((String) workoutJSON.get(DataHandler.INITIAL_TIME_PROPERTY));
      float goalTime = Float.parseFloat((String) workoutJSON.get(DataHandler.GOAL_TIME_PROPERTY));

      newWorkout.setProperty(DataHandler.MARATHON_LENGTH_PROPERTY, lengthOfMarathon);
      newWorkout.setProperty(DataHandler.INITIAL_TIME_PROPERTY, initialTime);
      newWorkout.setProperty(DataHandler.GOAL_TIME_PROPERTY, goalTime);
      newWorkout.setProperty(DataHandler.MILE_TIME_PROPERTY,
                            Float.parseFloat((String) workoutJSON.get(DataHandler.MILE_TIME_PROPERTY))/((float) 60.0));
      
      newWorkout.setProperty(DataHandler.GOAL_STEPS_PROPERTY, new Text(new ProgressModel.Builder()
                                                            .setDaysAvailable(weeksToTrain, DAYS_PER_WEEK)
                                                            .setDurationIncrementStart(workoutName, lengthOfMarathon/initialTime)
                                                            .setDurationIncrementGoal(workoutName, lengthOfMarathon/goalTime)
                                                            .build()
                                                            .toJson()));
    }

    // Put the new workout in datastore and update the user
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(user);
    datastore.put(newWorkout);
    response.sendRedirect("/");
  }
}
