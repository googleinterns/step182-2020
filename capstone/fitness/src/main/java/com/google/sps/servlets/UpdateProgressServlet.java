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
import com.google.sps.util.DataHelper;
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

  static Gson gson = new Gson();
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    // Display the complete session screen which then sends a POST to this server.
    RequestDispatcher view = request.getRequestDispatcher("complete-session.html");
    view.forward(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

    Entity user=DataHelper.getUser();
    // Redirect if user not found.
    if(user == null) {
      RequestDispatcher view = request.getRequestDispatcher("/");
      view.forward(request, response);
      System.out.println("user is null");
    }

    // Get the users current progress string (JSON format).
    String progressJson = (String) (user.getProperty(DataHelper.PROGRESS_PROPERTY));

    // Get the users marathon length
    double m = (Double) user.getProperty(DataHelper.MARATHON_LENGTH_PROPERTY);
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

    //Convert the sessions back to a JSON string.
    progressJson = gson.toJson(sessions);

    // Update new progress string in datastore.
    user.setProperty(DataHelper.PROGRESS_PROPERTY, progressJson);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(user);

    // Redirect to home.
    RequestDispatcher view = request.getRequestDispatcher("/");
    view.forward(request, response);
  }
}




