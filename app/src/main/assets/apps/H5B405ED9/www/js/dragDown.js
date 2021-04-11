var dragHeightLimit = deviceHeight * 0.3;

$('#scene #dragBar').bind('touchmove', function (e) {
  e.preventDefault();
  var touch = e.originalEvent.touches[0] || e.originalEvent.changedTouches[0];
  var divY = touch.pageY - deviceHeight * 0.01;
  if (divY < dragHeightLimit) {
    divY = dragHeightLimit;
  }
  $('#photo').css('top', divY - deviceHeight * 0.8 + 'px');
  $('#scene').css('top', divY + 'px');
  if ($('#scene').offset().top > dragHeightLimit * 1.8) {
    $('#scene').css('top', dragHeightLimit * 1.8 + 'px');
    $('#photo').css('top', deviceHeight * -0.27 + 'px');
  }
});

$('#scene #dragBar').bind('touchend', function (e) {
  e.preventDefault();
  if ($('#scene').offset().top >= dragHeightLimit * 1.75) {
    $('#toUpload').click();
  } else {
    $('#scene').css('top', '30vh');
    $('#photo').css('top', '-50vh');
  }
});

$('#style #dragBar').bind('touchmove', function (e) {
  e.preventDefault();
  var touch = e.originalEvent.touches[0] || e.originalEvent.changedTouches[0];
  var styleY = touch.pageY - deviceHeight * 0.01;
  if (styleY < dragHeightLimit) {
    styleY = dragHeightLimit;
  }
  $('#scene').css('top', deviceHeight - styleY + 'px');
  $('#style').css('top', styleY + 'px');
  if ($('#style').offset().top > dragHeightLimit * 1.8) {
    $('#style').css('top', dragHeightLimit * 1.8 + 'px');
    $('#scene').css('top', deviceHeight - dragHeightLimit * 1.8 + 'px');
  }
});

$('#style #dragBar').bind('touchend', function (e) {
  e.preventDefault();
  if ($('#style').offset().top >= dragHeightLimit * 1.75) {
    $('#_toScene').click();
  } else {
    $('#style').css('top', '30vh');
    $('#scene').css('top', '101vh');
  }
});