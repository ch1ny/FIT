var scene = "";
var style = "";

// 选择场景
$('#scene .usuals').click(function () {
    var self = $(this);
    var span = self.children();
    scene = span.text();
    span.addClass("chosen");
    self.siblings().children().removeClass("chosen");
    $('#scene .recommends span').removeClass("chosen");
})
$('#scene .recommends').click(function () {
    var self = $(this);
    var span = self.children();
    scene = span.text();
    span.addClass("chosen");
    self.siblings().children().removeClass("chosen");
    $('#scene .usuals span').removeClass("chosen");
})

// 选择风格
$('#style').on('click', '.usuals', function () {
    var self = $(this);
    var span = self.children();
    style = span.text();
    span.addClass("chosen");
    self.siblings().children().removeClass("chosen");
    $('#style .recommends span').removeClass("chosen");
})
$('#style').on('click', '.recommends', function () {
    var self = $(this);
    var span = self.children();
    style = span.text();
    span.addClass("chosen");
    self.siblings().children().removeClass("chosen");
    $('#style .usuals span').removeClass("chosen");
})