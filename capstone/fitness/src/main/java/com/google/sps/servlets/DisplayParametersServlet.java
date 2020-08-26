// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.sps.util.DisplayParameters;
import com.google.sps.util.DisplayParameters.Filter;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.*; 

/** Servlet that sets display parameters for GoalStep ordering. */
@WebServlet("/display-param")
public class DisplayParametersServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession();
    DisplayParameters displayParam = (DisplayParameters) session.getAttribute("displayParam");
    if(displayParam == null) {
      displayParam = new DisplayParameters();
      session.setAttribute("displayParam", displayParam);
    }
    response.setContentType("application/json;");
    response.getWriter().println(getJson(displayParam));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession();
    DisplayParameters displayParam = (DisplayParameters) session.getAttribute("displayParam");
    session.setAttribute("displayParam", updateDisplayParameters(displayParam, request));
    response.sendRedirect("/progress.html");
  }

  private String getJson(DisplayParameters displayParam) {
    Gson gson = new Gson();
    String json = gson.toJson(displayParam);
    return json;
  }
  
  /**
  * Creates an updated instance of the goal steps display parameters given the request parameters
  * @param displayParam display parameters to update
  * @param request holds request parameters for specific http request
  */
  private DisplayParameters updateDisplayParameters(DisplayParameters displayParam, HttpServletRequest request) {
    if(displayParam == null) {
      displayParam = new DisplayParameters();
    }
    
    int count = displayParam.getCount();
    Filter filterData = displayParam.getFilter();

    String countString = request.getParameter("count");
    if(countString != null && !countString.isEmpty()) {
      count = Integer.parseInt(countString);
    }
    
    String filter = request.getParameter("filter");
    if(filter != null) {
      for(Filter f : Filter.values()) {
        if(f.name().equalsIgnoreCase(filter)) {
          filterData = f;
        }
      }
    }
    return new DisplayParameters(count, filterData);
  }
}