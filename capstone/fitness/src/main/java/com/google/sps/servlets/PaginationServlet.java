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
import com.google.sps.util.Metadata;
import com.google.sps.util.Metadata.Filter;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.*; 

/** Servlet that sets metadata for GoalStep ordering. */
@WebServlet("/pagin")
public class PaginationServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession();
    Metadata metadata = (Metadata) session.getAttribute("metadata");
    if(metadata == null) {
      metadata = new Metadata();
      session.setAttribute("metadata", metadata);
    }
    response.setContentType("application/json;");
    response.getWriter().println(getJson(metadata));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession();
    Metadata metadata = (Metadata) session.getAttribute("metadata");
    session.setAttribute("metadata", updateMetadata(metadata, request));
    response.sendRedirect("/progress.html");
  }

  private String getJson(Metadata metadata) {
    Gson gson = new Gson();
    String json = gson.toJson(metadata);
    return json;
  }
  
  /**
  * Creates an updated instance of the goal steps display metadata given the request parameters
  * @param metadata metadata to update
  * @param request holds request parameters for specific http request
  */
  private Metadata updateMetadata(Metadata metadata, HttpServletRequest request) {
    if(metadata == null) {
      metadata = new Metadata();
    }
    
    int count = metadata.getCount();
    int page = metadata.getPage();
    Filter filterData = metadata.getFilter();

    String countString = request.getParameter("count");
    if(countString != null && !countString.isEmpty()) {
      count = Integer.parseInt(countString);
      page = 0;
    }

    String movePage = request.getParameter("move-page");
    if(movePage != null && !movePage.isEmpty()) {
      if(movePage.equals("previous")) {
        if(page != 0) {
          page--;
        }
      }
      else if(movePage.equals("next")) {
        page++;
      }
      else {
        page = Integer.parseInt(movePage);
      }
    }
    
    String filter = request.getParameter("filter");
    if(filter != null) {
      for(Filter f : Filter.values()) {
        if(f.name().equalsIgnoreCase(filter)) {
          filterData = f;
        }
      }
    }
    return new Metadata(count, page, filterData);
  }
}