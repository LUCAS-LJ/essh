<%@ page import="com.eryansky.modules.sys.utils.DictionaryUtils" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<%-- 引入kindEditor插件 --%>
<link rel="stylesheet" href="${ctxStatic}/js/kindeditor-4.1.10/themes/default/default.css">
<script type="text/javascript" src="${ctxStatic}/js/kindeditor-4.1.10/kindeditor-all-min.js" charset="utf-8"></script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/bug.js?_=${sysInitTime}" charset="utf-8"></script>
<%-- 隐藏iframe --%>
<iframe id="bug_temp_iframe" style="display: none;"></iframe>
<%-- 列表右键 --%>
<div id="bug_datagrid_menu" class="easyui-menu" style="width:120px;display: none;">
    <div onclick="showDialog();" iconCls="easyui-icon-add">新增</div>
    <div onclick="edit();" data-options="iconCls:'easyui-icon-edit'">编辑</div>
    <div onclick="del();" data-options="iconCls:'easyui-icon-remove'">删除</div>
    <div onclick="exportExcel();" data-options="iconCls:'easyui-icon-edit'">Excel导出</div>
    <div onclick="importExcel();" data-options="iconCls:'easyui-icon-edit'">Excel导入</div>
</div>
<div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">
    <div data-options="region:'north',title:'过滤条件',collapsed:false,split:false,border:false"
         style="padding: 0px; height: 70px;width:100%; overflow-y: hidden;">
        <form id="bug_search_form" style="padding: 5px;">
            &nbsp;类型:<e:dictionary id="type" code="<%=DictionaryUtils.DIC_BUG%>" type="combobox" name="type" selectType="all" height="28"></e:dictionary>
            &nbsp;标题:<input type="text" name="filter_LIKES_title" maxLength="25"
                      class="easyui-validatebox textbox eu-input" placeholder="请输入标题..."
                      onkeydown="if(event.keyCode==13)search()" style="width: 160px" />
            &nbsp;<a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-search',width:100,height:28,onClick:search">查询</a>
            <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-no',width:100,height:28" onclick="javascript:$bug_search_form.form('reset');">重置查询</a>
        </form>
    </div>
    <%-- 中间部分 列表 --%>
    <div data-options="region:'center',split:false,border:false"
         style="padding: 0px; height: 100%;width:100%; overflow-y: hidden;">
        <table id="bug_datagrid"></table>
    </div>
</div>