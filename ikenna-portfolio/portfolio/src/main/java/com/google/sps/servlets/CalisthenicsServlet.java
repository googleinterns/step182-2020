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
@WebServlet("/calisthenics")
public final class CalisthenicsServlet extends HttpServlet {

  private Calisthenics calisthenics;

  @Override
  public void init() {
    calisthenics = new Calisthenics("/images/vsit.jpg", "/images/lsit.jpg", "V Sit", "L Sit");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    response.getWriter().println(getJson());
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String type = request.getParameter("cal");
    if(type != null) {
      if(type.equals("vs")) calisthenics = new Calisthenics("/images/vsit.jpg", "/images/lsit.jpg", "V Sit", "L Sit");
      else if(type.equals("bl")) calisthenics = new Calisthenics("/images/back_lever.jpg", "/images/adv_tuck_back_lever.jpg", "Back Lever", "Advanced Tuck Back Lever");
      else if(type.equals("ps")) calisthenics = new Calisthenics("/images/pistol_squat.jpg", "/images/supported_pistol_squat.jpg", "Pistol Squat", "Pole Supported Pistol Squat");
      else if(type.equals("fl")) calisthenics = new Calisthenics("/images/front_lever.jpg", "/images/single_leg_front_lever.png", "Front Lever", "Single Leg Front Lever");
      else if(type.equals("hspu")) calisthenics = new Calisthenics("/images/hspu.jpg", "/images/decline_pike_push_up.jpg", "Freestanding Handstand Push Up", "Decline Pike Push Up");
    }
    response.sendRedirect("/index.html#calisthenics-sect");
  }

  private String getJson() {
    Gson gson = new Gson();
    String json = gson.toJson(calisthenics);
    return json;
  }
}
