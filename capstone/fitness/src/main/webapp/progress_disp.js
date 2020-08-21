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
const sortingStrategies = ["uncomplete", "all", "complete"];
let viewingIndex = -1;

async function loadPage() {
  await updateModel(-1);
  loadPercentages();
}

async function updateModel(insertionIndex) {
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

/** 
 * Updates progress page's viewing panel if the user hovers over a goal step.
 */
$(document).on("mouseover", "button[name='" + viewStep + "']", async function() {
    const value = parseInt($(this).val());
    if(viewingIndex != value) {
      viewingIndex = value;
      await updateModel(value);
    }
});

/**
 * Changes the sorting style and progress page's goal steps if a radio button is selected. 
 */
$(document).on("click", "input[name='sorting']", async function() {
    const params = new URLSearchParams();
    params.append('sorting', $(this).val());
    await fetch('/pagin', {method: 'POST', body: params});
    await updateModel(-1);
});

/**
 * Handles logic for shifts in page numbers.
 */
$(document).on("click", "button[name='move-page']", async function() {
    const params = new URLSearchParams();
    params.append('move-page', $(this).val());
    await fetch('/pagin', {method: 'POST', body: params});
    await updateModel(-1);
});

function loadPercentages() {
  $(function() {
    $(".progress").each(function() {
      // Takes each progress panel and writes two half circles to it that make up the circular progress bar.
      var value = $(this).attr('data-value');
      var left = $(this).find('.progress-left .progress-bar');
      var right = $(this).find('.progress-right .progress-bar');
    
      if (value > 0) {
        if (value <= 50) {
          right.css('transform', 'rotate(' + percentageToDegrees(value) + 'deg)');
        } 
        else {
          right.css('transform', 'rotate(180deg)');
          left.css('transform', 'rotate(' + percentageToDegrees(value - 50) + 'deg)');
        }
      }
    })
  });
}

function percentageToDegrees(percentage) {
  return percentage / 100 * 360;
}