$(function () {
    settime($("#getCode"));
    $("#getCode").click(function () {
        var emailDiv = $("input[name=email]").parent();
        if (emailDiv.find("span").length > 0) {
            return;
        }
        var email = $("input[name=email]").val();
        if (email != null && email != "") {
            $.ajax({
                "url": "http://47.92.69.253/user/emailIsExist/" + email,
                "async": false,	//校验邮箱是否存在需要同步再发验证码
                "type": "GET",
                "dataType": "json",
                "success": function (data) {
                    if (data.status != 200) {
                        $("input[name=email]").parent().find("span").remove();
                        $.ajax({
                            "url": "http://47.92.69.253/user/sendCode",
                            "async": false,	//不同步倒计时取可能从sesiion中取不到值
                            "type": "POST",
                            "data": JSON.stringify({email: email, yt: "zhmm"}),
                            //"dataType":"json",	//返回值是void,如果带这个属性不会调用回调函数
                            "contentType": "application/json;charset=UTF-8",
                            "success": function (data) {
                                sendemail();
                            }
                        });
                    } else {
                        if ($("#emailIsExist").length == 0) {
                            $("input[name=email]").parent().find("span").remove();
                            $("input[name=email]").after("<span id='emailIsExist' class='tips'>" + "邮箱不存在</span>");
                        }

                    }
                }
            });

        } else {
            if ($("#emailIsNull").length <= 0) {
                $("#getCode").parent().find("span").remove();
                $("#getCode").after("<span id='emailIsNull' for='code' class='tips'>请先填写邮箱</span>");
            }
            return;
        }
    });

    $("input[name=email]").focus(function () {
        $("#emailIsExist").remove();
    });
    $("input[name=email]").blur(function () {
        var email = $("input[name=email]").val();
        if (email == null || email == "") {
            return;
        }
        $.ajax({
            "url": "http://47.92.69.253/user/emailIsExist/" + email,
            "type": "GET",
            "dataType": "json",
            "success": function (data) {
                if (data.status == 200) {
                    if ($("#emailIsExist").length == 0) {
                        $("input[name=email]").parent().find("label").remove();
                        $("input[name=email]").after("<span id='emailIsExist' class='tips'>邮箱不存在</span>");
                    }
                }
            }
        });
    });

    $("#zhmm").click(function () {
        if ($("input[name=email]").val() == null || $("input[name=email]").val() == "") {
            $("input[name=email]").parent().find("span").remove();
            $("input[name=email]").after("<span class='tips'>请先填写邮箱</span>");
            return;
        }
        if ($("input[name=code]").val() == null || $("input[name=code]").val() == "") {
            $("input[name=code]").parent().find("span").remove();
            $("input[name=code]").after("<span class='tips'>请填写验证码</span>");
            return;
        }
        if ($("input[name=password]").val().length < 6 || $("input[name=password]").val().length > 16) {
            $("input[name=password]").parent().find("span").remove();
            $("input[name=password]").after("<span class='tips'>请输入6~16位的新密码</span>");
            return;
        }
        if ($("#emailIsExist").length != 0) {
            return;
        }
        var flag = false;
        var email = $("input[name=email]").val();
        var code = $("input[name=code]").val();
        $.ajax({
            "async": false,	//同步校验才能成功
            "url": "http://47.92.69.253/user/codeIsTrue",
            "type": "POST",
            "data": JSON.stringify({email: email, code: code, yt: "zhmm"}),
            "contentType": "application/json;charset=UTF-8",
            "dataType": "json",
            "success": function (data) {
                if (data.status == 200) {
                    flag = true;
                } else {
                    if ($("#codeIsFalse").length == 0) {
                        $("#getCode").parent().find("label").remove();
                        $("#getCode").parent().find("span").remove();
                        $("#getCode").after("<label id='codeIsFalse' class='tips'>" + data.msg + "</label>");
                    }
                }
            }
        });
        if (flag) {
            $("#zhmmForm").submit();
        }
    });
});

//获取邮件按钮倒计时
function sendemail() {
    var obj = $("#getCode");
    $("#getCode").parent().find("label").remove();
    $("#getCode").parent().find("span").remove();
    $("#getCode").after("<span id='tip' class='tips'>可能在邮件垃圾箱里哦</span>");
    // setTimeout(function() {
    // 	$('#tip').remove();
    // }, 2000);
    settime(obj);

}

var countdown = 0;

function settime(obj) { //发送验证码倒计时
    $.ajax({
        "url": "http://47.92.69.253/user/getdjs?yt=zhmm",
        "async": false,	//不同步countdown的值为0
        "type": "GET",
        "dataType": "json",
        "success": function (data) {
            countdown = parseInt(data);
        }
    });
    if (countdown == 0) {
        obj.attr('disabled', false);
        //obj.removeattr("disabled");
        obj.val("点击获取");
        return;
    } else {
        obj.attr('disabled', true);
        obj.val("重新发送(" + countdown + ")");
    }
    setTimeout(function () {
            settime(obj)
        }
        , 1000)
}