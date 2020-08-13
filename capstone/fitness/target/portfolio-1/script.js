/**
 Function that runs when the body of index loads.
 */
function initIndex() {
  displayLogIn();
}

/**
 Function that runs when the body of view-data loads.
 */
function initViewData() {
  loadDataChart();
  displayLogIn();
}

/**
 Function that fills in the charts div.
 Retrieves sesssion data from datastore and displays it on the chart.
 */
google.charts.load('current', {packages: ['corechart', 'line']});
async function loadDataChart() {
  
  // Set up chart for X/Y visualization.
  var data = new google.visualization.DataTable();
  data.addColumn('number', 'numberOfSessions');
  data.addColumn('number', 'speed');

  // Gets the JSON object that holds all the sesssions.
  const progressData = await fetch('/progress');
  const dataJson = await progressData.json();
  // Create matrix with sessions numbers and speeds.
  var dataRows = [];
  var i=0;
  while(dataJson[i]) {
    dataRows[i] = [i, dataJson[i].speed];
    i++;
  }
  // Adds the data points to the chart.
  data.addRows(dataRows);

  //TODO(gabrieldg)
  //  Get the initial and goal time to display as horizontal lines.

  // Customizing the chart
  var options = {
    hAxis: {
      title: 'Session #'
    },
    vAxis: {
      title: 'Speed (Km/h)'
    }
  };

  var chart = new google.visualization.LineChart(document.getElementById('data-chart'));
  chart.draw(data, options);
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
 Uses string literals to create a HTML template for logging in/out
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

// TODO: load in user information from LoginServlet.java
// TODO: load in user's scheduled events from CalendarServlet.java
// DONE: get current date.
async function getCalendarInfo(){
    console.log("fetching date");
    const response = await fetch('/calendar-servlet');
    const date = await response.text();
    document.getElementById('week-container').innerText = `Planned events for the week of ${date}`;
}