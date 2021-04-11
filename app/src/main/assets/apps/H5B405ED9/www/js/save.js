//保存到本地
$('#save').click(function () {
    var timestamp = (new Date()).valueOf();
    var bitmap = new plus.nativeObj.Bitmap("save");
	bitmap.loadBase64Data($('#prev').attr("src"), function() {
		bitmap.save( "_doc/" + timestamp + ".jpg"
			,{
				quality:100
			}
			,function(i){
				console.log('转化Bitmap成功：'+JSON.stringify(i));
            plus.gallery.save("_doc/" + timestamp + ".jpg", function () {
                alert("保存成功");
            }, function () {
                alert("保存失败，请重试");
            });
			}
			,function(e){
				console.log('转化Bitmap失败：'+JSON.stringify(e));
			});
	})
})