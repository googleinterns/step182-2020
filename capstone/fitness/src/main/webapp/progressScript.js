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

const rightArrow = "<div class=\"col-sm\"><span>&#8594;</span></div>";
const goalStepComplete = "<div class=\"col-sm step\"><p>progress</p></div>";
const goalStepNotComplete = "<div class=\"col-sm step\"><a class=\"btn btn-secondary btn-sm\" href=\"#\" title=\"Goal Step\" data-toggle=\"popover\" data-trigger=\"hover\" data-content=\"progress\">See Goal Step!</a></div>";

$(document).ready(function(){
  $('[data-toggle="popover"]').popover();
});

async function loadProgressModel() {
  const response = await fetch("/pro");
  const progressList = await response.json();
  document.getElementById("model").innerHTML = "";
  for(let i = 0; i < progressList.length; i++) {
    if(progressList[i].complete) {
      document.getElementById("model").innerHTML += goalStepComplete.replace("progress", progressList[i].exerciseString);
    }
    else {
      document.getElementById("model").innerHTML += goalStepNotComplete.replace("progress", progressList[i].exerciseString);
    }
    if(i != progressList.length - 1) {
      document.getElementById("model").innerHTML += rightArrow;
    }
  }
}