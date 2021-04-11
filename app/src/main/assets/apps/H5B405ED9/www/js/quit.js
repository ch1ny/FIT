document.addEventListener('plusready', function (a) {
    var firstQuit = null;
    plus.key.addEventListener('backbutton', function () {
        //首次按键，提示‘再按一次退出应用’
        if (!firstQuit) {
            firstQuit = new Date().getTime();
            console.log('再按一次退出应用');
            $('#exitInfo').fadeIn();
            $('#exitInfo').fadeOut(1000, function () {
                firstQuit = null;
            });
        } else {
            if (new Date().getTime() - firstQuit < 1000) {
                plus.runtime.quit();
            }
        }
    }, false);
});