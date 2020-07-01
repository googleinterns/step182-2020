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
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns JSON content. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  public static List<Comment> comments;

  @Override
  public void init() {
    comments = new ArrayList<>();
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if(comments.isEmpty()) {
      Query query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);
      for (Entity entity : results.asIterable()) {
        String name = (String) entity.getProperty("name");
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");
        Comment comment = new Comment(name, text, timestamp);
        comments.add(comment);
      }
      if(comments.isEmpty()) comments.add(new Comment("", "", 0)); // Fixes situation where database is empty
    }

    Gson gson = new Gson();
    String json = gson.toJson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("name-box");
    String text = request.getParameter("text-box");
    long timestamp = System.currentTimeMillis();
    boolean empty_name = name == null || name.equals("");
    String true_name = name.replaceAll("<[^>]*>", "Please Don't Inject HTML");
    String true_text = text.replaceAll("<[^>]*>", "Please Don't Inject HTML");
    Comment comment = empty_name ? new Comment(true_text, timestamp) : new Comment(true_name, true_text, timestamp);

    Entity comment_entity = new Entity("Comment");
    comment_entity.setProperty("name", comment.getName());
    comment_entity.setProperty("text", comment.getText());
    comment_entity.setProperty("timestamp", comment.getTimestamp());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
    datastore.put(comment_entity);
    comments.add(comment);
    response.sendRedirect("/index.html");
  }
}
