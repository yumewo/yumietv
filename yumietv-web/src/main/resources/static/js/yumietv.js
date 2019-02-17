$(function () {
    $(".hello").fadeOut(0);
    $(".hello").fadeIn(1000);

    setInterval(function () {
        check()
    }, 4000);
    var check = function () {
        function doCheck(a) {
            if (("" + a / a)["length"] !== 1 || a % 20 === 0) {
                (function () {
                }
                    ["constructor"]("debugger")())
            } else {
                (function () {
                }
                    ["constructor"]("debugger")())
            }
            doCheck(++a)
        }

        try {
            doCheck(0)
        } catch (err) {
        }
    };
    check();

    var event = arguments.callee.caller.arguments[0] || window.event;// 消除浏览器差异
    $("input[name=searchtj]").keydown(function (event) {
        if (event.keyCode == 13) {
            search();
        }
    });

    $("#searchBtn").click(function () {
        search();
    });
    $("#img").mouseenter(function () {
        $("#img").css("opacity", "1");
    });
    $("#img").mouseleave(function () {
        $("#img").css("opacity", "0.6");
    });
    if ($("#jujiUl li").length > 24) {
        $("#jujiUl").css("height", "180px");
        $("#jujiUl").css("width", "1250px");
        $("#jujiUl").css("overflow", "auto");
    }
    $("#jujiUl li").mouseenter(function () {
        $(this).css("background-color", "deepskyblue");
        $(this).css("border", "1px solid deepskyblue")
    });
    $("#jujiUl li").mouseleave(function () {
        $(this).css("background-color", "");
        $(this).css("border", "1px solid lightgray");
    });
    $(".img-rounded").mouseenter(function () {
        $(this).find("div").find(".rmHistoryDiv").html("<span class=\"rmHistory\" style=\"color:hotpink; font-size: 21px;font-weight: 700;line-height: 1;\">&times;</span>");
    });
    $(".img-rounded").mouseleave(function () {
        $(this).find("div").find("div").find(".rmHistory").remove();
    });
    $(".rmHistoryDiv").click(function () {
        var title = $(this).next().find("div").find("span").text();
        var nyr = $(this).parent().parent().parent().prev().text();
        location.href = "http://47.92.69.253/playHistory/rm?title=" + title + "&nyr=" + nyr;
    });
    $("#rmHistoryAll").click(function () {
        var msg = "您确定要删除所有记录吗？";
        if (confirm(msg) == true) {
            location.href = "http://47.92.69.253/playHistory/rmAll";
            return true;
        } else {
            return false;
        }
    });

    $("body").click(function () {
        qiehuan();
    });
});

function toIndex(index) {
    location.href = index;
}

function search() {
    var tiaojian = $("input[name=searchtj]").val().replace(/\s+/g, "");
    if (tiaojian == null || tiaojian == "") {
        return;
    }
    location.href = "http://47.92.69.253/search?str=" + tiaojian;
}

function qiehuan() {
    var bfb = $("body").css("background-position-x");
    var num = Math.floor(Math.random() * (1 - 100) + 100);
    if (num != parseInt(bfb.replace("%", ""))) {
        $("body").css("background-position-x", num + "%");
    } else {
        qiehuan();
    }
}
