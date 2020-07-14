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
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;


@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  final String commentEntity = "Comment";
  final String textProperty = "text";
  final String timestampProperty = "timestamp";
  final String nameProperty = "name";
  final String languageProperty = "language";

  static Gson gson;

  @Override
  public void init() {
    gson = new Gson();
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Query query = new Query(commentEntity).addSort(timestampProperty, SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Loop through the query to get information from each comment.
    List<String> comments = new ArrayList<String>();
    loopThroughComments: 
    for(Entity e : results.asIterable()) {
        String text = e.getKey().toString();
        if(!(text == null)) {
          comments.add(text);
        }
    }
    
    // Convert string to json and output.
    String json = gson.toJson(comments);
    response.setContentType("text/html;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    String text = getParameter(request, "commentBox", "");
    String language = getParameter(request, "languageBox", "en");
    String name = getParameter(request, "nameBox", "");

    long timestamp = System.currentTimeMillis();
    
    // Create a new comment entity and allocate the necessary information.
    Entity comment = new Entity(commentEntity);
    comment.setProperty(textProperty, text);
    comment.setProperty(timestampProperty, timestamp);
    comment.setProperty(nameProperty, name);
    comment.setProperty(languageProperty, language);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(comment);

    response.sendRedirect("/index.html");
  }

  /*
    Returns a certain parameter from a post requests
    if it doesn't exist, it returns the a default value. 
  */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    return value == null ? defaultValue : value;
  }
}

