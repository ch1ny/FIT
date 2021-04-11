var detailCloth, detailIndex, detailFrom, fromIndex; // 选择的衣物编号、具体的图案编号、图案上层类别、类别序号

$('#clothChooser').on('click', '.clothes', function () {
    $('#operation').animate({
        "top": "80vh"
    }, 200);
    $('#efct').animate({
        "top": "7vh"
    }, 200);
    detailCloth = $(this).attr("clothIndex");
    $(this).addClass("selected");
    $(this).siblings().removeClass("selected");
    $('#operation #chooser #chooseColor').click();
    $('#operation #container #colors').children().removeClass("selected");
    $('#styles').children().removeClass("selected");
    detailIndex = undefined;
    detailFrom = undefined;
})

$('#operation #chooser #chooseColor').click(() => {
    $('#operation #chooser #chooseColor').css({
        "backgroundColor": "#ffffff",
        "color": " #ffc33a"
    });
    $('#operation #chooser #chooseStyle').css({
        "backgroundColor": "#f1f1f1",
        "color": " #969696"
    });
    $('#operation #container #colors').css("display", "block");
})

$('#operation #chooser #chooseStyle').click(() => {
    $('#operation #chooser #chooseStyle').css({
        "backgroundColor": "#ffffff",
        "color": " #ffc33a"
    });
    $('#operation #chooser #chooseColor').css({
        "backgroundColor": "#f1f1f1",
        "color": " #969696"
    });
    $('#operation #container #colors').css("display", "none");
})

$('#operation #container #colors .colour').click(function () {
    $('#colorInfo').fadeOut();
    $('#colorInfo').text($(this).attr("colorName"));
    $('#colorInfo').fadeIn();
    $('#colorInfo').fadeOut(1000);
    var self = $(this);
    plus.nativeUI.showWaiting("正在处理图片，请稍后...");
    $.ajax({
        type: "POST",
        url: ourServerURL + "picture/changeColor",
        data: {
            img: $('#photo').attr("src").split(",")[1],
            id: detailCloth,
            color: $(this).attr("colorId"),
            isRGB: 0
        },
        dataType: "json",
        success: function (msg) {
            plus.nativeUI.closeWaiting();
            console.log(msg.ret);
            if (msg.ret == 0) {
                $('#operation #container #colors .myColour').css({
                    "border": "2.5px solid white"
                });
                self.addClass("selected");
                self.siblings().removeClass("selected");
                $('#efct').attr("src", "data:img/jpg;base64," + msg.img);
            } else {
                alert("图片处理失败");
            }
        },
        error(response, status, xhr) {
            plus.nativeUI.closeWaiting();
            console.log(response); //服务器返回的信息
            console.log(status); //服务器返回的信息
            console.log(xhr.status); //状态码，要看其他的直接输出 xhr 就行
            var jsonData = JSON.stringify(response);
            alert("啊哦！服务器好像出了点问题");
        }
    });
})

var app = new Framework7();
var colorPicker = app.colorPicker.create({
    inputEl: '#operation #container #colors .myColour',
    targetEl: '#operation #container #colors .myColour',
    targetElSetBackgroundColor: true,
    modules: ['sb-spectrum', 'hue-slider'],
    openIn: 'popover',
    value: {
        hex: '#000000',
    },
    on: {
        change: function () {
            console.log('HEX value is ' + colorPicker.getValue().hex);
        },
        close: function () {
            $('#colorInfo').fadeOut();
            $('#colorInfo').text("自定义");
            $('#colorInfo').fadeIn();
            $('#colorInfo').fadeOut(1000);
            plus.nativeUI.showWaiting("正在处理图片，请稍后...");
            var rgb = colorPicker.getValue().rgb;
            $.ajax({
                type: "POST",
                url: ourServerURL + "picture/changeColor",
                data: {
                    img: $('#photo').attr("src").split(",")[1],
                    id: detailCloth,
                    r: rgb[0],
                    g: rgb[1],
                    b: rgb[2],
                    isRGB: 1
                },
                dataType: "json",
                success: function (msg) {
                    plus.nativeUI.closeWaiting();
                    if (msg.ret == 0) {
                        $('#operation #container #colors .myColour').css({
                            "border": "2.5px solid " + colorPicker.getValue().hex
                        });
                        $('#operation #container #colors .colour').removeClass("selected");
                        $('#efct').attr("src", "data:img/jpg;base64," + msg.img);
                    } else {
                        alert("图片处理失败");
                    }
                },
                error(response, status, xhr) {
                    plus.nativeUI.closeWaiting();
                    console.log(response); //服务器返回的信息
                    console.log(status); //服务器返回的信息
                    console.log(xhr.status); //状态码，要看其他的直接输出 xhr 就行
                    var jsonData = JSON.stringify(response);
                    alert("啊哦！服务器好像出了点问题");
                }
            });
        }
    }
})

