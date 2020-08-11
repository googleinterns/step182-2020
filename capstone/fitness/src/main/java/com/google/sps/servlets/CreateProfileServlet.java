package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.progress.*;
import com.google.sps.util.DataHandler;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.gson.Gson;
import java.util.*;

@WebServlet("/create-profile")
public class CreateProfileServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

    UserService userService = UserServiceFactory.getUserService();
    String userEmail = userService.getCurrentUser().getEmail();

    // Set up datastore.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key userKey = KeyFactory.createKey(DataHandler.USER_ENTITY, userEmail);
    Query query = new Query(DataHandler.USER_ENTITY).addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, userKey).setKeysOnly();
    PreparedQuery results = datastore.prepare(query);

    // If the user is already in datastore, we do not need to re-create profile.
    if(results.asList(FetchOptions.Builder.withLimit(1)).size() > 0) {
      response.sendRedirect("/");
    }
    else {
      RequestDispatcher view = request.getRequestDispatcher("profile-info.html");
      view.forward(request, response);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    UserService userService = UserServiceFactory.getUserService();
    String userEmail = userService.getCurrentUser().getEmail();

    // Get the parameters from the request.
    String name = request.getParameter(DataHandler.NAME_PROPERTY);
    Integer age = Integer.parseInt(request.getParameter(DataHandler.AGE_PROPERTY));
    Integer weeksTotrain = Integer.parseInt(request.getParameter(DataHandler.WEEKS_TO_TRAIN_PROPERTY));
    Float lengthOfMarathon = Float.parseFloat(request.getParameter(DataHandler.MARATHON_LENGTH_PROPERTY));
    // Divide by 60 to convert to hours.
    Float mileTime = Float.parseFloat(request.getParameter(DataHandler.MILE_TIME_PROPERTY))/60;
    Float initialTime = Float.parseFloat(request.getParameter(DataHandler.INITIAL_TIME_PROPERTY));
    Float goalTime = Float.parseFloat(request.getParameter(DataHandler.GOAL_TIME_PROPERTY));

    // Create a user entity that uses the email as the key.
    Entity newUser = new Entity(DataHandler.USER_ENTITY, userEmail);
    newUser.setProperty(DataHandler.NAME_PROPERTY, name);
    newUser.setProperty(DataHandler.AGE_PROPERTY, age);
    newUser.setProperty(DataHandler.MARATHON_LENGTH_PROPERTY, lengthOfMarathon);
    newUser.setProperty(DataHandler.WEEKS_TO_TRAIN_PROPERTY, weeksTotrain);
    newUser.setProperty(DataHandler.PROGRESS_PROPERTY, "[]");
    newUser.setProperty(DataHandler.MILE_TIME_PROPERTY, mileTime);
    newUser.setProperty(DataHandler.INITIAL_TIME_PROPERTY, initialTime);
    newUser.setProperty(DataHandler.GOAL_TIME_PROPERTY, goalTime);

    String exerciseName = "Running";
    newUser.setProperty(DataHandler.GOAL_STEPS_PROPERTY, new Text(new ProgressModel.Builder()
                                                            .setDaysAvailable("" + weeksTotrain)
                                                            .setDurationIncrementStart(exerciseName, "" + lengthOfMarathon/initialTime)
                                                            .setDurationIncrementGoal(exerciseName, "" + lengthOfMarathon/goalTime)
                                                            .build()
                                                            .toJson()));

    // Put user in datastore.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(newUser);
    
    // Redirect back to home.
    response.sendRedirect("/");
  }
}
