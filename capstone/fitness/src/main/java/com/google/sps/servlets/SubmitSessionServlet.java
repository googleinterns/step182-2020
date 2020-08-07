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
import com.google.sps.fit.*;
import com.google.sps.fit.Exercise.SetType;
import com.google.sps.progress.*;
import com.google.sps.util.*;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/subsess")
public class SubmitSessionServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    float duration1 = convertToFloat(request.getParameter("duration1"));
    float duration2 = convertToFloat(request.getParameter("duration2"));
    float distance1 = convertToFloat(request.getParameter("distance1"));
    float distance2 = convertToFloat(request.getParameter("distance2"));

    HttpSession session = request.getSession();
    session.setAttribute("lastSession", generateSession(duration1, duration2, distance1, distance2));
    response.sendRedirect("/progress.html");
  }

  private float convertToFloat(String parameter) {
    return parameter.isEmpty() ? 0 : Float.parseFloat(parameter);
  }

  private Session generateSession(float duration1, float duration2, float distance1, float distance2) {
    if(duration1 == 0 || distance1 == 0) {
      return null;
    }

    if(duration2 == 0 || distance2 == 0) {
      return new Session(
        new Exercise[] {
          new Exercise.Builder("test")
              .addSetTypeWithValues(SetType.DISTANCE, new float[] {distance1})
              .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {duration1})
              .build()}); 
    }
    return new Session(
      new Exercise[] {
        new Exercise.Builder("test")
            .addSetTypeWithValues(SetType.DISTANCE, new float[] {distance1, distance2})
            .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {duration1, duration2})
            .build()});
  }
}
