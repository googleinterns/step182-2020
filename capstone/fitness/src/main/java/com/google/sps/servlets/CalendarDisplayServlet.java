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

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    response.setContentType("application/json");

    String id = DataHandler.getUserData("calendarId", DataHandler.getUser());
    
    String events = DataHandler.getUserData("EventIds", DataHandler.getUser());
    List<String> eventArray = gson.fromJson(events, new TypeToken<List<String>>(){}.getType());

    eventArray.add(0, id);

    response.getWriter().println(gson.toJson(eventArray));
  }
}