$('#operation #container #styles .preStyles').click(function () {
    $('#styleDetails').empty();
    var element = "<div><img src=\"../icon/Style/FanHui.jpg\"></img></div>";
    $('#styleDetails').append(element);
    var img_num = 0;
    var from = $(this).children("img").attr("src").substring($(this).children("img").attr("src").lastIndexOf("/") + 1, $(this).children("img").attr("src").lastIndexOf("."));
    var imgPath = "../icon/Style/" + from + "/";
    var textureNum = texture.getPicNum(from);
    if (from != detailFrom) {
        while (img_num < textureNum) {
            element = "<div class=\"style\" from=\"" + from + "\"><img src=\"" + imgPath + img_num++ + ".jpg" + "\"></img></div>";
            $('#styleDetails').append(element);
        }
    } else {
        while (img_num < textureNum) {
            element = "<div class=\"style";
            if (img_num == detailIndex) {
                element += " selected";
            }
            element += "\" from=\"" + from + "\"><img src=\"" + imgPath + img_num++ + ".jpg" + "\"></img></div>";
            $('#styleDetails').append(element);
        }
    }
    $('#operation #container #styles').css("display", "none");
})

$('#styleDetails').on('click', 'div', function () {
    $('#operation #container #styles').css("display", "block");
})

$('#styleDetails').on('click', '.style', function () {
    var self = $(this);
    detailFrom = self.attr("from");
    fromIndex = $('#styles .preStyles').index(self.parent().siblings('#styles').children('#' + detailFrom));
    detailIndex = self.children().attr("src").substring(self.children().attr("src").lastIndexOf("/") + 1, self.children().attr("src").lastIndexOf("."));
    self.addClass("selected");
    self.siblings().removeClass("selected");
    self.parent().siblings('#styles').children().removeClass("selected");
    self.parent().siblings('#styles').children('#' + detailFrom).addClass("selected");
    plus.nativeUI.showWaiting("正在处理图片，请稍后...");
    $.ajax({
        type: "POST",
        url: ourServerURL + "picture/changeTexture",
        data: {
            id: detailIndex,
            img: $('#photo').attr("src").split(",")[1],
            type: (fromIndex + 1),
            clothe: detailCloth,
            isCustomized: 0
        },
        dataType: "json",
        success: function (msg) {
            plus.nativeUI.closeWaiting();
            console.log(msg.ret);
            if (msg.ret == 0) {
                self.addClass("selected");
                self.siblings().removeClass("selected");
                $('#operation #container #styles #ZiDingYi').css({
                    "border": "2.5px solid white"
                });
                $('#efct').attr("src", "data:img/jpg;base64," + msg.img);
            } else {
                alert("图片处理失败");
            }
        },
        error(response, status, xhr) {
            plus.nativeUI.closeWaiting();
            console.log(response); //服务器返回的信息
            console.log(status); //服务器返回的信息
            console.log(xhr.status); //状态码，要看其他的直接输出 xhr 就行
            // var jsonData = JSON.stringify(response);
            alert("啊哦！服务器好像出了点问题");
        }
    });
})

$('#operation #container #styles #ZiDingYi').click(() => {
    $('#CoASelector').fadeIn();
    $('#CoASelector #selector').animate({
        "top": "90vh"
    }, 300);
})