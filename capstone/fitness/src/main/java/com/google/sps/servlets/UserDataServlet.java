package com.google.sps.servlets;

import com.google.sps.util.DataHandler;
import com.google.sps.util.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.Entity;
import org.json.*;

/**
 This servelet is used to easily retrieve the user's data from datastore 
 in JSON format.
*/
@WebServlet("/get-user-data")
public class UserDataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
    
    Entity user = DataHandler.getUser();

    // The user must be signed in.
    if(user == null) {
      response.setContentType("text/html");
      response.getWriter().println("You must sign in");
      return;
    }

    JSONObject json = new JSONObject();

    for(String property : DataHandler.USER_PROPERTIES) {
      if(DataHandler.isNumber(property)) {
        json.put(property, Float.parseFloat(DataHandler.getUserData(property, user)));
      }
      else {
        json.put(property, DataHandler.getUserData(property, user));    
      }
    }

    json.put("email", DataHandler.getUserEmail());

    response.setContentType("application/json");
    response.getWriter().println(json.toString());
  }
}