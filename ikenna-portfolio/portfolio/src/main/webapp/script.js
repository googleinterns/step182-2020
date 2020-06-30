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

const project_desc = [
  "The CycleGAN is a deep learning model that uses two generative adversarial networks (GAN) and the concept of cycle \
  consistency to turn something from one domain to another domain. For example, if I were to say \"My name is Ikenna Elue\" \
  into a CycleGan that turns language into Spanish, then it would output \"Me llamo Ikenna Elue\". Two perks of this model \
  that make it stand out from the rest is that, using the example model described earlier, the model can only output in \
  a 1:1 fashion (i.e. can’t say \"Mi nombre es Ikenna Elue\") and if we put the Spanish back through the model it would \
  become \"My name is Ikenna Elue\". For this particular CycleGAN, we (my partner and I did this for a school project to \
  reproduce a paper) turned images of clothing in the right side up domain into clothing of the rotated domain. The \
  results are pictured above. From left to right respectively we have the original image, the generated image in the \
  rotated domain, and the generated image in the right side up domain using the rotated domain image. <b>You can tell it\'s \
  a generated image based on the noisiness of the image</b>",
        
  "Deep photo style transfer is an extension to the deep learning model Neural Style. Neural Style takes a content and style \
  image as inputs and attempts to generate the content image in the style of the style image. One shortcoming of this model \
  is that any generated image looks like a painting. Deep photo attempts to remedy this problem by using masks, laplacian \
  matting, an extra loss function (see paper for more info). So far, my partner and I (this was a school project), have \
  implemented Neural Style using the help of the tutorial found below. Doing such has yielded the result pictured above \
  where from left to right is the content image, the style image, and the created image. Our progress on the deep photo \
  portion of the project is technically done, but it looks terrible and was made using code we didn’t really understand (the \
  original codebase was made in lua whereas we were used to python deep learning projects). Going forward, we hope to \
  implement the model fully and integrate it into a web application.",
        
  "Although this project hasn’t been worked on for a while for reasons I’ll explain later, I still think it’s worth sharing. \
  This project attempts to create a game engine that removes all dependencies from systems to make it multithreaded and modular. \
  Although I technically succeeded in doing such I found out that based on the way opengl (a graphics library) renders its data, \
  I am not allowed to render across multiple threads. The fix for this would be a synchronization at the end of each step of \
  the the game state, but that seemed like an unrewarding solution. So, to this day, I am thinking of better solutions/trying to \
  see how I can abstract the messaging system away from the game engine to make it easier to implement everything else. The \
  abstraction would also allow me to implement other projects I might want multithreaded such as a genetic algorithm builder \
  or neural net generator."
];

const links = [
  "Here's a link to the <a href=\"https://arxiv.org/pdf/1703.10593.pdf\">paper</a>.",

  "Here's a link to the <a href=\"https://arxiv.org/pdf/1703.07511.pdf\">paper</a>.<br> \
  Here's a link to the pytorch <a href=\"https://pytorch.org/tutorials/advanced/neural_style_tutorial.html\">tutorial</a>.", 
      
  "Here's a link to the <a href=\"https://www.gamasutra.com/blogs/MichaelKissner/20151027/257369/Writing_a_Game_Engine_from_Scratch__Part_1_Messaging.php\">site</a> explaining the message system further."
];

const goal = "Goal: -";
const current = "Current Ability: -";

/**
* Initializes the page with containers and server requests
*/
async function initializePage() {
  addComment();
  loadInteractiveContainers();
}

/**
* Changes Projects and Calisthenics Containers to their default states
*/
function loadInteractiveContainers() {
  changeCycleGAN();
  changeVSit()
}

async function addComment() {
  const response = await fetch('/data');
  const comments = await response.json();
  let msg = "";
  console.log(comments);
  for(comment of comments) {
    if(comment.name === "") continue;
    msg += "<p>User: " + comment.name + "<br>" + comment.comment + "</p>";
  }
  document.getElementById('comments').innerHTML = msg;
}

/**
* Adds a random quote to the page.
*/
async function addRandomQuote() {
  const response = await fetch('/random');
  const quote = await response.text();
  document.getElementById('quote-container').innerText = quote;
}

/**
* Changes Projects Container to CycleGAN state
*/
function changeCycleGAN() {
  document.getElementById("pro-image1").src = "/images/real.png";
  document.getElementById("pro-image2").src = "/images/real_to_rot.png";
  document.getElementById("pro-image3").src = "/images/rot_to_real.png";
  document.getElementById("pro-desc").innerHTML = project_desc[0];
  document.getElementById("pro-links").innerHTML = links[0];
}

/**
* Changes Projects Container to Deep Photo Style Transfer state
*/
function changeDeepPhoto() {
  document.getElementById("pro-image1").src = "/images/dancing.jpg";
  document.getElementById("pro-image2").src = "/images/picasso.jpg";
  document.getElementById("pro-image3").src = "/images/Figure_1.png";
  document.getElementById("pro-desc").innerHTML = project_desc[1];
  document.getElementById("pro-links").innerHTML = links[1];
}

/**
* Changes Projects Container to Message System state
*/
function changeMsgSys() {
  document.getElementById("pro-image1").src = "";
  document.getElementById("pro-image2").src = "/images/Message Bus.png";
  document.getElementById("pro-image3").src = "";
  document.getElementById("pro-desc").innerHTML = project_desc[2];
  document.getElementById("pro-links").innerHTML = links[2];
}

/**
* Changes Calisthenics Container to V Sit state
*/
function changeVSit() {
  document.getElementById("cal-image1").src = "/images/vsit.jpg";
  document.getElementById("cal-image2").src = "/images/lsit.jpg";
  document.getElementById("goal").innerText = goal.replace("-", "V Sit");
  document.getElementById("current").innerText = current.replace("-", "L Sit");
}

/**
* Changes Calisthenics Container to Back Lever state
*/
function changeBackLever() {
  document.getElementById("cal-image1").src = "/images/back_lever.jpg";
  document.getElementById("cal-image2").src = "/images/adv_tuck_back_lever.jpg";
  document.getElementById("goal").innerText = goal.replace("-", "Back Lever");
  document.getElementById("current").innerText = current.replace("-", "Advanced Tuck Back Lever");
}

/**
* Changes Calisthenics Container to Pistol Squat state
*/
function changePistolSquat() {
  document.getElementById("cal-image1").src = "/images/pistol_squat.jpg";
  document.getElementById("cal-image2").src = "/images/supported_pistol_squat.jpg";
  document.getElementById("goal").innerText = goal.replace("-", "Pistol Squat");
  document.getElementById("current").innerText = current.replace("-", "Pole Supported Pistol Squat");
}

/**
* Changes Calisthenics Container to Front Lever state
*/
function changeFrontLever() {
  document.getElementById("cal-image1").src = "/images/front_lever.jpg";
  document.getElementById("cal-image2").src = "/images/single_leg_front_lever.png";
  document.getElementById("goal").innerText = goal.replace("-", "Front Lever");
  document.getElementById("current").innerText = current.replace("-", "Single Leg Front Lever");
}

/**
* Changes Calisthenics Container to Freestanding Handstand Push Up state
*/
function changeHSPU() {
  document.getElementById("cal-image1").src = "/images/hspu.jpg";
  document.getElementById("cal-image2").src = "/images/decline_pike_push_up.jpg";
  document.getElementById("goal").innerText = goal.replace("-", "Freestanding Handstand Push Up");
  document.getElementById("current").innerText = current.replace("-", "Decline Pike Push Up");
}
