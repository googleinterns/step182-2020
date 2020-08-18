const infoMsg = "rada---------------------------------------------------------------------------------------------------------------------------------------------------<br>rada<br>rada<br>rada<br>rada<br>rada<br>rada<br>";

$(function() {
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
  })

  function percentageToDegrees(percentage) {
    return percentage / 100 * 360;
  }
});

/* Mock function for what happens when a step in progress model is clicked on progress page. */
function displayGoalStep() {
  const info = document.getElementById("button0");
  if(info.innerHTML === infoMsg) {
    info.innerHTML = ""; 
    info.style.height = "25%";
  }
  else {
    info.innerHTML = infoMsg; 
    info.style.height = "";
  }
}

const rightArrow = "<div class=\"goal-desc\"></div>";
const stepMessage = "Step number";
const viewStep = "view-step";
const goalStep = "<div class=\"row step\"><button name=\"" + viewStep + "\" title=\"stepMessage\" type=\"button\" class=\"btn buttonType btn-sm\" data-container=\"body\" data-toggle=\"popover\" data-trigger=\"hover\" data-placement=\"right\" data-content=\"progress\" data-html=\"true\" value=\"arrayNum\">stepMessage</button></div>";
const completedButtonStyle = "btn-dark";
const unCompletedButtonStyle = "btn-light";

const paginationButton = "<li class=\"page-item\"><button name=\"move-page\" class=\"page-link\" value=\"pageNum\">pageNum</button></li>"

async function loadProgressModel() {
  await formatModel(-1);
  $('[data-toggle="popover"]').popover();
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
    let formattedStr = getFormattedStr(startIndex + i, progressList[i], metadata.goalSteps, insertionIndex);
    model.innerHTML += formattedStr;
    if(i != progressList.length - 1) {
      model.innerHTML += rightArrow;
    }
  }
}

function getFormattedStr(index, goalStepObj, listSize, insertionIndex) {
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

  if(goalStepObj.complete) {
    formattedStr = formattedStr.replace("buttonType", completedButtonStyle);
  }
  else {
    formattedStr = formattedStr.replace("buttonType", unCompletedButtonStyle);
  }
  if(index === insertionIndex) {
    formattedStr = formattedStr.replace("progress", goalStepObj.exerciseString);
  }
  return formattedStr;
}

$(document).on("click", "button[name='" + viewStep + "']", async function() {
    $('[role="tooltip"]').remove();
    await formatModel(parseInt($(this).val()));
    $('[data-toggle="popover"]').popover();
});

$(document).on("click", "input[name='sorting']", async function() {
    const params = new URLSearchParams();
    params.append('sorting', $(this).val());
    await fetch('/pagin', {method: 'POST', body: params});
    await formatModel(-1);
});

$(document).on("click", "button[name='move-page']", async function() {
    const params = new URLSearchParams();
    params.append('move-page', $(this).val());
    await fetch('/pagin', {method: 'POST', body: params});
    await formatModel(-1);
});