package com.google.sps.servlets;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

// This servlet  returns whether or not the user is logged in and prompts them to login if they are not.
@WebServlet("/login-status")
public class LoginStatusServlet extends HttpServlet {
  private static final Gson gson = new Gson();
  private static final String redirectUrl = "/";
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String logoutUrl = userService.createLogoutURL(redirectUrl);
      ArrayList<String> userInfo = new ArrayList<String>();
      userInfo.add(userEmail);
      userInfo.add(logoutUrl);
      String jsonInfo = gson.toJson(userInfo);
      response.getWriter().println(jsonInfo);
    } else {
      String loginUrl = userService.createLoginURL(redirectUrl);
      ArrayList<String> urlArray = new ArrayList<String>();
      urlArray.add(loginUrl);
      String jsonArray = gson.toJson(urlArray);
      response.getWriter().println(jsonArray);
    }
  }
}