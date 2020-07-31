package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.servlets.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/progress")
public class GetProgressServlet extends HttpServlet {

  // Constants for datastore.
  final static String USER_ENTITY = "user";
  final static String PROGRESS_PROPERTY = "sessions";
  final static String NAME_PROPERTY = "name";
  final static String AGE_PROPERTY = "age";
  final static String MARATHON_LENGTH_Property = "marathonLength";
  final static String WEEKS_Property = "weeksToPrepare";


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Get the users email
    UserService userService = UserServiceFactory.getUserService();
    String userEmail = userService.getCurrentUser().getEmail();

    // Set up datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(USER_ENTITY);
    PreparedQuery results = datastore.prepare(query);

    // Get user from datastore
    // I tried some other methods for getting the user but they were giving me problems.
    // This approach is O(n) which should be the same as the built in methods.
    Entity user=null;
    loopThroughComments: 
    for(Entity e : results.asIterable()) {
        String key = e.getKey().toString();
        if(!(key == null) && UpdateProgressServlet.equalKeys(userEmail, key)) {
          user = e;
        }
    }

    // Get the users progress from datastore
    String progressString = (String) user.getProperty(PROGRESS_PROPERTY);

    //Give the output in JSON format
    response.setContentType("application/json");
    response.getWriter().println(progressString);



  }
}
