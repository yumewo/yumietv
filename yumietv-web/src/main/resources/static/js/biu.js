var fgm = {
    on: function (element, type, handler) {
        return element.addEventListener ? element.addEventListener(type, handler, false) : element.attachEvent("on" + type, handler)
    },
    un: function (element, type, handler) {
        return element.removeEventListener ? element.removeEventListener(type, handler, false) : element.detachEvent("on" + type, handler)
    },
    bind: function (object, handler) {
        return function () {
            return handler.apply(object, arguments)
        }
    },
    randomRange: function (lower, upper) { //产生范围在lower~upper的随机数
        return Math.floor(Math.random() * (upper - lower + 1) + lower)
    },
    getRanColor: function () { //随机获得十六进制颜色
        var str = this.randomRange(0, 0xFFFFFF).toString(16);
        while (str.length < 6) str = "0" + str;
        return "#" + str
    }
};

//初始化对象
function FireWorks() {
    this.type = 0;
    this.timer = null;
    this.fnManual = fgm.bind(this, this.manual)
}

FireWorks.prototype = {
    initialize: function () {
        clearTimeout(this.timer);
        fgm.un(document, "click", this.fnManual);
        switch (this.type) {
            case 1:
                fgm.on(document, "click", this.fnManual);
                break;
        }
        ;
    },
    manual: function (event) {
        event = event || window.event;
        this.__create__({
            x: event.clientX,
            y: event.clientY
        });
    },
    __create__: function (param) {
        //param即鼠标点击点（即烟花爆炸点）
        var that = this;
        var oChip = null;
        var aChip = [];
        var timer = null;
        var oFrag = document.createDocumentFragment();
        (function () {
            //在50-100之间随机生成碎片
            //由于IE浏览器处理效率低, 随机范围缩小至20-30
            //自动放烟花时, 随机范围缩小至20-30
            //var len = (/msie/i.test(navigator.userAgent) || that.type == 2) ? fgm.randomRange(20, 30) : fgm.randomRange(50, 100);
            var len = 10;
            //产生所有烟花爆炸颗粒实体
            for (i = 0; i < len; i++) {
                //烟花颗粒形态实体
                oChip = document.createElement("div");
                with (oChip.style) {
                    position = "absolute";
                    top = param.y + document.body.scrollTop + "px";
                    left = param.x + "px";
                    width = "4px";
                    height = "4px";
                    overflow = "hidden";
                    borderRadius = "4px";
                    background = fgm.getRanColor();
                }
                ;
                oChip.speedX = fgm.randomRange(-10, 10);
                oChip.speedY = fgm.randomRange(-10, 10);
                oFrag.appendChild(oChip);
                aChip[i] = oChip
            }
            ;
            document.body.appendChild(oFrag);
            timer = setInterval(function () {
                for (i = 0; i < aChip.length; i++) {
                    var obj = aChip[i];
                    with (obj.style) {
                        // top = obj.y + obj.speedY + "px";
                        // console.log(document.body.scrollTop);
                        // console.log(document.body.scrollHeight+"ww");
                        // console.log(obj.offsetTop);
                        top = obj.offsetTop + obj.speedY + "px";
                        left = obj.offsetLeft + obj.speedX + "px";
                    }
                    ;
                    obj.speedY++;
                    //判断烟花爆炸颗粒是否掉落至窗体之外，为真则remove
                    //splice() 方法可删除从 index 处开始的零个或多个元素
                    //(obj.offsetTop < 0 || obj.offsetLeft < 0 || obj.offsetTop > document.documentElement.clientHeight + document.body.scrollTop || obj.offsetLeft > document.documentElement.clientWidth) && (document.body.removeChild(obj), aChip.splice(i, 1))
                    (obj.offsetTop > param.y + 100 || obj.offsetLeft < 0 || obj.offsetLeft > document.documentElement.clientWidth) && (document.body.removeChild(obj), aChip.splice(i, 1))
                }
                ;
                //判断烟花爆炸颗粒是否全部remove，为真则clearInterval(timer);
                !aChip[0] && clearInterval(timer);
            }, 30)
        })();
    }
};

fgm.on(window, "load", function () {
    var oFireWorks = new FireWorks();
    oFireWorks.type = 1;
    oFireWorks.initialize();
});

//			fgm.on(document, "contextmenu", function(event) {
//				var oEvent = event || window.event;
//				oEvent.preventDefault ? oEvent.preventDefault() : oEvent.returnValue = false
//			});