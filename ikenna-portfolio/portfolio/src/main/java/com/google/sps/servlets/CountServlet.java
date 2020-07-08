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
import com.google.sps.data.Metadata;
import com.google.sps.data.Metadata.Sort;
import com.google.sps.database.CommentDatabase;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.*; 

/** Servlet that returns JSON content. */
@WebServlet("/count")
public class CountServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession();
    Metadata metadata = (Metadata) session.getAttribute("metadata");
    if(metadata == null) {
      metadata = new Metadata();
    }
    response.setContentType("application/json;");
    response.getWriter().println(getJson(metadata));
  }

  
  /**
   * This method creates metadata from the comments container
   * that describes how the comments in the comments container
   * should be displayed.
   * @param request 
   * @param response 
  */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    CommentDatabase database = new CommentDatabase();
    HttpSession session = request.getSession();
    
    Metadata metadata = (Metadata) session.getAttribute("metadata");
    if(metadata == null) {
      metadata = new Metadata();
    }
    
    int count = metadata.getCount();
    int page = metadata.getPage();
    Sort sortData = metadata.getSort();

    String countString = request.getParameter("count");
    if(!countString.equals("")) {
      count = Integer.parseInt(countString);
      page = 0;
    }

    String movePage = request.getParameter("move-page");
    if(movePage != null) {
      if(movePage.equals("left") && page != 0) {
        page--;
      }
      else if(movePage.equals("right") && page != database.getMaxPages(count) - 1) {
        page++;
      }
    }
    
    String sorting = request.getParameter("sorting");
    if(sorting != null) {
      for(Sort s : Sort.values()) {
        if(s.name().equalsIgnoreCase(sorting)) {
          sortData = s;
        }
      }
    }
    
    session.setAttribute("metadata", new Metadata(count, page, database.getMaxPages(count), sortData));
    response.sendRedirect("/index.html#comments-sect");
  }

  private String getJson(Metadata metadata) {
    Gson gson = new Gson();
    String json = gson.toJson(metadata);
    return json;
  }
}
