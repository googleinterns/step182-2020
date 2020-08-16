const infoMsg = "rada---------------------------------------------------------------------------------------------------------------------------------------------------<br>rada<br>rada<br>rada<br>rada<br>rada<br>rada<br>";

$(function() {

  $(".progress").each(function() {

    var value = $(this).attr('data-value');
    var left = $(this).find('.progress-left .progress-bar');
    var right = $(this).find('.progress-right .progress-bar');

    if (value > 0) {
      if (value <= 50) {
        right.css('transform', 'rotate(' + percentageToDegrees(value) + 'deg)')
      } else {
        right.css('transform', 'rotate(180deg)')
        left.css('transform', 'rotate(' + percentageToDegrees(value - 50) + 'deg)')
      }
    }

  })

  function percentageToDegrees(percentage) {
    return percentage / 100 * 360
  }

});

function displayGoalStep() {
  const info = document.getElementById("button0");
  if(info.innerHTML === infoMsg) {
    info.innerHTML = ""; 
    info.style.height = "50%";
  }
  else {
    info.innerHTML = infoMsg; 
    info.style.height = "";
  }
}