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
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns a random quote. */
@WebServlet("/projects")
public final class ProjectsServlet extends HttpServlet {

  private Project project;

  @Override
  public void init() {
    project = new Project("CycleGAN", "/images/real.png", "/images/real_to_rot.png", "/images/rot_to_real.png", "rada0", "rada0");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    response.getWriter().println(getJson());
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String type = request.getParameter("pro");
    if(type != null) {
      if(type.equals("cyclegan")) project = new Project("CycleGAN", "/images/real.png", "/images/real_to_rot.png", "/images/rot_to_real.png", "rada0", "rada0");
      else if(type.equals("deep-photo")) project = new Project("Deep Photo Style Transfer", "/images/dancing.jpg", "/images/picasso.jpg", "/images/Figure_1.png", "rada1", "rada1");
      else if(type.equals("msg-sys")) project = new Project("Message System", "", "/images/Message Bus.png", "", "rada2", "rada2");
    }
    response.sendRedirect("/index.html#projects-sect");
  }

  private String getJson() {
    Gson gson = new Gson();
    String json = gson.toJson(project);
    return json;
  }
}
