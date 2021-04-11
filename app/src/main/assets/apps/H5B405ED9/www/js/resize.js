$(document).ready(function () {
    //屏幕当前的高度
    var oHeight = $(window).height();
    $(window).resize(function () {
        if ($(window).height() < oHeight) {
            $('#photo').css({
                "height": 0.75 * oHeight + "px",
                "top": -0.5 * oHeight + "px"
            });
            if ($('#scene').offset().top < $(window).height()) {
                $('#scene').css({
                    "height": 0.7 * oHeight + "px",
                    "top": 0.3 * oHeight + "px"
                });
            }
            if ($('#style').offset().top < $(window).height()) {
                $('#style').css({
                    "height": 0.7 * oHeight + "px",
                    "top": 0.3 * oHeight + "px"
                });
            }
            $('.decoration').css({
                "top": 0.01 * oHeight + "px"
            });
        }
    });
});