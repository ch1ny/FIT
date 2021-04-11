function album(operation) {
	// 从相册中选择图片
	console.log("从相册中选择图片:");
	plus.gallery.pick(function (path) {
		plus.io.resolveLocalFileSystemURL(path, function (entry) {
			entry.file(function (file) {
				var fileReader = new plus.io.FileReader();
				// alert("getFile:" + JSON.stringify(file));
				fileReader.readAsDataURL(file, 'utf-8');
				fileReader.onloadend = function (evt) {
					// alert("11" + evt);
					// alert("evt.target" + evt.target);
					var base64 = evt.target.result;
					if (operation == 0) {
						clipPhoto(base64);
					} else {
						var self = $('#operation #container #styles #ZiDingYi');
						plus.nativeUI.showWaiting("正在处理图片，请稍后...");
						$.ajax({
							type: "POST",
							url: ourServerURL + "picture/changeTexture",
							data: {
								id: 0,
								img: $('#photo').attr("src").split(",")[1],
								type: 0,
								clothe: detailCloth,
								isCustomized: 1,
								texture: base64.split(",")[1]
							},
							dataType: "json",
							success: function (msg) {
								plus.nativeUI.closeWaiting();
								console.log(msg.ret);
								if (msg.ret == 0) {
									self.css({
										"border": "2.5px solid #ffc33a"
									});
									self.parent().children(".preStyles").removeClass("selected");
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
				// alert(file.size + '--' + file.name);
			});
		}, function (e) {
			alert("Resolve file URL failed: " + e.message);
		});
	}, function (e) {
		console.log("取消选择图片");
	}, { filter: "image" });
}