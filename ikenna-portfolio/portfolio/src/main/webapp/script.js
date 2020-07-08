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

const comment_count = "Comments Per Page: -";
const current_filter = "Current Filter: -"
const page_count = "Page: page_num of page_max";

/**
* Initializes the page with containers and server requests
*/
async function initializePage() {
  loadProjectsContainer();
  loadCalisthenicsContainer();
  loadMetadata().then((metadata) => {
    loadCommentsContainer(metadata);
  });
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
  document.getElementById("cal-image1").src = calisthenics.goalImg;
  document.getElementById("cal-image2").src = calisthenics.currentAblityImg;
  document.getElementById("goal").innerText = goal.replace("-", calisthenics.goalName);
  document.getElementById("current").innerText = current.replace("-", calisthenics.currentAblityName);
}

async function loadMetadata() {
  const mresponse = await fetch("/count")
  const metadata = await mresponse.json();
  await addMetadata(metadata);  
  return await metadata;
}

async function loadCommentsContainer(metadata) {
  const dresponse = await fetch("/data");
  const comments = await dresponse.json(); 
  let msg = "";
  document.getElementById("comment-count").innerText = comment_count.replace("-", metadata.count);
  document.getElementById("filter-label").innerText = current_filter.replace("-", metadata.filterLabel).replace("_", " ").replace("_", "-");
  document.getElementById("page-count").innerText = page_count.replace("page_num", (metadata.page + 1)).replace("page_max", metadata.maxPages); 
  for(comment of comments) {
    if(comment.name === "") continue;
    msg += comment_item.replace("timestamp", new Date(comment.timestamp)).replace("comment_id", comment.id).replace("comment_name", comment.name).replace("comment_text", comment.text);
  }
  document.getElementById("comments").innerHTML = msg;
}

async function addMetadata(metadata) {
  const params = new URLSearchParams();
  params.append("count", metadata.count);
  params.append("page", metadata.page);
  params.append("filterLabel", metadata.filterLabel);
  await fetch("/data", {method: "POST", body: params});
}

/**
* Adds a random quote to the page.
*/
async function addRandomQuote() {
  const response = await fetch("/random");
  const quote = await response.text();
  document.getElementById("quote-container").innerText = quote;
}
