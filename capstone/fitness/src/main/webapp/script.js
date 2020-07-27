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

/*
 initIndex()
 Function that runs when the body of index loads.
 Actions:
   - Check if the user is logged in and display their name or give them the 
     option to log in. 
 */
function initIndex() {
  console.log("this works");
  displayLogIn();
}

async function displayLogIn() {
  const loginResponse = await fetch('/login');
  const loginResponseText = await loginResponse.text();
  var loginInfo = loginResponseText.split("\n");
  
  // This contains wheter the user is logged in, the user email, and login/out link
  const loggedin = loginInfo[0];
  const userEmail = loginInfo[1]
  const url = loginInfo[2];

  const loginContainer = document.getElementById("login");

  if(loggedin == "1") {
    loginContainer.innerHTML = createLogoutTemplate(userEmail, url);
  }
  else if(loggedin == "0") {
    loginContainer.innerHTML = createLoginTemplate(url);
  }


}

function createLoginTemplate(url) {
  var template = 
  `
  <p>Welcome, stranger. 
    <a href='https://8080-ce19f3ee-62b8-4778-b1d0-8b6beb1e067f.us-east1.cloudshell.dev/${url}'>Login Here!</a>
  </p>
  `;
  return template;
}

function createLogoutTemplate(name, url) {
  var template = 
  `
  <p>Welcome, ${name}. 
    <a href='https://8080-ce19f3ee-62b8-4778-b1d0-8b6beb1e067f.us-east1.cloudshell.dev/${url}'>Logout here</a>
  </p>
  `;
  return template;
}

