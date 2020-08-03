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

//TODO (@piercedw) : Fetch user's nickname from LoginServlet.java.
// get current date.
async function getDate(){
  console.log("fetching date");
  const response = await fetch('/date');
  const jsonDate = await response.json();
  console.log("date is " + jsonDate);
  document.getElementById('week-container').innerText = `Planned events for the week of ${jsonDate}:`;
}

//Fetches JSON from server and displays.
async function getCalendarInfo(){
  console.log("fetching Workouts");
  const response = await fetch('/calendar-creation');
  const jsonArray = await response.json();
  const eventsContainer = document.getElementById('events-container');
  var daysInAWeek = 8; 
  for(var i =0; i < daysInAWeek; i++) {
    if(jsonArray[i]) {
      eventsContainer.appendChild(appendEvent("[DAY]: " + jsonArray[i].description + " at [TIME]")); 
      }
    }
  }

// helper function for displaying workouts
function appendEvent(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

// These functions work with the prev and next buttons
// TODO (@piercedw) : build these functions. fetch next week's workouts. change "week of".
function getNextWeek(){

}

function getPreviousWeek(){
    
}


