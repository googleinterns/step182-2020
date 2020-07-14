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
import com.google.sps.data.User;
import com.google.sps.login.WebLogin;
import com.google.sps.database.CommentDatabase;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.*; 

/** Servlet that handles login information. */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  
  private final String[] admins = new String[] {"ijelue@google.com"};
  private final String redirect = "/index.html";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    WebLogin login = new WebLogin();
    HttpSession session = request.getSession();
    User user = (User) session.getAttribute("user");
    String userEmail = login.getEmail();
    
    if(user == null || !user.getEmail().equals(userEmail)) {
      user = new User(userEmail, isAdmin(userEmail));
    }

    session.setAttribute("user", user);
    response.setContentType("application/json;");
    response.getWriter().println(getJson(user));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    WebLogin login = new WebLogin();
    String url = login.getLoginRedirect(redirect);
    response.sendRedirect(url);
  }

  private String getJson(User user) {
    Gson gson = new Gson();
    String json = gson.toJson(user);
    return json;
  }

  private boolean isAdmin(String userEmail) {
    for(String admin : admins) {
      if(userEmail.equals(admin)) {
        return true;
      }
    }
    return false;
  }
}
