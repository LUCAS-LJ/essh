<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<div>
    <form id="versionLog_form" class="dialog-form" method="post">
        <input type="hidden" id="id" name="id" />
        <div>
            <label>版本号:</label>
            <input type="text" name="versionName"
                   maxLength="36" class="easyui-validatebox textbox" placeholder="版本号"
                   data-options="required:true,missingMessage:'请输入版本号....',validType:['minLength[1]']" />
        </div>
        <div>
            <label style="vertical-align: top;">更新说明:</label>
            <input name="remark" class="easyui-textbox" data-options="multiline:true" placeholder="更新说明" style="width:420px;height:200px;">
        </div>
    </form>
</div>