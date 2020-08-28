package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.sps.util.*;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


// Servlet sends calendarId. 
@WebServlet("/embed")
public class EmbedCalendarServlet extends HttpServlet { 
  Gson gson = new Gson();
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {     
    response.setContentType("application/json");
    String id = DataHandler.getUserData("calendarId", DataHandler.getUser());
    response.getWriter().println(gson.toJson(id));
  }
}