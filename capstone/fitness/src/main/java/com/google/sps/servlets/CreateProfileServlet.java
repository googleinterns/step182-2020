package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/create-profile")
public class CreateProfileServlet extends HttpServlet {
  
  // Constants used for datastore.
  final static String USER_ENTITY = "user";
  final static String PROGRESS_PROPERTY = "sessions";
  final static String NAME_PROPERTY = "name";
  final static String AGE_PROPERTY = "age";
  final static String MARATHON_LENGTH_PROPERTY = "marathonLength";
  final static String WEEKS_PROPERTY = "weeksToPrepare";
  final static String INITIAL_TIME_PROPERTY = "initialTime";
  final static String GOAL_TIME_PROPERTY = "goalTime";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

    // We get the user email everytime we do a request because if we had it in.
    UserService userService = UserServiceFactory.getUserService();
    String userEmail = userService.getCurrentUser().getEmail();

    // Set up datastore.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key userKey = KeyFactory.createKey(USER_ENTITY, userEmail);
    Query query = new Query(USER_ENTITY).addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, userKey).setKeysOnly();
    PreparedQuery results = datastore.prepare(query);

    // Although it is a loop, results only has at most 1 item so it is still O(1)
    int counter=0;
    for(Entity e : results.asIterable()) {
      counter++;
    } 

    // If the user is already in datastore, we do not need to re-create profile.
    if(counter > 0) {
      response.sendRedirect("/");
    }
    else {
      RequestDispatcher view = request.getRequestDispatcher("profile-info.html");
      view.forward(request, response);
    }

  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Get user email
    UserService userService = UserServiceFactory.getUserService();
    String userEmail = userService.getCurrentUser().getEmail();

    // Get the parameters from the request;
    String name = request.getParameter("nickname");
    Integer age = Integer.parseInt(request.getParameter("age"));
    Integer weeksTotrain = Integer.parseInt(request.getParameter("timeToTrain"));
    Float lengthOfMarathon = Float.parseFloat(request.getParameter("marathonLength"));
    Float initialTime = Float.parseFloat(request.getParameter("initialTime"));
    Float goalTime = Float.parseFloat(request.getParameter("goalTime"));

    // Create a user entity that uses the email as the key.
    Entity newUser = new Entity(USER_ENTITY, userEmail);
    newUser.setProperty(NAME_PROPERTY, name);
    newUser.setProperty(MARATHON_LENGTH_PROPERTY, lengthOfMarathon);
    newUser.setProperty(WEEKS_PROPERTY, weeksTotrain);
    newUser.setProperty(PROGRESS_PROPERTY, "[]"); //Initialize like this for JSON format.
    newUser.setProperty(INITIAL_TIME_PROPERTY, initialTime);
    newUser.setProperty(GOAL_TIME_PROPERTY, goalTime);

    // Put user in datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(newUser);

    response.sendRedirect("/");

  }


}
