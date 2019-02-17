$(function () {
    $(".jieguo div a").each(function () {
        if ($(this).find("span").length > 0) {
            $(this).attr("href", "http://47.92.69.253/search?str=" + $(this).text());
        }
    });
    $(".fenye").click(function () {
        var fenyeLink = "http://47.92.69.253/search?str=" + $("#gjz").text();
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