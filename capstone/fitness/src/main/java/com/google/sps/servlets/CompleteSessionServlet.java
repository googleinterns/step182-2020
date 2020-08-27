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
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/complete-session")
public class CompleteSessionServlet extends HttpServlet { 

  static Gson gson = new Gson();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    HttpSession session = request.getSession();
    String workoutName = (String) session.getAttribute("workoutName");
    Entity workout = DataHandler.getWorkout(workoutName);

    String type = DataHandler.getWorkoutData(DataHandler.TYPE_PROPERTY, workout);

    if(type.equals("lifting")) {
      response.sendRedirect("complete-lifting-session.html");
    }
    else {
      response.sendRedirect("complete-marathon-session.html");
    }

  }
}
