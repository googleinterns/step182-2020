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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.DateTime;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.sps.fit.*;
import com.google.sps.util.*;
import com.google.sps.progress.*;
import com.google.sps.fit.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;   
import java.util.*;

// CalendarDisplayServlet sends the correct information to calendar.html to display the calendar and the workout. 
@WebServlet("/cal-display")
public class CalendarDisplayServlet extends HttpServlet { 
  Gson gson = new Gson();
  String someName;
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {     

    response.sendRedirect("/calendar.html");
    someName = (String)request.getAttribute("events");
    System.out.println("EVENTS : " + someName);

  }
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    response.setContentType("application/json");

    System.out.println("FROM POST:" + someName);
    String id = DataHandler.getUserData("calendarId", DataHandler.getUser());
    
    // String events = DataHandler.getUserData("EventIds", DataHandler.getUser());
    // List<String> eventArray = gson.fromJson(events, new TypeToken<List<String>>(){}.getType());

    // eventArray.add(0, id);

    response.getWriter().println(gson.toJson(eventArray));
}
}