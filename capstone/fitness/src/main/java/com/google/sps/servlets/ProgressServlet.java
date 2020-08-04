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
import com.google.sps.util.DataHandler;
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

@WebServlet("/progress")
public class ProgressServlet extends HttpServlet {

  static Gson gson = new Gson();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    Entity user=DataHandler.getUser();

    // Get the users progress from datastore
    String progressString = (String) user.getProperty(DataHandler.PROGRESS_PROPERTY);

    //Give the output in JSON format
    response.setContentType("application/json");
    response.getWriter().println(progressString);
  }

    @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

    Entity user=DataHandler.getUser();

    // TODO(gabrieldg@) change to throw exception.
    // Redirect if user not found.
    if(user == null) {
      RequestDispatcher view = request.getRequestDispatcher("/");
      view.forward(request, response);
    }

    // Get the users current progress string (JSON format).
    String progressJson = (String) (user.getProperty(DataHandler.PROGRESS_PROPERTY));

    // Get the users marathon length.
    float marathonLength = (float) (double) user.getProperty(DataHandler.MARATHON_LENGTH_PROPERTY);

    // Should never happen if user is not null.
    // TODO(@gabrieldg) throw actual exception.
    if(progressJson == null) {
      progressJson = "[]";
    }

    // Get parameters from progress update and calculate total time.
    float hours = Float.parseFloat(request.getParameter("hours"));
    float minutes = Float.parseFloat(request.getParameter("minutes"));
    float seconds = Float.parseFloat(request.getParameter("seconds"));
    float totalhours = hours + minutes/((float) 60) + seconds/((float) 3660);

    // Convert progress JSON string into an arraylist of sessions.
    ArrayList<MarathonSession> sessions = gson.fromJson(progressJson, new TypeToken<List<MarathonSession>>(){}.getType());

    // Get the date.
    long timestamp = System.currentTimeMillis();
    String date = DataHandler.getDate(timestamp);
    
    // Add current session to the list of sessions.
    MarathonSession curSession = new MarathonSession(timestamp, marathonLength/totalhours, date);
    sessions.add(curSession);

    //Convert the sessions back to a JSON string.
    progressJson = gson.toJson(sessions);

    // Update new progress string in datastore.
    user.setProperty(DataHandler.PROGRESS_PROPERTY, progressJson);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(user);

    // Redirect to home.
    RequestDispatcher view = request.getRequestDispatcher("/");
    view.forward(request, response);
  }
}
