function getCookie(cname) {
    for (var i = 0; i < cNum; i++) {
        if (cName[i] == cname || cName[i] == " " + cname)
            return cVal[i];
    }
    return "";
}