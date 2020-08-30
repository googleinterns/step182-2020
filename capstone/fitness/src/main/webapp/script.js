/**
 Function that runs when the body of index loads.
 */
function initIndex() {
  displayLogIn();
  fillViewWorkouts();
}

/**
 Function that runs when the body of view-data loads.
 */
function initViewData() {
  loadDataChart();
  displayLogIn();
}

// Fills in data for embeded calendar. 
async function getCalendarInfo(){
  // Get calendar ID from URL instead of from JSON to avoid CORS error. 
  const urlParams = new URLSearchParams(window.location.search);;
  const id = urlParams.get("calendarId");
  document.getElementById("calendar-container").src = "https://calendar.google.com/calendar/embed?src=" + id + "&ctz=America%2FNew_York";
  }


async function isLoggedin() {
  const loginResponse = await fetch('/login');
  const loginInfo = await loginResponse.json();
  const userEmail = loginInfo.email;

  if(userEmail != "stranger")
    return true;
  else
    return false;
}

/**
 Function that fills in the login div.
 Checks if the user is logged in and accordingly gives the user 
 a log in/out option.
 */
async function displayLogIn() {
  const loginResponse = await fetch('/login');
  const loginInfo = await loginResponse.json();
  
  const userEmail = loginInfo.email;
  const url = loginInfo.url;

  const loginContainer = document.getElementById("login");
  

  if(userEmail != "stranger") {
    loginContainer.innerHTML = createLoginTemplate(userEmail, url, "out");
  }
  else {
    loginContainer.innerHTML = createLoginTemplate(userEmail, url, "in");
  }
}

/**
 Uses string literals to create a HTML template for logging in/out.
 */
function createLoginTemplate(name, url, type) {
  var template = 
  `
  <p>Welcome, ${name}. 
    <a href='${url}'>Log${type} here</a>
  </p>
  `;
  return template;
}

async function getUser(){
  const loginResponse = await fetch('/login');
  const loginInfo = await loginResponse.json();
  
  const userEmail = loginInfo.email;
  usernameContainer = document.getElementById("user-message");
  usernameContainer.innerHTML = "Hello " + userEmail + ".";
}

/*
  Dynamically adds a form to get the information to create a workout.
*/
function fillWorkoutQuestions() {
  workoutQuestions = document.getElementById("workout-information-container");
  const type = document.getElementById("workout-type").value;
  
  if(type == "lifting") {
    workoutQuestions.innerHTML = 
      `
        <form action="javascript:createWorkout();" method="POST">
          <label for="workoutName">Enter a name for this workout</label>
          <textarea name="workoutName"></textarea>
          <br>
          <label for="weekstoTrain">How many weeks do you have to train: </label>
          <input type="number" name="weeksToTrain" min="5">
          <br>
          <label for="initialWeight">Enter your current max weight: </label>
          <input type="number" name="initialWeight" min="0" step="0.01">
          <br>
          <label for="initialReps">Enter your current max reps: </label>
          <input type="number" name="initialReps" min="0">
          <br>
          <label for="goalWeight">Enter your goal weight: </label>
          <input type="number" name="goalWeight" min="0" step="0.01">
          <br>
          <label for="goalReps">Enter your goal reps: </label>
          <input type="number" name="goalReps" min="0">
          <br>
          <input type="submit" value="Create workout!">
        </form>
      `;
  }
  else {
    workoutQuestions.innerHTML =
      `
        <form action="javascript:createWorkout();" method="POST">
          <label for="workoutName">Enter a name for this workout</label>
          <textarea name="workoutName"></textarea>
          <br>
          <label for="weekstoTrain">How many weeks do you have to train: </label>
          <input type="number" name="weeksToTrain" min="5">
          <br>
          <label for="marathonLength">How long is your marathon (in kilometers) </label>
          <input type="number" name="marathonLength" min="5" step="0.01">
          <br>
          <label for="initialTime">Enter your current time in hours: </label>
          <input type="number" name="initialTime" min="0" step="0.01">
          <br>
          <label for="goalTime">Enter your goal time in hours: </label>
          <input type="number" name="goalTime" min="0" step="0.01">
          <br>
          <label for="mileTime">Enter your mile time in minutes: </label>
          <input type="number" name="mileTime" min="0" step="0.01">
          <br>
          <input type="submit" value="Create workout!">
        </form>
      `;
  }
}

/*
  Gets the information gathered from fillWorkoutQuestions,
  creates a JSON string with it and makes a POST request to
  /create-workout, which stores everything in datastore.
*/
async function createWorkout() {
  const type = document.getElementById("workout-type").value;
  var workoutInfoJson = {};
  workoutInfoJson.workoutName = document.getElementsByName("workoutName")[0].value;
  workoutInfoJson.workoutType = document.getElementsByName("workoutType")[0].value;
  workoutInfoJson.weeksToTrain = document.getElementsByName("weeksToTrain")[0].value;
  if(type == "lifting") {
    workoutInfoJson.initialWeight = document.getElementsByName("initialWeight")[0].value;
    workoutInfoJson.initialReps = document.getElementsByName("initialReps")[0].value;
    workoutInfoJson.goalWeight = document.getElementsByName("goalWeight")[0].value;
    workoutInfoJson.goalReps = document.getElementsByName("goalReps")[0].value;
  } 
  else {
    workoutInfoJson.marathonLength = document.getElementsByName("marathonLength")[0].value;
    workoutInfoJson.initialTime = document.getElementsByName("initialTime")[0].value;
    workoutInfoJson.goalTime = document.getElementsByName("goalTime")[0].value;
    workoutInfoJson.mileTime = document.getElementsByName("mileTime")[0].value;
  }

  const infoString = JSON.stringify(workoutInfoJson);
  const createWorkoutRequest = await fetch('/create-workout', {
                                                    method: "POST",
                                                    body: infoString
  });

  document.getElementById("workout-information-container").innerHTML = '';
  
}

/*
  Adds a dropdown with all of a user's workouts, and when submitted,
  it sends to user to /view-workout.
*/
async function fillViewWorkouts() {
  
  if(isLoggedin()) {
    const userInfoRequest = await fetch('/get-user-data');
    const userInfoJSON = await userInfoRequest.json();

    var form = document.createElement("FORM");
    form.method="GET";
    form.action="/view-workout"

    const workoutsArray = JSON.parse(userInfoJSON.workoutList);    
    var selectWorkout = document.createElement("SELECT");
    selectWorkout.name = "selectWorkout";

    var i;
    for(i=0; i < workoutsArray.length; i++) {
      var option = document.createElement("OPTION");
      option.value = workoutsArray[i];
      option.innerHTML = removeEmail(workoutsArray[i], userInfoJSON.email);
      selectWorkout.append(option);
    }

    form.append(selectWorkout);

    var submit = document.createElement("input");
    submit.type = "submit"; 
    submit.value = "View workout!"
    form.append(submit);

    document.getElementById("view-workouts-container").append(form);

  }
}

function removeEmail(id, email) {
  const emailLen = email.length;
  return id.substring(emailLen);
}

