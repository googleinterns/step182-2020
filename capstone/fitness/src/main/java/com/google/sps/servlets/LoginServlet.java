package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.json.*;



@WebServlet("/login")
public class LoginServlet extends HttpServlet { 

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    response.setContentType("application/json");

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String redirectUrl = "/index.html";
      String logoutUrl = userService.createLogoutURL(redirectUrl);

      JSONObject json = new JSONObject();
      json.put("email", userEmail);
      json.put("url", logoutUrl);
      response.getWriter().println(json.toString());
    } 
    else {
      String redirectUrl = "/create-profile";
      String loginUrl = userService.createLoginURL(redirectUrl);
      
      JSONObject json = new JSONObject();
      json.put("email", "stranger");
      json.put("url", loginUrl);
      response.getWriter().println(json.toString());
    }
  }
}
