function clipPhoto(base64) {
	var pc = new PhotoClip('#clipArea', {
		size: [deviceWidth * 0.84, deviceHeight * 0.7], // 截取框大小。
		outputSize: [0, 0], // 输出图像大小。
		outputQuality: 1,
		//adaptive: ['60%', '80%'], // 截取框自适应。设置该选项后，size 选项将会失效，此时 size 进用于计算截取框的宽高比例。
		outputType: 'jpg', // 指定输出图片的类型，可选 'jpg' 和 'png' 两种种类型，默认为 'jpg'。
		img: base64,
		view: '#view',
		ok: '#clipBtn',
		//img: 'img/mm.jpg',
		loadStart: function () {
			console.log('开始读取照片');
			$('body').css("backgroundColor", "white");
			$('#fake').css("display", "none");
			$('#cutting').css("display", "initial");
			plus.navigator.setStatusBarBackground('#FFF8E6');
			plus.navigator.setStatusBarStyle('dark');
			var rotate = 0;
			$('#clipTurn').click(() => {
				pc.rotate(rotate += 90, 100);
				if (rotate == 360) {
					rotate = 0;
				}
			})
		},
		loadComplete: function () {
			console.log('照片读取完成');
		},
		done: function (dataURL) {
			$('#photo').attr("src", dataURL);
		},
		fail: function (msg) {
			alert(msg);
		}
	});
}