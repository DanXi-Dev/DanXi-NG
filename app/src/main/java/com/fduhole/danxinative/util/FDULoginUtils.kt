package com.fduhole.danxinative.util

import com.fduhole.danxinative.model.fdu.UISInfo

fun uisLoginJavaScript(info: UISInfo): String = """try{
    document.getElementById('username').value = String.raw`""" +
        info.id +
        """`;
    document.getElementById('password').value = String.raw`""" +
        info.password +
        """`;
    document.forms[0].submit();
}
catch (e) {
    try{
        document.getElementById('mobileUsername').value = String.raw`""" +
        info.id +
        """`;
        document.getElementById('mobilePassword').value = String.raw`""" +
        info.password +
        """`;
        document.forms[0].submit();
    }
    catch (e) {
        window.alert("DanXi: Failed to auto login UIS");
    }
}"""
