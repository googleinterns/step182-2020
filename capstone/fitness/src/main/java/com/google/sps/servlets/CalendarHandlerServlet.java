package com.google.sps.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/calendar-handler")
public class CalendarHandlerServlet extends HttpServlet { 
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
    response.setContentType("text/html;");
    response.getWriter().println("We Made It!");
  
  }
}
