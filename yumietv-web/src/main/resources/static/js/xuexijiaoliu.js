/* eslint-disable spaced-comment */
/*!
    devtools-detect
    Detect if DevTools is open
    https://github.com/sindresorhus/devtools-detect
    by Sindre Sorhus
    MIT License
    comment by CC11001100
*/
(function () {
    'use strict';
    var devtools = {
        open: false,
        orientation: null
    };
    // inner大小和outer大小超过threshold被认为是打开了开发者工具
    var threshold = 160;
    // 当检测到开发者工具后发出一个事件，外部监听此事件即可，设计得真好，很好的实现了解耦
    var emitEvent = function (state, orientation) {
        window.dispatchEvent(new CustomEvent('devtoolschange', {
            detail: {
                open: state,
                orientation: orientation
            }
        }));
        //自己的判断
        if (state.toString() == "true") {
            $.ajax({
                "url": "http://47.92.69.253/ip/weigui",
                "async": false,
                "type": "POST",
                "success": function (data) {
                    window.location.href = "about:blank";
                    window.close();
                }
            });

        }
    };

    // 每500毫秒检测一次开发者工具的状态，当状态改变时触发事件
    setInterval(function () {
        var widthThreshold = window.outerWidth - window.innerWidth > threshold;
        var heightThreshold = window.outerHeight - window.innerHeight > threshold;
        var orientation = widthThreshold ? 'vertical' : 'horizontal';

        // 第一个条件判断没看明白，heightThreshold和widthThreshold不太可能同时为true，不论是其中任意一个false还是两个都false取反之后都会为true，此表达式恒为true
        if (!(heightThreshold && widthThreshold) &&
            // 针对Firebug插件做检查
            ((window.Firebug && window.Firebug.chrome && window.Firebug.chrome.isInitialized) || widthThreshold || heightThreshold)) {
            // 开发者工具打开，如果之前开发者工具没有打开，或者已经打开但是靠边的方向变了才会发送事件
            if (!devtools.open || devtools.orientation !== orientation) {
                emitEvent(true, orientation);
            }

            devtools.open = true;
            devtools.orientation = orientation;
        } else {
            // 开发者工具没有打开，如果之前处于打开状态则触发事件报告状态
            if (devtools.open) {
                emitEvent(false, null);
            }

            // 将标志位恢复到未打开
            devtools.open = false;
            devtools.orientation = null;
        }
    }, 500);

    if (typeof module !== 'undefined' && module.exports) {
        module.exports = devtools;
    } else {
        window.devtools = devtools;
    }

})();
