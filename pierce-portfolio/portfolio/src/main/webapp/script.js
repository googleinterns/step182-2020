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
 * Adds a random greeting to the page. Divided array into multiple lines
 */
function addRandomGreeting() {
  const greetings =
      ['tomatoes are my favorite food', 
      'I ate an apple every day in 2019'
      ,'I am a middle child', 
      'I am learning to drive right now', 
      'I went to boarding school for high school', 
      'I play the saxophone and piano'];
  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];
  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}
function getMessage() {
  console.log('Fetching comments');
  // The fetch() function returns a Promise because the request is asynchronous.
  const responsePromise = fetch('/data');

  // When the request is complete, pass the response into handleResponse().
  responsePromise.then(handleResponse);
}
/**
 * Handles response by converting it to text and passing the result to
 * addQuoteToDom().
 */
function handleResponse(response) {
  console.log('Handling the response.');

  // response.text() returns a Promise, because the response is a stream of
  // content and not a simple variable.
  const jsonPromise = response.json();

  // When the response is converted to text, pass the result into the
  // addMessageToDom() function.
  jsonPromise.then(addMessageToDom);
}

/** Adds the hello world message to the DOM. */
function addMessageToDom(comments) {
  console.log('Adding message to dom: ' + comments);
  const commentContainer = document.getElementById('comments-container');
  commentContainer.innerHTML = '';
  var i;
  commentLoop:
  for(i=0; i < comments.length; i++) {
    if(comments[i]) {
    // tried using the getters here, but I don't think the functions can be passed through a JSON object?
      commentContainer.appendChild(newComment(comments[i].name+ " said: " + comments[i].text + " | " + comments[i].timestamp));    
    }
  }
}
function newComment(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

function checkLoginStatus(){
  var x = document.getElementById("comments-form");
  console.log("fetching login status");
  const loginPromise = fetch('/login-status');
  loginPromise.then(handleLogin);
  }

function handleLogin(loginResponse){
  const loginJson = loginResponse.json();
  loginJson.then(updateDisplay);
  }

function updateDisplay(login){
    console.log("login status is:" + login + login.length);
    const commentsForm = document.getElementById("comments-form");
    const loginLink = document.getElementById("login-link")
    if (login.length==1){
        commentsForm.style.display = "none";
        loginLink.href= login[0];
    }

    else{
        commentsForm.style.display = "initial";
        para = document.getElementById("p1");
        node = document.createTextNode("You are logged in. Email: " + login[0] );
        loginLink.innerHTML="Logout.";
        loginLink.href=login[1];

    }
}


//   if (x.style.display === "none") {
//     x.style.display = "block";
//   } else {
//     x.style.display = "none";
//   }

