package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;  
import com.google.sps.servlets.*;
import com.google.sps.util.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/update-progress")
public class UpdateProgressServlet extends HttpServlet {

  static Gson gson;  
  
  @Override
  public void init() {
    gson = new Gson();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    // Display the complete session screen which then sends a POST to this server.
    RequestDispatcher view = request.getRequestDispatcher("complete-session.html");
    view.forward(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    
    // Get the users email.
    UserService userService = UserServiceFactory.getUserService();
    String userEmail = userService.getCurrentUser().getEmail();

    // Redirect if user not found.
    if(userEmail == null) {
      RequestDispatcher view = request.getRequestDispatcher("/");
      view.forward(request, response);
      System.out.println("user is null");
    }

    // Set up datastore and get query.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(CreateProfileServlet.USER_ENTITY);
    PreparedQuery results = datastore.prepare(query);

    // Get user from datastore
    // I tried some other methods for getting the user but they were giving me problems.
    // This approach is O(n) which should be the same as the built in methods.
    Entity user=null;
    loopThroughComments: 
    for(Entity e : results.asIterable()) {
        String key = e.getKey().toString();
        if(!(key == null) && equalKeys(userEmail, key)) {
          user = e;
        }
    }

    // Redirect if user not found.
    if(user == null) {
      RequestDispatcher view = request.getRequestDispatcher("/");
      view.forward(request, response);
      System.out.println("user is null");
    }

    // Get the users current progress string (JSON format).
    String progressJson = (String) (user.getProperty(CreateProfileServlet.PROGRESS_PROPERTY));

    // Get the users marathon length
    double m = (Double) user.getProperty(CreateProfileServlet.MARATHON_LENGTH_PROPERTY);
    Float marathonLength = (float) m;

    // Should never happen if user is not null.
    if(progressJson == null) {
      progressJson = "[]";
    }

    // Get parameters from progress update and calculate total time.
    Float hours = Float.parseFloat(request.getParameter("hours"));
    Float minutes = Float.parseFloat(request.getParameter("minutes"));
    Float seconds = Float.parseFloat(request.getParameter("seconds"));
    Float totalhours = hours + minutes/((float) 60) + seconds/((float) 3660);

    // Convert progress JSON string into an arraylist of sessions.
    ArrayList<MarathonSession> sessions;
    sessions = gson.fromJson(progressJson, new TypeToken<List<MarathonSession>>(){}.getType());

    // Get the date.
    long timestamp = System.currentTimeMillis();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
    String date = formatter.format(new Date(timestamp));
    
    // Add current session to the list of sessions.
    MarathonSession curSession = new MarathonSession(timestamp, marathonLength/totalhours, date);
    sessions.add(curSession);

    //Convert the sessions back to a JSON string
    progressJson = gson.toJson(sessions);

    // Putting new progress string.
    user.setProperty(CreateProfileServlet.PROGRESS_PROPERTY, progressJson);
    datastore.put(user);

    // Redirect to home.
    RequestDispatcher view = request.getRequestDispatcher("/");
    view.forward(request, response);
  }


  /**
   * equalKeys checks if an email matches the key of a entity.
   * Entity keys are in the format user("key"), so using the 
   * substring gets rid of the 'user("' and '")' at both ends.
   * The complexity of this is O(1)
   *
   * @param email - the email of the current user
   * @param key - the key in the above mentioned format
   * @return if the email matches the key.
   */
  public static boolean equalKeys(String  email, String key) {
    return key.substring(6, key.length()-2).equals(email);
  }

}




