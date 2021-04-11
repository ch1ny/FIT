$('#start').click(() => {
	if (getCookie("user") == "" || getCookie("sessionid") == "") {
		alert("发现您处于未登录状态，将为您跳转至登录页面");
		plus.webview.open("./login.html");
	} else {
		// camera();
		$('#CoASelector').fadeIn();
		$('#CoASelector #selector').animate({
			"top": "90vh"
		}, 300);
	}
})

$('#cloth').click(() => {
	if (getCookie("user") == "" || getCookie("sessionid") == "") {
		alert("发现您处于未登录状态，将为您跳转至登录页面");
		plus.webview.open("./login.html");
	} else {
		// camera();
		$('#CoASelector').fadeIn();
		$('#CoASelector #selector').animate({
			"top": "90vh"
		}, 300);
	}
})

$('#closeCoA').click(() => {
	$('#CoASelector').fadeOut();
	$('#CoASelector #selector').animate({
		"top": "100vh"
	});
})

$('#CoASelector #selector').children().eq(0).click(() => {
	camera($('#photo').attr("src").length);
	$('#closeCoA').click();
})

$('#CoASelector #selector').children().eq(1).click(() => {
	album($('#photo').attr("src").length);
	$('#closeCoA').click();
})

$('#setting').click(() => {
	alert(plus.navigator.getCookie(ourServerURL));
})

$('#clipBtn').click(() => {
	$('#cutting').css("display", "none");
	$('#true').css("visibility", "visible");
	$('body').css("backgroundColor", "#FFF8E6");
})

$('#backToIndex').click(() => {
	window.location.href = "./index.html";
})

$('#toScene').click(() => {
	plus.nativeUI.showWaiting("正在上传图片...");
	$.ajax({
		type: "POST",
		url: ourServerURL + "picture/scene",
		dataType: "json",
		success(msg) {
			// seg.work();
			// alert($('#photo').attr("src"));
			// alert(seg.getImage());
			// $('#photo').attr("src", seg.getImage());
			plus.nativeUI.closeWaiting();
			if (msg.ret == 0) {
				var sceneNum = msg.allScene.length;
				var i = $('#scene #usualCont').children().length + $('#scene #recommendCont').children()
					.length;
				var usual = "";
				var recom = "";
				for (; i < 2 && i < sceneNum; i++) {
					usual += "<button class=\"usuals\"><span>" + msg.allScene[i].scene +
						"</span></button>";
				}
				$('#scene #usualCont').append(usual);
				$('#scene #usualCont').delegate("button", "click", function () {
					var self = $(this);
					var span = self.children();
					scene = span.text();
					span.addClass("chosen");
					self.siblings().children().removeClass("chosen");
					$('#scene .recommends span').removeClass("chosen");
				});
				for (; i < 5 && i < sceneNum; i++) {
					recom += "<button class=\"recommends\"><span>" + msg.allScene[i].scene +
						"</span></button>";
				}
				$('#scene #recommendCont').append(recom);
				$('#scene #recommendCont').delegate("button", "click", function () {
					var self = $(this);
					var span = self.children();
					scene = span.text();
					span.addClass("chosen");
					self.siblings().children().removeClass("chosen");
					$('#scene .usuals span').removeClass("chosen");
				});
				$('#toScene').css("visibility", "hidden");
				$('#backToIndex').css("visibility", "hidden");
				$('#photo').animate({
					top: "-50vh",
					width: "80%",
					left: "10%"
				}, 500);
				$('#scene').animate({
					top: "30vh"
				}, 500);
			} else {
				alert(msg.ret + '\n' + msg.msg);
				if (msg.ret == 302) {
					window.location.href = "./login.html";
				}
			}
		},
		error(msg) {
			plus.nativeUI.closeWaiting();
			alert("啊哦！服务器好像出了点问题呢！");
		}
	})
	// $('#toScene').css("visibility", "hidden");
	// $('#backToIndex').css("visibility", "hidden");
	// $('#photo').animate({
	// 	top: "-50vh",
	// 	width: "80%",
	// 	left: "10%"
	// }, 500);
	// $('#scene').animate({
	// 	top: "30vh"
	// }, 500);
})

$('#toUpload').click(() => {
	$('#photo').animate({
		top: "7vh",
		width: "90%",
		left: "5%"
	}, 500);
	$('#scene').animate({
		top: "101vh"
	}, 500, function () {
		$('#toScene').css("visibility", "visible");
		$('#backToIndex').css("visibility", "visible");
	});
})

