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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

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
  private static final Gson gson = new Gson();
  
  final String commentEntity = "Comment";
  final String nameProperty = "name";
  final String textProperty = "text";
  final String timestampProperty = "timestamp";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    List <Comment> theComments = new ArrayList<Comment>();  

    for (Entity entity : results.asIterable()) {
        theComments.add(new Comment(entity.getProperty("name").toString(),entity.getProperty("text").toString(),entity.getProperty("timestamp").toString()));
    }
    response.setContentType("application/json;");
    String jsonText =  gson.toJson(theComments);

    response.getWriter().println(jsonText);
  }
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    
    Comment tComment = new Comment(request.getParameter("name-input"), request.getParameter("comment-input"));

    Entity myComment = new Entity(commentEntity);
    myComment.setProperty(nameProperty, tComment.getName());
    myComment.setProperty(textProperty, tComment.getText());
    myComment.setProperty(timestampProperty, tComment.getTimestamp());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(myComment);

    datastore.put(myComment);

    response.sendRedirect("/comments.html");
  }
}