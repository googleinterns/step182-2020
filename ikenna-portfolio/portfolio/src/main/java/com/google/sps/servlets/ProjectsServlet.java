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
  private final String[] project_desc = new String[] {
    "The CycleGAN is a deep learning model that uses two generative adversarial networks (GAN) and the concept of cycle " +
    "consistency to turn something from one domain to another domain. For example, if I were to say \"My name is Ikenna Elue\" " +
    "into a CycleGan that turns language into Spanish, then it would output \"Me llamo Ikenna Elue\". Two perks of this model " +
    "that make it stand out from the rest is that, using the example model described earlier, the model can only output in " +
    "a 1:1 fashion (i.e. can’t say \"Mi nombre es Ikenna Elue\") and if we put the Spanish back through the model it would " +
    "become \"My name is Ikenna Elue\". For this particular CycleGAN, we (my partner and I did this for a school project to " +
    "reproduce a paper) turned images of clothing in the right side up domain into clothing of the rotated domain. The " +
    "results are pictured above. From left to right respectively we have the original image, the generated image in the " +
    "rotated domain, and the generated image in the right side up domain using the rotated domain image. <b>You can tell it\'s " +
    "a generated image based on the noisiness of the image</b>",
    
    "Deep photo style transfer is an extension to the deep learning model Neural Style. Neural Style takes a content and style " +
    "image as inputs and attempts to generate the content image in the style of the style image. One shortcoming of this model " +
    "is that any generated image looks like a painting. Deep photo attempts to remedy this problem by using masks, laplacian " +
    "matting, an extra loss function (see paper for more info). So far, my partner and I (this was a school project), have " +
    "implemented Neural Style using the help of the tutorial found below. Doing such has yielded the result pictured above " +
    "where from left to right is the content image, the style image, and the created image. Our progress on the deep photo " +
    "portion of the project is technically done, but it looks terrible and was made using code we didn’t really understand (the " +
    "original codebase was made in lua whereas we were used to python deep learning projects). Going forward, we hope to " +
    "implement the model fully and integrate it into a web application.",
        
    "Although this project hasn’t been worked on for a while for reasons I’ll explain later, I still think it’s worth sharing. " +
    "This project attempts to create a game engine that removes all dependencies from systems to make it multithreaded and modular. " +
    "Although I technically succeeded in doing such I found out that based on the way opengl (a graphics library) renders its data, " +
    "I am not allowed to render across multiple threads. The fix for this would be a synchronization at the end of each step of " +
    "the the game state, but that seemed like an unrewarding solution. So, to this day, I am thinking of better solutions/trying to " +
    "see how I can abstract the messaging system away from the game engine to make it easier to implement everything else. The " +
    "abstraction would also allow me to implement other projects I might want multithreaded such as a genetic algorithm builder " +
    "or neural net generator."
  };

  private final String[] links = new String[] {
    "Here's a link to the <a href=\"https://arxiv.org/pdf/1703.10593.pdf\">paper</a>.",

    "Here's a link to the <a href=\"https://arxiv.org/pdf/1703.07511.pdf\">paper</a>.<br>" +
    "Here's a link to the pytorch <a href=\"https://pytorch.org/tutorials/advanced/neural_style_tutorial.html\">tutorial</a>.", 
      
    "Here's a link to the <a href=\"https://www.gamasutra.com/blogs/MichaelKissner/20151027/257369/Writing_a_Game_Engine_from_Scratch__Part_1_Messaging.php\">site</a> explaining the message system further."
  };


  @Override
  public void init() {
    project = new Project("CycleGAN", "/images/real.png", "/images/real_to_rot.png", "/images/rot_to_real.png", project_desc[0], links[0]);
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
      if(type.equals("cyclegan")) project = new Project("CycleGAN", "/images/real.png", "/images/real_to_rot.png", "/images/rot_to_real.png", project_desc[0], links[0]);
      else if(type.equals("deep-photo")) project = new Project("Deep Photo Style Transfer", "/images/dancing.jpg", "/images/picasso.jpg", "/images/Figure_1.png", project_desc[1], links[1]);
      else if(type.equals("msg-sys")) project = new Project("Message System", "", "/images/Message Bus.png", "", project_desc[2], links[2]);
    }
    response.sendRedirect("/index.html#projects-sect");
  }

  private String getJson() {
    Gson gson = new Gson();
    String json = gson.toJson(project);
    return json;
  }
}
