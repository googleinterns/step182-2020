// Info insertion containers.
const goalStepDescConnector = "<div class=\"goal-desc-insertion\"></div>";
const goalStepDescConnectorWithText = "<div class=\"goal-desc-insertion-with-text\">infoMsg</div>";

// Variables for goal step display.
const stepMessage = "Step number";
const viewStep = "view-step";
const goalStepWidget = `<div class="row step"><button name="${viewStep}" class="btn buttonType btn-sm" value="arrayNum">${stepMessage}</button></div>`;
const completedGoalStepStyle = "btn-dark";
const uncompletedGoalStepStyle = "btn-light";

// Templates and variables for pagination control.
const paginationButton = "<li class=\"page-item\"><button name=\"move-page\" class=\"page-link\" value=\"pageNum\">pageNum</button></li>"
const goalStepCountLabel = "Goal Steps Per Page: -";
const pageLabel = "Page - of -";
const filters = ["uncomplete", "all", "complete"];
let pageNum = 1;

// Keywords for panels.
const nextKeyword = "Next";
const goalKeyword = "Goal";
const sessionKeyword = "Session";
const viewKeyword = "Viewing";

// Viewing panel helpers.
const viewingHeaderStr = "Distance From Step #";
let viewingIndex = -1;

// Templates for panels.
const progressSets = `
  <div class="row text-center mt-4">
    <div class="col-6 border-right">
      <div class="font-weight-bold mb-0">sets</div>
      <span class="small text-gray">Last Session</span>
    </div>
    <div class="col-6">
      <div class="font-weight-bold mb-0">sets</div>
      <span class="small text-gray">keyword</span>
    </div>
  </div>
`;

const progressCircle = `
  <div class="progress mx-auto" data-value='percentage'>
    <span class="progress-left"><span class="progress-bar border-primary"></span></span>
    <span class="progress-right"><span class="progress-bar border-primary"></span></span>
    <div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">
      <div class="h2 font-weight-bold">percentage<sup class="small">%</sup></div>
    </div>
  </div><br>`;

async function loadPage() {
  await updateDisplay(-1);
}

async function updateDisplay(insertionIndex) {
  const progressResponse = await fetch("/pro");
  const progressList = await progressResponse.json();

  const displayResponse = await fetch("/display-param");
  const displayParam = await displayResponse.json();

  // Add goal step count to pagination label.
  document.getElementById("count-label").innerText = goalStepCountLabel.replace("-", displayParam.count)
  
  // Sets the radio button filters with the correct one checked.
  setFilteredDisplay(displayParam.filter);

  setupPaginationBarAndModel(insertionIndex, displayParam.filter, progressList, displayParam.count);

  updatePanels(insertionIndex, progressList.length - 1);
}

function setFilteredDisplay(filterLabel) {
  for (filter of filters) {
    document.getElementById(filter).checked = filterLabel.toLowerCase() === filter;
  }
}

function setupPaginationBarAndModel(insertionIndex, filter, progressList, count) {
  const goalStepsFiltered = filterGoalSteps(filter, progressList);
  const paginationBar = $('#pagination-bar');
  const model = $('#model');

  paginationBar.pagination({
    dataSource: goalStepsFiltered["goalSteps"],
    pageSize: count,
    pageNumber: pageNum,
    callback: function(data, pagination) {
      pageNum = pagination.pageNumber;
      let dataHTML = "";
      for(let i = 0; i < data.length; i++) {
        const trueIndex = i + goalStepsFiltered["shiftedStart"] + ((pagination.pageNumber - 1) * pagination.pageSize);
        const formattedStr = getFormattedStr(trueIndex, data[i], progressList.length, insertionIndex, i == data.length - 1);
        dataHTML += formattedStr;
      }
      model.html(dataHTML);
      document.getElementById("page-label").innerText = pageLabel.replace("-",  pagination.pageNumber).replace("-", Math.ceil(pagination.totalNumber/ pagination.pageSize));
    }
  });
}

function filterGoalSteps(filter, goalSteps) {
  let start = 0;
  let end = goalSteps.length - 1;
  switch(filter) {
    case "UNCOMPLETE":
      start = end;
      while(start > 0 && !isComplete(goalSteps[start])) {
        start--;
      }
      start++;
      break;
    case "COMPLETE":
      end = start;
      while(end < goalSteps.length && isComplete(goalSteps[end])) {
        end++;
      }
      end--;
      break;
    default:
      break;
  }
  return {"goalSteps" : goalSteps.slice(start, end + 1), "shiftedStart" : start};
}

function getFormattedStr(index, goalStepObj, listSize, insertionIndex, isLast) {
  // Default formatted string.
  let formattedStr = goalStepWidget.replace("arrayNum", "" + index);

  // Formatted string based on placement in progress model.
  if(index == 0) {
    formattedStr = formattedStr.replace(stepMessage, "Start").replace(stepMessage, "Start"); 
  }
  else if(index == listSize - 1) {
    formattedStr = formattedStr.replace(stepMessage, "Goal").replace(stepMessage, "Goal");
  }
  else {
    formattedStr = formattedStr.replace("number", index).replace("number", index);
  }

  // Styles goal step based on if it is complete or not.
  if(isComplete(goalStepObj)) {
    formattedStr = formattedStr.replace("buttonType", completedGoalStepStyle);
  }
  else {
    formattedStr = formattedStr.replace("buttonType", uncompletedGoalStepStyle);
  }

  // Inserts info into panel if the insertion index matches the goal step number.
  if(index === insertionIndex) {
    formattedStr += goalStepDescConnectorWithText.replace("infoMsg", goalStepObj.exerciseString);
  }
  else {
    formattedStr += goalStepDescConnector;
    // Makes it so the last goal step viewed on the page doesn't have a tail under it.
    if(isLast) {
      formattedStr = formattedStr.replace("goal-desc-insertion", "goal-desc-insertion-empty");
    }
  }

  return formattedStr;
}

