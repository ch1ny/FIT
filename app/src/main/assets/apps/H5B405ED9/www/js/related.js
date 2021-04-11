var boxNum = 0;

function getSrc(src) {
	return "data:image/jpg;base64," + src;
}

function getTags() {
	if (style != "")
		var tags = "#" + style;
	return tags;
}

function newBox(src) {
	var box = $('<div id=\"box' + boxNum + '\">' +
		'<img id=\"img' + boxNum + '\" src=\"' + getSrc(src) + '\" class=\"newImg\" />' +
		'<span>' + getTags() + ' ￥98.00</span>' +
		'</div>');
	if (boxNum % 2 == 0) {
		$('#container1').append(box);
	} else {
		$('#container2').append(box);
	}
	boxNum++;
}

var page = 1; //分页码
var off_on = true; //分页开关(滚动加载方法 1 中用的)
// var timers = null; //定时器(滚动加载方法 2 中用的)

//加载数据
var LoadingDataFn = function () {
	// var dom = '';
	plus.nativeUI.showWaiting("正在获取更多推荐...");
	$.ajax({
		type: "POST",
		url: ourServerURL + "picture/others",
		data: {
			style: style
		},
		dataType: "json",
		success(msg) {
			var picNum = msg.picList.length;
			var picIndex = 0;
			while (picIndex < 8 && picIndex < picNum) {
				newBox(msg.picList[picIndex++].pic);
			}
			off_on = true; //[重要]这是使用了 {滚动加载方法1}  时 用到的 ！！！[如果用  滚动加载方法1 时：off_on 在这里不设 true的话， 下次就没法加载了哦！]
			plus.nativeUI.closeWaiting();
		}
	})
};

//初始化， 第一次加载
// $(document).ready(function() {
// 	LoadingDataFn();
// });

//底部切换
$(document.body).on('click', '#footer div', function () {
	$(this).addClass('active').siblings().removeClass('active');
});

//滚动加载方法1
$('#associated').scroll(function () {
	//当滚动条离底部10px时开始加载下一页的内容
	if (($(this)[0].scrollTop + $(this).height() + 10) >= $(this)[0].scrollHeight) {
		//这里用 [ off_on ] 来控制是否加载 （这样就解决了 当上页的条件满足时，一下子加载多次的问题啦）
		if (off_on) {
			off_on = false;
			LoadingDataFn(); //调用执行上面的加载方法
			page++;
			console.log("第" + page + "页");
		}
	}
});

//滚动加载方法2
// $('#associated').scroll(function () {
//     //当滚动条离底部60px时开始加载下一页的内容
//     if (($(this)[0].scrollTop + $(this).height() + 60) >= $(this)[0].scrollHeight) {
//         clearTimeout(timers);
//         //这里还可以用 [ 延时执行 ] 来控制是否加载 （这样就解决了 当上页的条件满足时，一下子加载多次的问题啦）
//         timers = setTimeout(function () {
//             page++;
//             LoadingDataFn(); //调用执行上面的加载方法
//         }, 300);
//     }
// });
