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
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['I can solve a rubiks cube in less than a minute',
       'I speak english and spanish fluently',
       'Both of my parents are from Cuba',
       'My favorite food is fried fish'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Show the hide-message button
  const hide = document.getElementById("hide-message");
  hide.style.visibility = "visible";

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function hideMessage() {
  // Set the message to an empty string
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = "";

  // Hide the hide-message button
  const button = document.getElementById("hide-message");
  button.style.visibility = "hidden";
}