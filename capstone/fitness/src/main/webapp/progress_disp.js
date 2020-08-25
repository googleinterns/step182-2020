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
let viewingIndex = -1;

let pageNum = 1;

async function loadPage() {
  await updateModel(-1);
  loadPercentages();
}

async function updateModel(insertionIndex) {
  const progressResponse = await fetch("/pro");
  const progressList = await progressResponse.json();

  const metadataResponse = await fetch("/pagin");
  const metadata = await metadataResponse.json();

  // Add goal step count to pagination label.
  document.getElementById("count-label").innerText = goalStepCountLabel.replace("-", metadata.count)

  // Set up filtered display.
  setFilteredDisplay(metadata.filter);

  const goalStepsFiltered = filterGoalSteps(metadata.filter, progressList);

  const paginationBar = $('#pagination-bar');
  const model = $('#model');
  paginationBar.pagination({
    dataSource: goalStepsFiltered["goalSteps"],
    pageSize: metadata.count,
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
      console.log(paginationBar);
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

function setFilteredDisplay(filterLabel) {
  for (filter of filters) {
    document.getElementById(filter).checked = filterLabel.toLowerCase() === filter;
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
 * Changes the filter and progress page's goal steps if a radio button is selected. 
 */
$(document).on("click", "input[name='filter']", async function() {
    const params = new URLSearchParams();
    params.append('filter', $(this).val());
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