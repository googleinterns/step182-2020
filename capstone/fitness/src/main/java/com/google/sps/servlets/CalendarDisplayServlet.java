package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.sps.util.*;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


// CalendarDisplayServlet sends the correct information to calendar.html to display the calendar and the workout. 
@WebServlet("/cal-display")
public class CalendarDisplayServlet extends HttpServlet { 
  Gson gson = new Gson();
  String eventsJson;
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {     
    response.sendRedirect("/calendar.html");
    eventsJson = (String)request.getAttribute("events");

  }
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    response.setContentType("application/json");
    response.getWriter().println(eventsJson);
}
>>>>>>> eventId-branch
}