$('#toStyle').click(() => {
	$.ajax({
		type: "POST",
		url: ourServerURL + "picture/style",
		dataType: "json",
		success(msg) {
			if (msg.ret == 0) {
				var styleNum = msg.allStyle.length;
				var i = $('#style #usualCont').children().length + $('#style #recommendCont').children()
					.length;
				var usual = "";
				var recom = "";
				for (; i < 2 && i < styleNum; i++) {
					usual += "<button class=\"usuals\"><span>" + msg.allStyle[i].style +
						"</span></button>";
				}
				$('#style #usualCont').append(usual);
				$('#style #usualCont').delegate("button", "click", function () {
					var self = $(this);
					var span = self.children();
					style = span.text();
					span.addClass("chosen");
					self.siblings().children().removeClass("chosen");
					$('#style .recommends span').removeClass("chosen");
				});
				var array = new Array();
				while (array.length < (styleNum - 2)) {
					var random = parseInt(Math.random() * (styleNum - 2)) + 2;
					if (array.indexOf(random) == -1) {
						array.push(random);
					}
				}
				for (; i < 5 && i < styleNum; i++) {
					recom += "<button class=\"recommends\"><span>" + msg.allStyle[array.pop()].style +
						"</span></button>";
				}
				$('#style #recommendCont').append(recom);
				$('#style #recommendCont').delegate("button", "click", function () {
					var self = $(this);
					var span = self.children();
					style = span.text();
					span.addClass("chosen");
					self.siblings().children().removeClass("chosen");
					$('#style .usuals span').removeClass("chosen");
				});
				$('#style').animate({
					top: "30vh"
				}, 500);
				$('#scene').animate({
					top: "101vh"
				}, 500);
			}
		},
		error() {
			alert("啊哦！服务器好像出了点问题呢！");
		}
	})
	// $('#style').animate({
	// 	top: "30vh"
	// }, 500);
	// $('#scene').animate({
	// 	top: "101vh"
	// }, 500);
})

$('#_toScene').click(() => {
	$('#style').animate({
		top: "101vh"
	}, 500);
	$('#scene').animate({
		top: "30vh"
	}, 500);
})

function refreshAnimation() {
	$('.refresh').css("-webkit-animation", "refresh 1s ease-out 1");
	$('.refresh').css("-ms-animation", "refresh 1s ease-out 1");
	$('.refresh').css("animation", "refresh 1s ease-out 1");
	// 刷新css动画
	var asdf = $('.refresh');
	setTimeout(function () {
		$(asdf).css("animation", "");
	}, 1000);
}

$('#scene .refresh').click(() => {
	refreshAnimation();
})

$('#style .refresh').click(() => {
	refreshAnimation();
	$.ajax({
		type: "POST",
		url: ourServerURL + "picture/style",
		dataType: "json",
		success(msg) {
			if (msg.ret == 0) {
				$('#style #usualCont').empty();
				$('#style #recommendCont').empty();
				var styleNum = msg.allStyle.length;
				var i = 0;
				var usual = "";
				var recom = "";
				for (; i < 2 && i < styleNum; i++) {
					usual += "<button class=\"usuals\"><span>" + msg.allStyle[i].style +
						"</span></button>";
				}
				$('#style #usualCont').append(usual);
				$('#style #usualCont').delegate("button", "click", function () {
					var self = $(this);
					var span = self.children();
					style = span.text();
					span.addClass("chosen");
					self.siblings().children().removeClass("chosen");
					$('#style .recommends span').removeClass("chosen");
				});
				var array = new Array();
				while (array.length < (styleNum - 2)) {
					var random = parseInt(Math.random() * (styleNum - 2)) + 2;
					if (array.indexOf(random) == -1) {
						array.push(random);
					}
				}
				for (; i < 5 && i < styleNum; i++) {
					recom += "<button class=\"recommends\"><span>" + msg.allStyle[array.pop()].style +
						"</span></button>";
				}
				$('#style #recommendCont').append(recom);
				$('#style #recommendCont').delegate("button", "click", function () {
					var self = $(this);
					var span = self.children();
					style = span.text();
					span.addClass("chosen");
					self.siblings().children().removeClass("chosen");
					$('#style .usuals span').removeClass("chosen");
				});
			}
		},
		error() {
			alert("啊哦！服务器好像出了点问题呢！");
		}
	})

})

