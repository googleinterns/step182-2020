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

const project_name = "<small>Project Showing: -</small>";

const goal = "Goal: -";
const current = "Current Ability: -";

const comment_item = "<li class=\"media\"><div class=\"media-body\"><button name=\"delete-comment\" class=\"pull-right btn btn-secondary\" value=\"comment_id\">x</button><small class=\"pull-right\">timestamp</small><strong class=\"pull-left\">comment_name</strong><br><br><p class=\"desc\" align=\"left\">comment_text</p></div></li>";

const comment_count = "<small>Comments Per Page: -</small>";
const current_filter = "<small>Current Filter: -</small>"
const page_count = "<small>Page: page_num-page_max</small>";

/**
* Initializes the page with containers and server requests
*/
async function initializePage() {
  loadProjectsContainer();
  loadCalisthenicsContainer();
  loadCommentsContainer();
}

async function loadProjectsContainer() {
  const response = await fetch("/projects");
  const project = await response.json();
  document.getElementById("project-name").innerHTML = project_name.replace("-", project.name);
  document.getElementById("projects-pics").innerHTML = project.imageHTML;
  document.getElementById("pro-desc").innerHTML = project.desc;
  document.getElementById("pro-links").innerHTML = project.links;
}

async function loadCalisthenicsContainer() {
  const response = await fetch("/calisthenics");
  const calisthenics = await response.json();
  document.getElementById("cal-image1").src = calisthenics.img1;
  document.getElementById("cal-image2").src = calisthenics.img2;
  document.getElementById("goal").innerText = goal.replace("-", calisthenics.title1);
  document.getElementById("current").innerText = current.replace("-", calisthenics.title2);
}

async function loadCommentsContainer() {
  const dresponse = await fetch("/data");
  const comments = await dresponse.json();
  const mresponse = await fetch("/count")
  const metadata = await mresponse.json(); 
  let msg = "";
  document.getElementById('comment-count').innerHTML = comment_count.replace("-", metadata.count);
  document.getElementById('filter-label').innerHTML = current_filter.replace("-", getFilter(metadata.ascending, metadata.search));
  document.getElementById('page-count').innerHTML = page_count.replace("page_num", (metadata.page + 1)).replace("page_max", metadata.max_pages); 
  for(comment of comments) {
    if(comment.name === "") continue;
    msg += comment_item.replace("timestamp", new Date(comment.timestamp)).replace("comment_id", comment.id).replace("comment_name", comment.name).replace("comment_text", comment.text);
  }
  document.getElementById("comments").innerHTML = msg;
}

function getFilter(ascending, search) {
  if(ascending) {
    if(search === "timestamp") return "Oldest";
    if(search === "name") return "Name A-Z";
  }
  else {
    if(search === "timestamp") return "Newest";
    if(search === "name") return "Name Z-A"; 
  }
}

/**
* Adds a random quote to the page.
*/
async function addRandomQuote() {
  const response = await fetch("/random");
  const quote = await response.text();
  document.getElementById("quote-container").innerText = quote;
}
