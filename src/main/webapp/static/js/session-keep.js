var eu = eu || {};

eu.contextPath = ctxAdmin==undefined ?"":ctxAdmin;
/**
 * 保持心跳
 */
window.setInterval(sessionInfo, 15 * 60 * 1000);
function sessionInfo() {
    $.ajax({
        type: "GET",
        url: eu.contextPath+"/login/sessionInfo",
        cache: false,
        dataType: "json",
        success: function (data) {

        }});
}