$('#confirm').click(() => {
	// 跳转到下一页
	if (scene != "" && style != "") {
		$('#sceneTag').text(scene);
		$('#sceneTag').css("display", "inline");
		$('#styleTag').text(style);
		$('#styleTag').css("display", "inline");
		var bitmap = new plus.nativeObj.Bitmap("test");
		bitmap.loadBase64Data($('#photo').attr("src").split(",")[1], function () {
			console.log("加载Base64图片数据成功");
			var fileName = new Date().getTime() + ".jpg"
			bitmap.save("_doc/" + fileName // 临时文件，用于移动端AI读取并分析
				, {}
				, function (i) {
					console.log('保存图片成功：' + JSON.stringify(i));
					plus.gallery.save("_doc/" + fileName, function () {
						aiboost.initOptions();
						aiboost.initAiBoost();
						aiboost.input(fileName);
						aiboost.Run();
						var suggest = aiboost.getSuggest(style, scene);
						bitmap.clear();
						console.log("处理成功");
						$('#photo').animate({
							top: "-25vh",
							width: "100%",
							height: "80vh",
							left: "0%"
						}, 500);
						$('#style').animate({
							top: "101vh"
						}, 500);
						$('#scene').animate({
							top: "101vh"
						}, 500, function () {
							$('#evaluate').fadeIn(1000);
						});
						$('#txt').text(suggest.replace(/(null)/g, scene));
					});
				}
				, function (e) {
					console.log('保存图片失败：' + JSON.stringify(e));
				});
		}, function () {
			console.log('加载Base64图片数据失败：' + JSON.stringify(e));
		});
	} else {
		var needToChoose = "";
		if (scene == "") {
			needToChoose += " 场景";
		}
		if (style == "") {
			needToChoose += "风格";
		}
		alert("选择的Tag不合法！请选择" + needToChoose);
	}
})

$('#backToStyle').click(() => {
	$('#evaluate').fadeOut(500, function () {
		$('#photo').animate({
			top: "-50vh",
			width: "80%",
			height: "75vh",
			left: "10%"
		}, 500);
		$('#style').animate({
			top: "30vh"
		}, 500);
	});
})

