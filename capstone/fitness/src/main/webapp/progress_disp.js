const rightArrow = "<div class=\"goal-desc\"></div>";
const rightArrowFill = "<div class=\"goal-desc-fill\"><p><i>infoMsg</i></p></div>";
const stepMessage = "Step number";
const viewStep = "view-step";
const goalStep = "<div class=\"row step\"><button name=\"" + viewStep + "\" title=\"stepMessage\" type=\"button\" class=\"btn buttonType btn-sm\" data-container=\"body\" data-toggle=\"popover\" data-trigger=\"hover\" data-placement=\"right\" data-content=\"progress\" data-html=\"true\" value=\"arrayNum\">stepMessage</button></div>";
const completedButtonStyle = "btn-dark";
const unCompletedButtonStyle = "btn-light";

const paginationButton = "<li class=\"page-item\"><button name=\"move-page\" class=\"page-link\" value=\"pageNum\">pageNum</button></li>"

const nextKeyword = "Next";
const goalKeyword = "Goal";
const viewKeyword = "Viewing";

const viewingHeaderStr = "Distance From Step #";
let viewingIndex = -1;

const progressCircle = `<div class="progress mx-auto" data-value='percentage'>
              <span class="progress-left"><span class="progress-bar border-primary"></span></span>
                <span class="progress-right"><span class="progress-bar border-primary"></span></span>
                <div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">
                  <div class="h2 font-weight-bold">percentage<sup class="small">%</sup></div>
                </div>
            </div>`;

