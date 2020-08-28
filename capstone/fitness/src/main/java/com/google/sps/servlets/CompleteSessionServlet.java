package com.google.sps.servlets;

import com.google.sps.util.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import java.io.BufferedReader;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/complete-session")
public class CompleteSessionServlet extends HttpServlet { 

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