$('#toEffect').click(() => {
	plus.nativeUI.showWaiting("AI正在后台识别图像，请稍等...");
	$.ajax({
		type: "POST",
		url: ourServerURL + "picture/parameter",
		data: {
			img: $('#photo').attr("src").split(",")[1]
		},
		dataType: "json",
		success(msg) {
			$('#efct').attr("src", $('#photo').attr("src"));
			$('#clothChooser').empty();
			detailCloth = undefined;
			if (msg.ret == 0) {
				var clothNum = msg.category.length;
				for (var i = 0; i < clothNum; i++) {
					var el = "<div class=\"clothes\" clothIndex=" + i + "><img src=\"../icon/clothes/";
					switch (msg.category[i]) {
						case 1:
							el += "short-top";
							break;
						case 2:
							el += "long-top";
							break;
						case 3: case 4:
							el += "outwear";
							break;
						case 5:
							el += "vest";
							break;
						case 6: case 13:
							el += "sling";
							break;
						case 7:
							el += "shorts";
							break;
						case 8:
							el += "trousers";
							break;
						case 9:
							el += "skirt";
							break;
						case 10: case 11: case 12:
							el += "dress";
							break;
					}
					el += ".png" + "\"></img></div>";
					$('#clothChooser').append(el);
					var evaluateColor = [
						{ colorName: "卡其色", colorHex: "#7D6C5C" },
						{ colorName: "复古红", colorHex: "#761B22" },
						{ colorName: "中国红", colorHex: "#BD464C" },
						{ colorName: "运动灰", colorHex: "#615B5B" },
						{ colorName: "少女粉", colorHex: "#CF9BA3" },
						{ colorName: "清冷灰", colorHex: "#505050" }
					];
					var suggest = $('#txt').text();
					if (suggest.indexOf("将上衣改为") > -1) {
						var changeColor = suggest.substring(suggest.indexOf("将上衣改为") + 5, suggest.indexOf("将上衣改为") + 8);
						for (var i = 0; i < evaluateColor.length; i++) {
							if (changeColor == evaluateColor[i].colorName) {
								// alert("up=" + up + "\nr" + parseInt("0x" + evaluateColor[i].colorHex.slice(1, 3)) + "\ng" + parseInt("0x" + evaluateColor[i].colorHex.slice(3, 5)) + "\b" + parseInt("0x" + evaluateColor[i].colorHex.slice(5, 7)))
								$.ajax({
									type: "POST",
									url: ourServerURL + "picture/changeColor",
									data: {
										img: $('#photo').attr("src").split(",")[1],
										id: 0,
										r: parseInt("0x" + evaluateColor[i].colorHex.slice(1, 3)),
										g: parseInt("0x" + evaluateColor[i].colorHex.slice(3, 5)),
										b: parseInt("0x" + evaluateColor[i].colorHex.slice(5, 7)),
										isRGB: 1
									},
									dataType: "json",
									success: function (msg) {
										plus.nativeUI.closeWaiting();
										if (msg.ret == 0) {
											$('#efct').attr("src", "data:img/jpg;base64," + msg.img);
											$('#photo').fadeOut(500);
											$('#evaluate').fadeOut(500, function () {
												$('#effect').fadeIn(500);
											})
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
					} else if (suggest.indexOf("将下身改为") > -1) {
						var changeColor = suggest.substring(suggest.indexOf("将下身改为") + 5, suggest.indexOf("将下身改为") + 8);
						for (var i = 0; i < evaluateColor.length; i++) {
							if (changeColor == evaluateColor[i].colorName) {
								$.ajax({
									type: "POST",
									url: ourServerURL + "picture/changeColor",
									data: {
										img: $('#photo').attr("src").split(",")[1],
										id: down,
										r: parseInt("0x" + evaluateColor[i].colorHex.slice(1, 3)),
										g: parseInt("0x" + evaluateColor[i].colorHex.slice(3, 5)),
										b: parseInt("0x" + evaluateColor[i].colorHex.slice(5, 7)),
										isRGB: 1
									},
									dataType: "json",
									success: function (msg) {
										plus.nativeUI.closeWaiting();
										if (msg.ret == 0) {
											$('#efct').attr("src", "data:img/jpg;base64," + msg.img);
											$('#photo').fadeOut(500);
											$('#evaluate').fadeOut(500, function () {
												$('#effect').fadeIn(500);
											})
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
					}

				}
			} else if (msg.ret == "1") {
				alert("未能成功识别图片");
			}
		},
		error(response, status, xhr) {
			console.log(response); //服务器返回的信息
			console.log(status); //服务器返回的信息
			console.log(xhr.status); //状态码，要看其他的直接输出 xhr 就行
			var jsonData = JSON.stringify(response);
			alert(jsonData);
		}
	});
})

$('#backToEvaluate').click(() => {
	$('#effect').fadeOut(500, function () {
		$('#photo').fadeIn(500);
		$('#evaluate').fadeIn(500);
	})
})

$('#toFree').click(() => {
	$('#operation').css({
		"top": "100vh"
	});
	$('#efct').animate({
		"top": "15vh"
	}, 500);
	$('#effect #top').fadeOut(500);
	$('#effect #toFree').fadeOut(500, function () {
		$('#collocation').fadeIn();
		$('body').css("backgroundColor", "black");
	});
})

$('#backToEffect').click(() => {
	$('#efct').animate({
		"top": "7vh"
	}, 500);
	$('#collocation').fadeOut(500, function () {
		$('body').css("backgroundColor", "#FFF8E6");
		$('#effect #top').fadeIn();
		$('#effect #toFree').fadeIn();
	})
})

$('#complete').click(() => {
	// 这个地方应该要给后端发送请求
	$('#prev').attr("src", $('#efct').attr("src"));
	$('body').css("backgroundColor", "#FFF8E6");
	$('#collocation').fadeOut(250);
	$('#efct').fadeOut(250, function () {
		$('#preview').fadeIn(250);
	})
})

$('#backToFree').click(() => {
	$('#preview').fadeOut(500, function () {
		$('#collocation').fadeIn();
		$('#efct').fadeIn();
		$('body').css("backgroundColor", "black");
		$('#effect #top').fadeIn();
		$('#effect #ToFree').fadeIn();
	})
})

$('#purchase').click(() => {
	plus.webview.open("./purchase.html");
})

$('#toRelated').click(() => {
	$('#preview').fadeOut(500, function () {
		$('#related').fadeIn(500);
	})
	$('#container1').empty();
	$('#container2').empty();
	plus.nativeUI.showWaiting("正在获取相关搭配...");
	$.ajax({
		type: "POST",
		url: ourServerURL + "picture/others",
		data: {
			style: style
		},
		dataType: "json",
		success(msg) {
			plus.nativeUI.closeWaiting();
			var picNum = msg.picList.length;
			boxNum = 0;
			if (boxNum == 0) {
				while (boxNum < 8 && boxNum < picNum) {
					newBox(msg.picList[boxNum].pic);
				}
			}
			off_on = true;
		},
		error() {
			plus.nativeUI.closeWaiting();
			alert("啊哦！服务器好像出了点问题呢！");
		}
	})
	page = 1;
})

// $('#associated div').on('click', 'div', function () {
// 	plus.webview.open("./price.html?img=" + $(this).children("img").attr("src"));
// })

$('#backToPrev').click(() => {
	off_on = false;
	$('#associated').scrollTop(0);
	$('#related').fadeOut(0, function () {
		$('#preview').fadeIn();
	})
})

$('#jumpToIndex').click(() => {
	window.location.href = "./index.html";
})
