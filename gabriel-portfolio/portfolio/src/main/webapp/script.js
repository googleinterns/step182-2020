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

/**
 * Adds a random fact to the page.
 */
function addRandomFact() {
  const randomFacts =
      ['I can solve a rubiks cube in less than a minute',
       'I speak english and spanish fluently',
       'Both of my parents are from Cuba',
       'My favorite food is fried fish'];

  // Pick a random greeting.
  const randomFact = randomFacts[Math.floor(Math.random() * randomFacts.length)];

  // Show the hide-message button.
  const hide = document.getElementById("hide-random-fact");
  hide.style.visibility = "visible";

  // Add it to the page.
  const randomFactContainer = document.getElementById('random-fact-container');
  randomFactContainer.innerText = randomFact;
}

/*
  Hides the random fact
*/
function hideRandomFact() {
  // Set the message to an empty string.
  const randomFactContainer = document.getElementById('random-fact-container');
  randomFactContainer.innerText = "";

  // Hide the hide-message button.
  const button = document.getElementById("hide-random-fact");
  button.style.visibility = "hidden";
}

/*
  Uses the fetch() function to get the greeting text from /get-greeting.
*/
async function getGreeting() {
  const response = await fetch('/get-greeting');
  const quote = await response.text();
  document.getElementById('greeting-container').innerText = quote;
}

function createCommentTemplate(text, name, language, date) {
        var template = 
        `
        <h3 class="text-comments">${name} [${language}]</h3>
        <div class="comment">
          <p class="text-comments">${text}</p>
          <p class="date-comments">${date}</p>
        </div>
        `;
    return template;
 
}

/*
  Uses fetch() to get comments from /data.
*/
async function getComments() {
  const response = await fetch('/data');
  const commentKeys = await response.json();
  
  const commentsContainer = document.getElementById('comments-container');
  commentsContainer.innerHTML = '';

  const maxNumberOfComments = document.getElementById("enter-max-comments").value;
  console.log(maxNumberOfComments);
  const languageCode = document.getElementsByName("language")[0].value;
  console.log(languageCode);

  

  // loop through the strings in the json object
  var i;
  commentLoop:
  for(i=0; i < maxNumberOfComments; i++) {
    if(commentKeys[i]) {
      var commentKey = commentKeys[i];
      const params = languageCode+commentKey;
      
      const response = await fetch('/translate', {
                                                  method: "POST",
                                                  body: params
      });
      // Gets the dat from response.
      const translated = await response.text();
      // Splits the output by each line
      var splitOutput = translated.split("\n");
      const text = splitOutput[0];
      const name = splitOutput[1];
      const language = splitOutput[2];
      const date = splitOutput[3];
      const commentTemplate = createCommentTemplate(text, name, language, date);
      const commentHTML = document.createElement("div");
      commentHTML.style = "border: 2px solid #c3c3c3;";
      commentHTML.innerHTML = commentTemplate;
      commentsContainer.append(commentHTML);
      commentsContainer.append(document.createElement("br"));      
    }
    else {
      console.log("Ran out of comments");
      break commentLoop;
    }
  }
}

function loadMaps() {
  const birthdayMap = new google.maps.Map(
    document.getElementById('birthday-map'),
    {center: {lat: 25.76, lng: -80.19}, zoom: 16});

  const stetsonMap = new google.maps.Map(
    document.getElementById('stetson-map'),
    {center: {lat: 29.0350, lng: -81.3032}, zoom: 16});
  
  const cubaMap = new google.maps.Map(
    document.getElementById('cuba-map'),
    {center: {lat: 21.9328, lng: -79.4366}, zoom: 16});

  const cssiMap = new google.maps.Map(
    document.getElementById('cssi-map'),
    {center: {lat: 40.4406, lng: -79.9959}, zoom: 16}); 

  const keyWestMap = new google.maps.Map(
    document.getElementById('key-west-map'),
    {center: {lat: 24.5551, lng: -81.7800}, zoom: 16});

  const newYorkMap = new google.maps.Map(
    document.getElementById('new-york-map'),
    {center: {lat: 40.7128, lng: -74.0060}, zoom: 16});

  const chicagoMap = new google.maps.Map(
    document.getElementById('chicago-map'),
    {center: {lat: 41.8781, lng: -87.6298}, zoom: 16});  
}