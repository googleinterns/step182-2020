package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* ViewWorkoutServlet will show the user the progress in the workout 
* as well as the ability to complete sessions.
* TODO(@gabrieldg @ijelue) integrate all components of our UIs
*/
@WebServlet("/view-workout")
public class ViewWorkoutServlet extends HttpServlet { 

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    response.setContentType("text/html");
    response.getWriter().println(request.getParameter("selectWorkout"));
  }
}
