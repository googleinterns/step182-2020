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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.Calendar;


@WebServlet("/data")
public class DataServlet extends HttpServlet {
  static Gson gson;


  

  private List <Comment> theComments = new ArrayList<Comment>();
  
  public class Comment {
    //   private final Date commentTime = new Date();
      String name;
      String text;
      String timestamp;

      public Comment(String tName, String tText){
          this.name=tName;
          this.text=tText;
          this.timestamp= new Date().toString();

      }
      public String toString(){
          return this.name + " said " + "\n" + this.text + "\n" + " (" + this.timestamp + ")";
      }
  }

//   @Override
//   public void init() {
//     gson = new Gson();
//   }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    String jsonText =  new Gson().toJson(theComments);
    response.getWriter().println(jsonText);
  }
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");

    theComments.add(new Comment(request.getParameter("name-input"), request.getParameter("comment-input")));
    //   for (Comment comment: theComments){
    //     response.getWriter().println(comment);}


    // Send the JSON as the response


    response.sendRedirect("/comments.html");

  }
//   private String convertToJsonUsingGson(Comment comment) {
//     Gson gson = new Gson();
//     String json = gson.toJson(comment);
//     return json;
//     }
}