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
import com.google.sps.data.*;
import com.google.sps.database.CommentDatabase;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.*; 

/** Servlet that returns JSON content. */
@WebServlet("/count")
public class CountServlet extends HttpServlet {

  public static int count = 10;
  public static int page = 0;
  public static boolean ascending = true;
  public static String search = "timestamp";
  
  private CommentDatabase database;
  
  @Override
  public void init() {
    database = new CommentDatabase();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Metadata metadata = new Metadata(count, page, database.getMaxPages(count), ascending, search);
    response.setContentType("application/json;");
    response.getWriter().println(getJson(metadata));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String count_string = request.getParameter("count");
    if(!count_string.equals(""))
      count = Integer.parseInt(count_string);
    String move_page = request.getParameter("move-page");
    if(move_page != null) {
        if(move_page.equals("left") && page != 0) page--;
        else if(move_page.equals("right") && page != database.getMaxPages(count) - 1) page++;
    }
    response.sendRedirect("/index.html#comments-sect");
  }

  private String getJson(Metadata metadata) {
    Gson gson = new Gson();
    String json = gson.toJson(metadata);
    return json;
  }
}