const progressSets = `<div class="row text-center mt-4">
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

const goalStepCountLabel = "Goal Steps Per Page: -";
const pageLabel = "Page - of -";

async function loadProgressModel() {
  await updateModelAndStats(-1);
}

async function loadPercentages(insertionIndex, goalIndex) {
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
  
  const statsResponse = await fetch("/stats");
  const statsList = await statsResponse.json();
  const next = getStat(nextKeyword, statsList);
  const goal = getStat(goalKeyword, statsList);
  const viewing = getStat(viewKeyword, statsList);
  clearPanels();
  enableViewing(viewing);
  for(let i = 0; i < statsList.length; i++) {
    if(statsList[i].tag.includes("Session") && statsList[i].name === goal.name) {
      addPercentages(statsList[i], next, goal, viewing);
    }
  }

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

function getStat(keyword, list) {
  for(let i = 0; i < list.length; i++) {
    if(list[i].tag.includes(keyword)) {
      return list[i];
    }
  }
  return null;
}

function clearPanels() {
  const panels = [getBarAndSet(nextKeyword), getBarAndSet(goalKeyword), getBarAndSet(viewKeyword)];
  panels.map(keywordTuple => {
   keywordTuple["bar"].html("");
   keywordTuple["sets"].html("");
  });
}

function enableViewing(viewing) {
  let panel = $("#view-panel");
  if(viewing) {
    panel.show();
  }
  else {
    panel.hide();
  }
}

function percentageToDegrees(percentage) {
  return percentage / 100 * 360;
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
  var keywordTuple = getBarAndSet(keyword);
  keywordTuple["bar"].html(keywordTuple["bar"].html() + progressCircle.replace("percentage", "" + percentage).replace("percentage", "" + percentage));
  keywordTuple["sets"].html(keywordTuple["sets"].html() + progressSets.replace("sets", "" + sessionValues).replace("sets", "" + comparisonValues).replace("keyword", keyword));
}

function sum(values) {
  let summation = values.reduce((a, b) => a + b, 0);
  return summation;
}

function getBarAndSet(keyword) {
  let keywordTuple = new Map();
  keywordTuple["bar"] = $("#" + keyword.toLowerCase() + "-bar");
  keywordTuple["sets"] = $("#" + keyword.toLowerCase() + "-sets");
  return keywordTuple;
}

async function formatModel(insertionIndex) {
  // Order matters
  const progressResponse = await fetch("/pro");
  const progressList = await progressResponse.json();
  const metadataResponse = await fetch("/pagin");
  const metadata = await metadataResponse.json();

  const pageMove = document.getElementById("start-page-bar");

  let paginationButtons = paginationButton.replace("pageNum", "previous").replace("pageNum", "Previous");
  for(let i = 0; i < metadata.maxPages; i++) {
    paginationButtons += paginationButton.replace("pageNum", i).replace("pageNum", (i + 1));
  }
  paginationButtons += paginationButton.replace("pageNum", "next").replace("pageNum", "Next");
  pageMove.innerHTML = paginationButtons;

  const model = document.getElementById("model");
  model.innerHTML = "";
  const startIndex = metadata.startIndex;

  for(let i = 0; i < progressList.length; i++) {
    let formattedStr = getFormattedStr(startIndex + i, progressList[i], metadata.goalSteps, insertionIndex, i == progressList.length - 1);
    model.innerHTML += formattedStr;
  }

  document.getElementById("count-label").innerText = goalStepCountLabel.replace("-", metadata.count)
  document.getElementById("page-label").innerText = pageLabel.replace("-", metadata.page + 1).replace("-", metadata.maxPages);

  switch(metadata.sortLabel){
    case "UNCOMPLETE":
      document.getElementById("uncomplete").checked = true;
      document.getElementById("all").checked = false;
      document.getElementById("complete").checked = false;
      break;
    case "COMPLETE":
      document.getElementById("uncomplete").checked = false;
      document.getElementById("all").checked = false;
      document.getElementById("complete").checked = true;
      break;
    case "ALL":
      document.getElementById("uncomplete").checked = false;
      document.getElementById("all").checked = true;
      document.getElementById("complete").checked = false;
      break;
    default:
      break;
  }
  return metadata.goalSteps;
}

function getFormattedStr(index, goalStepObj, listSize, insertionIndex, isLast) {
  let formattedStr = goalStep.replace("stepMessage", stepMessage).replace("stepMessage", stepMessage).replace("arrayNum", "" + index);
  if(index == 0) {
    formattedStr = formattedStr.replace(stepMessage, "Start").replace(stepMessage, "Start"); 
  }
  else if(index == listSize - 1) {
    formattedStr = formattedStr.replace(stepMessage, "Goal").replace(stepMessage, "Goal");
  }
  else {
    formattedStr = formattedStr.replace("number", index).replace("number", index);
  }

  if(isComplete(goalStepObj)) {
    formattedStr = formattedStr.replace("buttonType", completedButtonStyle);
  }
  else {
    formattedStr = formattedStr.replace("buttonType", unCompletedButtonStyle);
  }

  if(index === insertionIndex) {
    formattedStr += rightArrowFill.replace("infoMsg", goalStepObj.exerciseString);
  }
  else {
    formattedStr += rightArrow;
  }

  if(isLast) {
    formattedStr = formattedStr.replace("goal-desc", "goal-desc-no-border");
  }

  return formattedStr;
}

function isComplete(goalStepObj) {
  return goalStepObj.tag.includes("Complete");
}

$(document).on("mouseover", "button[name='" + viewStep + "']", async function() {
    const value = parseInt($(this).val());
    if(viewingIndex != value) {
      viewingIndex = value;
      await updateModelAndStats(value);
    }
});

$(document).on("click", "input[name='sorting']", async function() {
    const params = new URLSearchParams();
    params.append('sorting', $(this).val());
    await fetch('/pagin', {method: 'POST', body: params});
    await updateModelAndStats(-1);
});

$(document).on("click", "button[name='move-page']", async function() {
    const params = new URLSearchParams();
    params.append('move-page', $(this).val());
    await fetch('/pagin', {method: 'POST', body: params});
    await updateModelAndStats(-1);
});

async function updateModelAndStats(insertionIndex) {
  const goalStepCount = await formatModel(insertionIndex);
  const params = new URLSearchParams();
  params.append('insertion', insertionIndex);
  await fetch('/stats', {method: 'POST', body: params});
  await loadPercentages(insertionIndex, goalStepCount - 1);
}