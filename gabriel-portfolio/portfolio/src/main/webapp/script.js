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

/* 
  Creates an <li> element containing text .
*/
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/*
  Uses fetch() to get comments from /data.
*/
async function getComments() {
  const response = await fetch('/data');
  const comments = await response.json();
  
  const commentList = document.getElementById('comments-container');
  commentList.innerHTML = '';

  const maxNumberOfComments = document.getElementById("enter-max-comments").value;
  console.log(maxNumberOfComments);
  const languageCode = document.getElementsByName("language")[0].value;
  console.log(languageCode);

  

  // loop through the strings in the json object
  var i;
  commentLoop:
  for(i=0; i < maxNumberOfComments; i++) {
    if(comments[i]) {
      var comment = comments[i];
      const params = languageCode+comment;
      
      const response = await fetch('/translate', {
                                                  method: "POST",
                                                  body: params
      });
      const translated = await response.text();
      console.log(translated);

      commentList.appendChild(createListElement(translated));    
    }
    else {
      console.log("Ran out of comments");
      break commentLoop;
    }
  }
}
