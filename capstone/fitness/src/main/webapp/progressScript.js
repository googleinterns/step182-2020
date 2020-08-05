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
const stepMessage = "Step number";
const goalStep = "<div class=\"col-sm step\"><button title=\"stepMessage\" type=\"button\" class=\"btn buttonType btn-sm\" data-container=\"body\" data-toggle=\"popover\" data-trigger=\"hover\" data-placement=\"bottom\" data-content=\"progress\" data-html=\"true\">stepMessage</button></div>";
const completedButtonStyle = "btn-dark";
const unCompletedButtonStyle = "btn-light";

async function loadProgressModel() {
  const response = await fetch("/pro");
  const progressList = await response.json();
  document.getElementById("model").innerHTML = "";

  for(let i = 0; i < progressList.length; i++) {
    let formattedStr = goalStep.replace("stepMessage", stepMessage).replace("stepMessage", stepMessage);
    
    if(i == 0) {
      formattedStr = formattedStr.replace(stepMessage, "Start").replace(stepMessage, "Start"); 
    }
    else if(i == progressList.length - 1) {
      formattedStr = formattedStr.replace(stepMessage, "Goal").replace(stepMessage, "Goal");
    }
    else {
      formattedStr = formattedStr.replace("number", i).replace("number", i);
    }

    if(progressList[i].complete) {
      formattedStr = formattedStr.replace("buttonType", completedButtonStyle);
    }
    else {
      formattedStr = formattedStr.replace("buttonType", unCompletedButtonStyle);
    }

    formattedStr = formattedStr.replace("progress", progressList[i].exerciseString);
    
    document.getElementById("model").innerHTML += formattedStr;
    
    if(i != progressList.length - 1) {
      document.getElementById("model").innerHTML += rightArrow;
    }
  }
  $('[data-toggle="popover"]').popover();
}