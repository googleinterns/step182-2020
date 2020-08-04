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
import com.google.sps.util.DataHelper;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.FetchOptions;

@WebServlet("/create-profile")
public class CreateProfileServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

    UserService userService = UserServiceFactory.getUserService();
    String userEmail = userService.getCurrentUser().getEmail();

    // Set up datastore.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key userKey = KeyFactory.createKey(DataHelper.USER_ENTITY, userEmail);
    Query query = new Query(DataHelper.USER_ENTITY).addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, userKey).setKeysOnly();
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
    String name = request.getParameter(DataHelper.NAME_PROPERTY);
    Integer age = Integer.parseInt(request.getParameter(DataHelper.AGE_PROPERTY));
    Integer weeksTotrain = Integer.parseInt(request.getParameter(DataHelper.WEEKS_TO_TRAIN_PROPERTY));
    Float lengthOfMarathon = Float.parseFloat(request.getParameter(DataHelper.MARATHON_LENGTH_PROPERTY));
    Float initialTime = Float.parseFloat(request.getParameter(DataHelper.INITIAL_TIME_PROPERTY));
    Float goalTime = Float.parseFloat(request.getParameter(DataHelper.GOAL_TIME_PROPERTY));

    // Create a user entity that uses the email as the key.
    Entity newUser = new Entity(DataHelper.USER_ENTITY, userEmail);
    newUser.setProperty(DataHelper.NAME_PROPERTY, name);
    newUser.setProperty(DataHelper.MARATHON_LENGTH_PROPERTY, lengthOfMarathon);
    newUser.setProperty(DataHelper.WEEKS_TO_TRAIN_PROPERTY, weeksTotrain);
    newUser.setProperty(DataHelper.PROGRESS_PROPERTY, "[]");
    newUser.setProperty(DataHelper.INITIAL_TIME_PROPERTY, initialTime);
    newUser.setProperty(DataHelper.GOAL_TIME_PROPERTY, goalTime);

    // Put user in datastore.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(newUser);
    
    // Redirect back to home.
    response.sendRedirect("/");
  }
}