function isComplete(goalStepObj) {
  return goalStepObj.tag.includes("Complete");
}

async function updatePanels(insertionIndex, goalIndex) {
  // Get stats for the panels of the page.
  const params = new URLSearchParams();
  params.append('insertion', insertionIndex);
  await fetch('/stats', {method: 'POST', body: params});

  clearPanels();
  
  const panelsResponse = await fetch("/panels");
  const panelsList = await panelsResponse.json();
  const next = getPanelComparator(nextKeyword, panelsList);
  const goal = getPanelComparator(goalKeyword, panelsList);
  const viewing = getPanelComparator(viewKeyword, panelsList);
  
  enableViewingPanel(viewing, insertionIndex, goalIndex);

  // Add percentage comparisons between session exercises and panel headers (next goal step, goal, and viewing step).
  for(let i = 0; i < panelsList.length; i++) {
    if(panelsList[i].tag.includes(sessionKeyword) && panelsList[i].name === goal.name) {
      addPercentages(panelsList[i], next, goal, viewing);
    }
  }
  loadPercentages();
}

function clearPanels() {
  const panels = [getBarAndSets(nextKeyword), getBarAndSets(goalKeyword), getBarAndSets(viewKeyword)];
  panels.map(keywordTuple => {
   keywordTuple["bar"].html("");
   keywordTuple["sets"].html("");
  });
}

function getBarAndSets(keyword) {
  let keywordTuple = new Map();
  keywordTuple["bar"] = $("#" + keyword.toLowerCase() + "-bar");
  keywordTuple["sets"] = $("#" + keyword.toLowerCase() + "-sets");
  return keywordTuple;
}

function getPanelComparator(keyword, list) {
  for(let i = 0; i < list.length; i++) {
    if(list[i].tag.includes(keyword)) {
      return list[i];
    }
  }
  return null;
}

function enableViewingPanel(viewing, insertionIndex, goalIndex) {
  // Enable viewing panel if a viewing goal step exists.
  let panel = $("#view-panel");
  if(viewing) {
    panel.show();
    const viewHeader = document.getElementById("view-panel-header");
    if(insertionIndex === 0) {
      viewHeader.innerText = viewingHeaderStr.replace("#","Start");  
    }
    else if(insertionIndex === goalIndex) {
      viewHeader.innerText = viewingHeaderStr.replace("#","Goal");
    }
    else {
      viewHeader.innerText = viewingHeaderStr.replace("#", insertionIndex);  
    }
  }
  else {
    panel.hide();
  }
}

function addPercentages(sessionExercise, next, goal, viewing) {
  for (let type in sessionExercise.setValues) {
    addToPanel(nextKeyword, sessionExercise.setValues[type], next.setValues[type]);
    addToPanel(goalKeyword, sessionExercise.setValues[type], goal.setValues[type]);
    if(viewing) {
      addToPanel(viewKeyword, sessionExercise.setValues[type], viewing.setValues[type]);
    }
  }
}

function addToPanel(keyword, sessionValues, comparisonValues) {
  let percentage = (sum(sessionValues)/sum(comparisonValues) * 100).toPrecision(2);
  if(percentage >= 100) {
    percentage = "100";
  }
  // Set the progress bar and sets for the panel of the corresponding keyword.
  var keywordTuple = getBarAndSets(keyword);
  keywordTuple["bar"].html(keywordTuple["bar"].html() + progressCircle.replace("percentage", percentage).replace("percentage", percentage));
  keywordTuple["sets"].html(keywordTuple["sets"].html() + progressSets.replace("sets", sessionValues).replace("sets", comparisonValues).replace("keyword", keyword));
}

function sum(values) {
  let summation = values.reduce((a, b) => a + b, 0);
  return summation;
}

function loadPercentages() {
  $(".progress").each(function() {
    // Takes each progress panel and writes two half circles to it that make up the circular progress bar.
    var value = $(this).attr('data-value');
    var left = $(this).find('.progress-left .progress-bar');
    var right = $(this).find('.progress-right .progress-bar');
    
    if (value > 0) {
      if (value <= 50) {
        right.css('transform', 'rotate(' + percentageToDegrees(value) + 'deg)');
      } else {
        right.css('transform', 'rotate(180deg)');
        left.css('transform', 'rotate(' + percentageToDegrees(value - 50) + 'deg)');
      }
    }
  });
}

function percentageToDegrees(percentage) {
  return percentage / 100 * 360;
}

/** 
 * Updates progress page's viewing panel if the user hovers over a goal step.
 */
$(document).on("mouseover", "button[name='" + viewStep + "']", async function() {
    const value = parseInt(await $(this).val());
    if(viewingIndex != value) {
      viewingIndex = value;
      await updateDisplay(value);
    }
});

/**
 * Changes the filter and progress page's goal steps if a radio button is selected. 
 */
$(document).on("click", "input[name='filter']", async function() {
    const params = new URLSearchParams();
    params.append('filter', $(this).val());
    await fetch('/display-param', {method: 'POST', body: params});
    await updateDisplay(-1);
});