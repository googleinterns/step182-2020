package com.google.sps.servlets;

import com.google.sps.util.DataHandler;
import com.google.sps.util.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.Entity;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/get-data")
public class GetDataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      Entity user = DataHandler.getUser();
      String Json = "{";
      int counter = 0;
      int PROPERTIESLength = DataHandler.PROPERTIES.size();
      // Loop to build data JSON string
      // The order of the properties is random since they are stored in a hashset
      // but since they are accessed using json.property, the order does not matter. 
      for(String property : DataHandler.PROPERTIES)
      {
        Json += DataHandler.Jsonfy(property,
                                     DataHandler.getData(property, user), DataHandler.isNumber(property));
        if(counter == PROPERTIESLength - 1)
          Json += "}";
        else
          Json += ","; 
        counter++;
      }

      response.setContentType("application/json");
      response.getWriter().println(Json);
    }
    catch(Exception e) {
      response.setContentType("text/html");
      response.getWriter().println("something went wrong");
    }
  }
}
