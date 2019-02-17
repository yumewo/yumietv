$(function () {
    $(".shaixuanAnime").click(function () {
        if ($(this).css("color") == "rgb(255, 255, 255)") {
            return;
        }
        // 	var divId = $(this).parent().attr("id");
        // 	$("#shaixuanForm input[name="+divId+"]").attr("value",$(this).text());
        // 	$("#shaixuanForm").submit();
        // });
        // var sjvar = $("input[name=sj]").val();
        // if (sjvar != null && sjvar != "") {
        // 	$(".select_txt").text(sjvar);
        // }
        // var inputArray = $("#shaixuanForm input");
        // inputArray.each(function (i) {
        // 	if ($(this).val() != null && $(this).val() != "") {
        // 		var selectiveVal = $(this).val();
        // 		var divName = $(this).attr("name");
        // 		var divArray = $("#"+divName).find("div");
        // 		divArray.each(function (i) {
        // 			if ($(this).text() == selectiveVal) {
        // 				$(this).parent().find("div").css("background-color", "");
        // 				$(this).parent().find("div").css("color", "black");
        // 				$(this).css("background-color", "deepskyblue");
        // 				$(this).css("color", "white");
        // 			}
        // 		});
        // 	}
        var clickParentId = $(this).parent().attr("id");
        var clickValue = $(this).text().replace(/\ +/g,"").replace(/[\r\n]/g,"");
        if (clickValue == "全部") {
            var shaixuanLink = "http://47.92.69.253/shaixuan?" + clickParentId + "=&";
        }
        else if (clickValue == "#"){
            var shaixuanLink = "http://47.92.69.253/shaixuan?" + clickParentId + "=%23&";
        }
        else {
            var shaixuanLink = "http://47.92.69.253/shaixuan?" + clickParentId + "=" + clickValue + "&";
        }
        $(".shaixuanAnime").each(function (i) {
            var allPatentId = $(this).parent().attr("id");
            if ($(this).css("color") == "rgb(255, 255, 255)" && allPatentId != clickParentId) {
                var otherValue = $(this).text().replace(/\ +/g,"").replace(/[\r\n]/g,"");
                if (otherValue == "全部") {
                    shaixuanLink = shaixuanLink + allPatentId + "=" + "&";
                }
                else if (otherValue == "#"){
                    shaixuanLink = "http://47.92.69.253/shaixuan?" + allPatentId + "=%23&";
                }
                else {
                    shaixuanLink = shaixuanLink + allPatentId + "=" + otherValue + "&";
                }
            }
        });
        var shaixuanSj = $(".select_txt").text().replace(/\ +/g,"").replace(/[\r\n]/g,"");
        shaixuanLink = shaixuanSj == "全部" ? shaixuanLink + "sj=" : shaixuanLink + "sj=" + shaixuanSj;
        location.href = shaixuanLink;
    });


    $(".select_box").click(function (event) {
        event.stopPropagation();
        var option = $(this).find(".option");
        if (option.css("display") == "none") {
            $(".selet_open").css("transform", "rotateX(180deg)");
            option.show();
        } else {
            option.hide();
            $(".selet_open").css("transform", "rotateX(0deg)");
        }
    });
    //列表处于展开状态,点击页面其他位置也关闭列表
    $(document).click(function (event) {
        var eo = $(event.target);
        if ($(".select_box").is(":visible") && eo.attr("class") != "option" && !eo.parent(".option").length)
            $('.option').hide();
    });
    /*赋值给文本框*/
    $(".option a").click(function () {
        var value = $(this).text().replace(/\ +/g,"").replace(/[\r\n]/g,"");
        $(this).parent().siblings(".select_txt").text(value);
        var shaixuanLink = "http://47.92.69.253/shaixuan?";
        $(".shaixuanAnime").each(function (i) {
            var allPatentId = $(this).parent().attr("id");
            if ($(this).css("color") == "rgb(255, 255, 255)") {
                var sjValue = $(this).text().replace(/\ +/g,"").replace(/[\r\n]/g,"");
                if (sjValue == "全部") {
                    shaixuanLink = shaixuanLink + allPatentId + "=" + "&";
                }
                else if (sjValue == "#"){
                    shaixuanLink = shaixuanLink + allPatentId + "=%23&";
                }
                else {
                    shaixuanLink = shaixuanLink + allPatentId + "=" + sjValue + "&";
                }
            }
        });
        shaixuanLink = value == "全部" ? shaixuanLink + "sj=" : shaixuanLink + "sj=" + value;
        location.href = shaixuanLink;
    });

    $(".fenye").click(function () {
        var fenyeLink = "http://47.92.69.253/shaixuan?";
        $(".shaixuanAnime").each(function (i) {
            var allPatentId = $(this).parent().attr("id");
            if ($(this).css("color") == "rgb(255, 255, 255)") {
                var shaixuanValue = $(this).text().replace(/\ +/g,"").replace(/[\r\n]/g,"");
                if (shaixuanValue == "全部") {
                    fenyeLink = fenyeLink + allPatentId + "=" + "&";
                }
                else if (shaixuanValue == "#"){
                    fenyeLink = fenyeLink + allPatentId + "=%23&";
                }
                else {
                    fenyeLink = fenyeLink + allPatentId + "=" + shaixuanValue + "&";
                }
            }
        });
        var shaixuanSj = $(".select_txt").text().replace(/\ +/g,"").replace(/[\r\n]/g,"");;
        fenyeLink = shaixuanSj == "全部" ? fenyeLink + "sj=" : fenyeLink + "sj=" + shaixuanSj;
        var wherePage = $(this).find("a").text();
        if ($(this).attr("name") == "shangyiye") {
            wherePage = parseInt($(".active").find("a").text()) - 1;
        }
        if ($(this).attr("name") == "xiayiye") {
            wherePage = parseInt($(".active").find("a").text()) + 1;
        }
        if ($(this).attr("name") == "shouye") {
            wherePage = "1";
        }
        if ($(this).attr("name") == "weiye") {
            wherePage = $(this).val();
        }
        fenyeLink = fenyeLink + "&currentPage=" + wherePage;
        location.href = fenyeLink;
    });
});