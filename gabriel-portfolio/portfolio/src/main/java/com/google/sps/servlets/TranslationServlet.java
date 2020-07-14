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
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.HashMap;

@WebServlet("/translate")
public class TranslationServlet extends HttpServlet {

  static String languageCode;
  static String commentKey;

  final static String commentEntity = "Comment";
  final static String textProperty = "text";
  final static String timestampProperty = "timestamp";
  final static String nameProperty = "name";
  final static String languageProperty = "language";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Return a buffered reader with the body of a request
    BufferedReader br = request.getReader();
    String body = br.readLine();

    // LanguageCode will always be 2 characters 
    //and at the beginning of the string
    languageCode = body.substring(0,2);
    commentKey = body.substring(2);
    
    // Set up datastore
    Query query = new Query(commentEntity).addSort(timestampProperty, SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Data that we are getting for each comment
    String comment = null;
    String name = null;
    String language = null;
    long timestamp = 0;

    // Get the data from datastore
    loopThroughComments: 
    for(Entity e : results.asIterable()) {
        String key = e.getKey().toString();
        if(!(key == null) && key.equals(commentKey)) {
          comment = (String) e.getProperty(textProperty);
          name = (String) e.getProperty(nameProperty);
          timestamp = (long) e.getProperty(timestampProperty);
          language = (String) e.getProperty(languageProperty);
          break loopThroughComments;
        }
    }

    // Do the translation.
    System.out.println("Started translation");
    System.out.println("Tranlating to :" + languageCode);
    Translate translate = TranslateOptions.getDefaultInstance().getService();
    // Translation translation =
    //     translate.translate(comment, Translate.TranslateOption.targetLanguage(languageCode));
    // String translatedText = translation.getTranslatedText();
    System.out.println("ended translation");

    // Output the translation.
    response.setContentType("text/html; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(comment);
    response.getWriter().println(name);
    response.getWriter().println(language);
    response.getWriter().println(timestamp);
  }
}


