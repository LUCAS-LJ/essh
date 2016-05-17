<%@ page import="com.eryansky.common.web.utils.WebUtils" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>用户登录</title>
    <%@ include file="/common/meta.jsp" %>
    <%@ include file="/common/autocomplete.jsp" %>
    <style type="text/css">
        .login_label {display: inline-block;text-align: right;width: 76px;font-size: 14px;}
        body{width:100%; height:100%; margin:0; padding:0;}
    </style>
    <%
        Cookie cookie = WebUtils.getCookie(request, "loginName");
        if(cookie!=null){
            String loginNameOrName = WebUtils.getCookie(request, "loginName").getValue();
            request.setAttribute("loginNameOrName",loginNameOrName);
        }
    %>
    <script type="text/javascript">
        var loginForm;
        var login_linkbutton;
        var $loginName, $password, $rememberPassword, $autoLogin;
        $(function () {
            $loginName = $("#loginName");
            $password = $("#password");
            $rememberPassword = $("#rememberPassword");
            $autoLogin = $("#autoLogin");
            loginForm = $('#loginForm').form();
            //refreshCheckCode();

            $rememberPassword.prop("checked", "${cookie.rememberPassword.value}" == "" ? false : true);
            $autoLogin.prop("checked", "${cookie.autoLogin.value}" == "" ? false : true);

            if ("${cookie.autoLogin.value}" != "") {
                login();
            } else {
                $loginName.focus();
            }

            $rememberPassword.click(function () {
                var checked = $(this).prop('checked');
                if (checked) {
                    $.cookie('password', $password.val(), {
                        expires: 7
                    });
                    $.cookie('rememberPassword', checked, {
                        expires: 7
                    });
                } else {
                    $.cookie('password', "", {
                        expires: 7
                    });
                    $.cookie('rememberPassword', "", {
                        expires: 7
                    });
                }
            });
            $autoLogin.click(function () {
                var checked = $(this).prop('checked');
                if (checked) {
                    $.cookie('autoLogin', checked, {
                        expires: 7
                    });
                    $rememberPassword.prop('checked', checked);
                    $.cookie('rememberPassword', checked, {
                        expires: 7
                    });
                } else {
                    $.cookie('autoLogin', "", {
                        expires: 7
                    });
                }
            });

            $loginNameAutocompleter = $("#loginName").next().children(".textbox-text").autocomplete('${ctxAdmin}/sys/user/autoComplete', {
                remoteDataType:'json',
                minChars: 0,
                maxItemsToShow: 10
            });
            var ac = $loginNameAutocompleter.data('autocompleter');
            //添加查询属性
            ac.setExtraParam("rows",ac.options.maxItemsToShow);
        });
        function cover(){
            var win_width=$(window).width();
            var win_height=$(window).height();
            $("#bigpic").attr({width:win_width,height:win_height});
        }
        //刷新验证码
        function refreshCheckCode() {
            //加上随机时间 防止IE浏览器不请求数据
            var url = '${ctx}/servlet/ValidateCodeServlet?' + new Date().getTime();
            $('#validateCode_img').attr('src', url);
        }
        // 登录
        function login() {
            $.cookie('loginName', $loginName.val(), {
                expires: 7
            });
            if ($rememberPassword.prop("checked")) {
                $.cookie('password', $password.val(), {
                    expires: 7
                });
            }
            if (loginForm.form('validate')) {
                login_linkbutton = $('#login_linkbutton').linkbutton({
                    text: '正在登录...',
                    disabled: true
                });
                var cookieThemeType = "${cookie.themeType.value}"; //cookie初访的登录管理界面类型
                $.post('${ctxAdmin}/login/login?theme=' + cookieThemeType, $.serializeObject(loginForm), function (data) {
                    if (data.code == 1) {
                        window.location = data.obj;//操作结果提示
                    } else {
                        login_linkbutton.linkbutton({
                            text: '登录',
                            disabled: false
                        });
                        $('#validateCode').val('');
                        eu.showMsg(data.msg);
                        //refreshCheckCode();
                    }
                }, 'json');
            }
        }
    </script>
</head>
<body>
    <div id="login_div" align="center" style="margin-top: 100px;">
        <div class="easyui-panel" title="用户登录" align="center" style="width:400px;padding:20px 30px 20px 30px;)">
            <form id="loginForm" method="post" novalidate>
                <div style="padding: 5px;">
                    <label class="login_label">用户名：</label>
                    <input id="loginName" name="loginName" class="easyui-textbox" style="width: 210px;height:40px;padding:12px"
                           value="${fns:urlDecode(loginNameOrName)}"
                           data-options="prompt:'请输入登录名...',iconCls:'easyui-icon-man',iconWidth:38,required:true,validType:'minLength[1]',missingMessage:'请输入用户名!'"/>

                </div>
                <div style="padding: 5px;">
                    <label class="login_label">密&nbsp;&nbsp;码 ：</label>
                    <input id="password" name="password" class="easyui-textbox" type="password"
                           value="${cookie.password.value}"
                           onkeydown="javascript:if(event.keyCode==13){login();}" style="width:210px;height:40px;padding:12px"
                           data-options="prompt:'请输入密码...',iconCls:'easyui-icon-lock',iconWidth:38,required:true,validType:'minLength[1]',missingMessage:'请输入密码!'"/>

                </div>
                <%--<div style="padding: 5px;">--%>
                    <%--<label class="login_label"> 验证码：</label>--%>
                    <%--<input id="validateCode" name="validateCode" type="text" onkeydown="if(event.keyCode==13)login()"--%>
                           <%--class="easyui-validatebox textbox eu-input" style="width: 100px" required="true"--%>
                           <%--data-options="tipPosition:'left',validType:'alphanum',missingMessage:'请输入验证码!'"/>--%>
                    <%--<img id="validateCode_img" align="middle" onclick="refreshCheckCode();"/>--%>
                    <%--<a href="javascript:void(0)" onclick="refreshCheckCode();"> 看不清,换一个</a>--%>

                <%--</div>--%>
                <div style="padding: 5px;">

                    <label for="rememberPassword" style="margin-left: 76px;"><input id="rememberPassword" type="checkbox"/>记住密码</label>
                    <label for="autoLogin"><input id="autoLogin" type="checkbox"/>自动登录</label>
                </div>
                <div>
                    <a id="login_linkbutton" href="#" class="easyui-linkbutton" onclick="login()"
                       style="margin-left: 76px;width: 210px;height: 42px;">登 录</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
