// Info insertion containers.
const goalStepDescConnector = "<div class=\"goal-desc\"></div>";
const goalStepDescConnectorWithText = "<div class=\"goal-desc-fill\">infoMsg</div>";

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
const sortingStrategies = ["uncomplete", "all", "complete"];

// Keywords for panels.
const nextKeyword = "Next";
const goalKeyword = "Goal";
const sessionKeyword = "Session";
const viewKeyword = "Viewing";

// Viewing panel helpers.
const viewingHeaderStr = "Distance From Step #";
let viewingIndex = -1;


// Templates for the panels.
const progressCircle = `
  <div class="progress mx-auto" data-value='percentage'>
    <span class="progress-left"><span class="progress-bar border-primary"></span></span>
    <span class="progress-right"><span class="progress-bar border-primary"></span></span>
    <div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">
      <div class="h2 font-weight-bold">percentage<sup class="small">%</sup></div>
    </div>
  </div>`;

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

async function loadPage() {
  await updateModelAndStats(-1);
}

/**
 * Updates the displayed goal steps and panels based on the insertion index.
 */
async function updateModelAndStats(insertionIndex) {
  // Add formatted progress model to page with relevant pagination display.
  const goalStepCount = await formatModelAndPagination(insertionIndex);
  
  // Get stats for the panels of the page.
  const params = new URLSearchParams();
  params.append('insertion', insertionIndex);
  await fetch('/stats', {method: 'POST', body: params});

  // Add formatted stats to page.
  await formatPanels(insertionIndex, goalStepCount - 1);
}

async function formatPanels(insertionIndex, goalIndex) {
  clearPanels();
  
  const statsResponse = await fetch("/stats");
  const statsList = await statsResponse.json();
  const next = getStat(nextKeyword, statsList);
  const goal = getStat(goalKeyword, statsList);
  const viewing = getStat(viewKeyword, statsList);
  
  enableViewing(viewing, insertionIndex, goalIndex);

  // Add percentage comparisons between session exercises and panel headers (next goal step, goal, and viewing step).
  for(let i = 0; i < statsList.length; i++) {
    if(statsList[i].tag.includes(sessionKeyword) && statsList[i].name === goal.name) {
      addPercentages(statsList[i], next, goal, viewing);
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

function getStat(keyword, list) {
  for(let i = 0; i < list.length; i++) {
    if(list[i].tag.includes(keyword)) {
      return list[i];
    }
  }
  return null;
}

function enableViewing(viewing, insertionIndex, goalIndex) {
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
      viewHeader.innerText = viewingHeaderStr.replace("#","" + insertionIndex);  
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
    if(viewing != null) {
      addToPanel(viewKeyword, sessionExercise.setValues[type], viewing.setValues[type]);
    }
  }
}

function addToPanel(keyword, sessionValues, comparisonValues) {
  let percentage = (sum(sessionValues)/sum(comparisonValues) * 100).toPrecision(2);
  if(percentage > 100) {
    percentage = 100;
  }
  // Set the progress bar and sets for the panel of the corresponding keyword.
  var keywordTuple = getBarAndSets(keyword);
  keywordTuple["bar"].html(keywordTuple["bar"].html() + progressCircle.replace("percentage", "" + percentage).replace("percentage", "" + percentage));
  keywordTuple["sets"].html(keywordTuple["sets"].html() + progressSets.replace("sets", "" + sessionValues).replace("sets", "" + comparisonValues).replace("keyword", keyword));
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

async function formatModelAndPagination(insertionIndex) {
  // Get list of goal steps and upadate user http session's metadata.
  const progressResponse = await fetch("/pro");
  const progressList = await progressResponse.json();

  // Get updated metadata.
  const metadataResponse = await fetch("/pagin");
  const metadata = await metadataResponse.json();

  // Add goal step count to pagination label.
  document.getElementById("count-label").innerText = goalStepCountLabel.replace("-", metadata.count)

  // Set up sorting strategy display.
  setSortingDisplay(metadata.sortLabel);

  // Build pagination bar.
  const pageMove = document.getElementById("start-page-bar");
  let paginationButtons = paginationButton.replace("pageNum", "previous").replace("pageNum", "Previous");
  for(let i = 0; i < metadata.maxPages; i++) {
    paginationButtons += paginationButton.replace("pageNum", i).replace("pageNum", (i + 1));
  }
  paginationButtons += paginationButton.replace("pageNum", "next").replace("pageNum", "Next");
  pageMove.innerHTML = paginationButtons;
  document.getElementById("page-label").innerText = pageLabel.replace("-", metadata.page + 1).replace("-", metadata.maxPages);


  // Display progress model.
  const model = document.getElementById("model");
  model.innerHTML = "";
  const startIndex = metadata.startIndex;
  for(let i = 0; i < progressList.length; i++) {
    let formattedStr = getFormattedStr(startIndex + i, progressList[i], metadata.goalSteps, insertionIndex, i == progressList.length - 1);
    model.innerHTML += formattedStr;
  }
  
  // Return goal step count to help with the panel set up.
  return metadata.goalSteps;
}

function setSortingDisplay(sortLabel) {
  for (sortingStrategy of sortingStrategies) {
    document.getElementById(sortingStrategy).checked = sortLabel.toLowerCase() === sortingStrategy;
  }
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
  }

  // Makes it so the last goal step viewed on the page doesn't have a tail under it.
  if(isLast) {
    formattedStr = formattedStr.replace("goal-desc", "goal-desc-no-border");
  }

  return formattedStr;
}

function isComplete(goalStepObj) {
  return goalStepObj.tag.includes("Complete");
}

/** 
 * Updates progress page's viewing panel if the user hovers over a goal step.
 */
$(document).on("mouseover", "button[name='" + viewStep + "']", async function() {
    const value = parseInt($(this).val());
    if(viewingIndex != value) {
      viewingIndex = value;
      await updateModelAndStats(value);
    }
});

/**
 * Changes the sorting style and progress page's goal steps if a radio button is selected. 
 */
$(document).on("click", "input[name='sorting']", async function() {
    const params = new URLSearchParams();
    params.append('sorting', $(this).val());
    await fetch('/pagin', {method: 'POST', body: params});
    await updateModelAndStats(-1);
});

/**
 * Handles logic for shifts in page numbers.
 */
$(document).on("click", "button[name='move-page']", async function() {
    const params = new URLSearchParams();
    params.append('move-page', $(this).val());
    await fetch('/pagin', {method: 'POST', body: params});
    await updateModelAndStats(-1);
});
