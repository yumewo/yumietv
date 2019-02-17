$(function () {
    settime($("#getCode"));
    $("#getCode").click(function () {
        var emailDiv = $("input[name=email]").parent();
        if (emailDiv.find("span").length > 0 && emailDiv.find("label").length > 0) {
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
                        if ($("#emailIsExist").length == 0) {
                            $("input[name=email]").parent().find("label").remove();
                            $("input[name=email]").after("<span id='emailIsExist' class='tips'>" + data.msg + "</span>");
                        }
                    } else {
                        $("input[name=email]").parent().find("label").remove();
                        $("input[name=email]").parent().find("span").remove();
                        $.ajax({
                            "url": "http://47.92.69.253/user/sendCode",
                            "async": false,	//不同步倒计时取可能从sesiion中取不到值
                            "type": "POST",
                            "data": JSON.stringify({email: email, yt: "register"}),
                            //"dataType":"json",	//返回值是void,如果带这个属性不会调用回调函数
                            "contentType": "application/json;charset=UTF-8",
                            "success": function (data) {
                                sendemail();
                            }
                        });
                    }
                }
            });

        } else {
            if ($("#emailIsNull").length <= 0) {
                $("#getCode").parent().find("label").remove();
                $("#getCode").parent().find("span").remove();
                $("#getCode").after("<span id='emailIsNull' for='code' class='tips'>请先填写邮箱</span>");
            }
            return;
        }
    });


    //用户名异步查询是否重复
    $("input[name=username]").focus(function () {
        $("#usernameIsExist").remove();
    });
    $("input[name=username]").blur(function () {
        var username = $("input[name=username]").val();
        if (username == null || username == "") {
            return;
        }
        $.ajax({
            "url": "http://47.92.69.253/user/usernameIsExist/" + username,
            "type": "GET",
            "dataType": "json",
            "success": function (data) {
                if (data.status != 200) {
                    if ($("#usernameIsExist").length == 0) {
                        $("input[name=username]").parent().find("label").remove();
                        $("input[name=username]").after("<span id='usernameIsExist' class='tips'>" + data.msg + "</span>");
                    }
                }
            }
        });
    });
    //异步校验邮箱是否存在
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
                if (data.status != 200) {
                    if ($("#emailIsExist").length == 0) {
                        $("input[name=email]").parent().find("label").remove();
                        $("input[name=email]").after("<span id='emailIsExist' class='tips'>" + data.msg + "</span>");
                    }
                }
            }
        });
    });
    //vilidate表单校验
    $("#registerForm").validate({
        errorPlacement: function (error, element) {
            if (element.attr("name") == "code") {
                error.insertAfter($("#getCode"));
            } else {
                error.insertAfter(element);
            }
        },
        rules: {
            username: {
                required: true,
                minlength: 3,
                maxlength: 16,
            },
            password: {
                required: true,
                minlength: 6,
                maxlength: 16
            },
            email: {
                required: true,
                email: true
            },
            code: {
                required: true,
            }
        },
        messages: {
            username: {
                required: "请告诉我你的昵称吧",
                minlength: "用户名过短",
                maxlength: "用户名过长",
            },
            password: {
                required: "别忘记填写密码哦",
                minlength: "密码不能小于6位",
                maxlength: "密码不能大于16位"
            },
            email: {
                required: "不填写邮箱发不了验证码哦",
                email: "格式不对啊喂"
            },
            code: {
                required: "你你你验证码还没输呢",
            }
        }
    });
    $("#subForm").click(function () {

        if ($("#registerForm").valid()) {
            if ($("#emailIsExist").length != 0 || $("#usernameIsExist").length != 0) {
                return;
            }
            var flag = false;
            var email = $("input[name=email]").val();
            var code = $("input[name=code]").val();
            $.ajax({
                "async": false,	//同步校验才能成功
                "url": "http://47.92.69.253/user/codeIsTrue",
                "type": "POST",
                "data": JSON.stringify({email: email, code: code, yt: "register"}),
                "contentType": "application/json;charset=UTF-8",
                "dataType": "json",
                "success": function (data) {
                    if (data.status == 200) {
                        flag = true;
                    } else {
                        if ($("#codeIsFalse").length == 0) {
                            $("#getCode").parent().find("label").remove();
                            $("#getCode").parent().find("span").remove();
                            $("#getCode").after("<label id='codeIsFalse' class='error'>" + data.msg + "</label>");
                        }
                    }
                }
            });
            //alert($("#emailIsExist").length==0);
            //alert($("#usernameIsExist").length==0);
            if (flag) {
                $("#registerForm").submit();
            }
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
        "url": "http://47.92.69.253/user/getdjs?yt=register",
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