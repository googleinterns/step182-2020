package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  private String redirectUrl; 

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    response.setContentType("application/json");

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String redirectUrl = "/";
      String logoutUrl = userService.createLogoutURL(redirectUrl);

      // JSON string that will be read by JavaScript.
      String output = "{\"email\": \""+ userEmail +"\",\"url\": \""+logoutUrl+"\"}";
      response.getWriter().println(output);
    } 
    else {
      String redirectUrl = "/create-profile";
      String loginUrl = userService.createLoginURL(redirectUrl);
      
      // JSON string that will be read by JavaScript.
      String output = "{\"email\": \"stranger\",\"url\": \""+loginUrl+"\"}";
      response.getWriter().println(output);
    }
  }
}
