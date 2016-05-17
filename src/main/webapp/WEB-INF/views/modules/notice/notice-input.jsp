<%@ page import="com.eryansky.modules.notice.utils.NoticeUtils" %>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<script type="text/javascript">
var $form_noticeOrgan_combotree = undefined;
$(function () {
    loadNoticeUser();
    loadNoticeOrgan();


    var $isToAll =  $("input[name=isToAll]:eq(${model.isToAll})");
    $isToAll.attr("checked",'checked');
    toggoleIsToAllUser($isToAll.val());
    $("input[name=isToAll]").bind("click",function(){
        toggoleIsToAllUser($(this).val());
    });

    var $isTop =  $("input[name=isTop]:eq(${model.isTop})");
    $isTop.attr("checked",'checked');
    toggoleIsTop($isTop.val());
    $("input[name=isTop]").bind("click",function(){
        toggoleIsTop($(this).val());
    });

    uploadify();

    KindEditor.create('#content_kindeditor', {
        width: '96%',
        height: '360px',
        minWidth: '650px',
        items: [ 'source', '|', 'undo', 'redo', '|', 'preview', 'print', 'template', 'code', 'cut', 'copy', 'paste', 'plainpaste', 'wordpaste', '|', 'justifyleft', 'justifycenter', 'justifyright', 'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript', 'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 'fullscreen', '/', 'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'flash', 'media', 'table', 'hr', 'emoticons', 'baidumap', 'pagebreak', 'anchor', 'link', 'unlink' ],
        allowFileManager: true,
        uploadJson : '${ctxAdmin}/disk/filekindeditor/upload',
        fileManagerJson : '${ctxAdmin}/disk/filekindeditor/filemanager',
        afterCreate: function () { //加载完成后改变皮肤
            var color = $('.panel-header').css('background-color');
            $('.ke-toolbar').css('background-color', color);
        },
        afterChange : function() {
            this.sync();
        }
    });
});

function toggoleIsToAllUser(isToAll){
    if("1" == isToAll){
        $("#noticeUserIds_div").hide();
        $form_noticeUser_MultiSelect.value(" ");
        $("#noticeOrganIds_div").hide();
        $form_noticeOrgan_combotree.combotree("setValue","");
    }else{
        $("#noticeUserIds_div").show();
        $("#noticeOrganIds_div").show();
    }
}

function toggoleIsTop(isTop){
    if("0" == isTop){//不置顶
        $("#endTopDay_span").hide();
        $("#endTopDay").val('');
    }else{
        $("#endTopDay_span").show();
        $("#endTopDay").val("7");
    }
}

function loadNoticeOrgan() {
    $form_noticeOrgan_combotree = $("#_noticeOrganIds").combotree({
        url: '${ctxAdmin}/sys/organ/tree?grade=0',
        multiple: true,//是否可多选
        editable: false,
        value:${model.noticeOrganIds}
    });
}

var fileIdArray = ${model.fileIds};
var _fileIds = fileIdArray.join(",");
$("#_fileIds").val(_fileIds);
function uploadify() {
    $('#uploadify').uploadify({
        method: 'post',
        swf: '${ctxStatic}/js/uploadify/scripts/uploadify.swf',
        buttonText: '浏  览',
        uploader: '${ctxAdmin}/notice/notice/upload;jsessionid=<%=session.getId()%>',
        fileObjName: 'uploadFile',
        removeCompleted: false,
        multi: true,
        fileSizeLimit: '100MB', //单个文件大小，0为无限制，可接受KB,MB,GB等单位的字符串值
        fileTypeDesc: '全部文件',
        fileTypeExts: '*.*',
        onUploadSuccess: function (file, data, response) {
            data = eval("(" + data + ")");
            if(data.code != undefined && data.code == 1){
                fileIdArray.push(data.obj);
            }
            $('#' + file.id).find('.data').html(data.msg);
            var _fileIds = fileIdArray.join(",");
            $("#_fileIds").val(_fileIds);
            var uploadify = this;
            var cancel = $('#file-queue .uploadify-queue-item[id="' + file.id + '"]').find(".cancel a");
            if (cancel) {
                cancel.attr("rel", data.obj);
                cancel.click(function() {
                    delUpload( data.obj,file.id,uploadify);
                });
            }
        }

    });
}
function loadOrOpen(fileId) {
    $('#annexFrame').attr('src', '${ctxAdmin}/disk/fileDownload/' + fileId);
}

/**
 * 删除附件 页面删除
 * @param fileId 后台File ID
 * @param pageFileId uploadify页面ID'
 * @param uploadify
 */
function delUpload(fileId,pageFileId,uploadify) {
    fileIdArray.splice($.inArray(fileId,fileIdArray),1);
    var _fileIds = fileIdArray.join(",");
    $("#_fileIds").val(_fileIds);
    $('#' + fileId).remove();
    if(pageFileId){
        $('#' + pageFileId).empty();
        delete uploadify.queueData.files[pageFileId]; //删除上传组件中的附件队列
        $('#' + pageFileId).remove();
    }

}

/**
 *
 */
function loadNoticeUser() {
    $.ajax({
        type: "post",
        dataType: 'json',
        contentType: "application/json",
        url: "${ctxAdmin}/sys/user/combogridAll",
        async: false,
        success: function (data) {
            $form_noticeUser_MultiSelect = $("#noticeUserIds").kendoMultiSelect({
                dataTextField: "name",
                dataValueField: "id",
                dataSource: data.rows,
                value: ${model.noticeUserIds},
                dataBound: function (e) {

                }

            }).data("kendoMultiSelect");
        }
    });

}
function _selectUser() {
    var userIds = "";
    var dataItems = $form_noticeUser_MultiSelect.dataItems();
    if (dataItems && dataItems.length > 0) {
        var num = dataItems.length;
        $.each(dataItems, function (n, value) {
            if (n == num - 1) {
                userIds += value.id;
            } else {
                userIds += value.id + ",";
            }

        });

    }
    var input_selectUser_dialog = $("<div/>").dialog({
        title: "选择用户",
        top: 10,
        href: '${ctxAdmin}/sys/user/select?userIds=' + userIds+"&grade=0",
        width: '700',
        height: '450',
        maximizable: true,
        iconCls: 'easyui-icon-edit',
        modal: true,
        buttons: [
            {
                text: '确定',
                iconCls: 'easyui-icon-save',
                handler: function () {
                    _setSelectUser();
                    input_selectUser_dialog.dialog('destroy');
                }
            },
            {
                text: '关闭',
                iconCls: 'easyui-icon-cancel',
                handler: function () {
                    input_selectUser_dialog.dialog('destroy');

                }
            }
        ],
        onClose: function () {
            input_selectUser_dialog.dialog('destroy');
        }
    });
}

function _setSelectUser() {
    var selectUserIds = new Array();
    $("#selectUser option").each(function () {
        var txt = $(this).val();
        selectUserIds.push($.trim(txt));
    });
    $form_noticeUser_MultiSelect.value(selectUserIds);
}
</script>
<div>
    <form id="notice_form" class="dialog-form"  method="post" novalidate>
        <input type="hidden" name="id" value="${model.id}"/>
        <!-- 用户版本控制字段 version -->
        <input type="hidden" id="version" name="version" value="${model.version}"/>

        <div>
            <label>通知标题：</label>
            <input name="title" type="text" class="easyui-validatebox textbox" style="width: 360px;" value="${model.title}"
                   maxLength="128" data-options="required:true,missingMessage:'请输入标题.',validType:['minLength[1]']">
            <e:dictionary id="notice_type" name="type" code="<%=NoticeUtils.DIC_NOTICE%>" type="combobox" value="${model.type}" editable="false" selectType="select" height="28"></e:dictionary>
        </div>
        <div>
            <label>接收设置：</label>
            <label style="text-align: left;width: 120px;">
                <input type="radio" name="isToAll" style="width: 20px;" value="0" /> 指定接收人
            </label>
            <label style="text-align: left;width: 120px;">
                <input type="radio" name="isToAll" style="width: 20px;" value="1" /> 所有人接收
            </label>
        </div>
        <div id="noticeUserIds_div">
            <label style="display:block;float:left;">接收人员：</label>
            <select id="noticeUserIds" name="_noticeUserIds" multiple="true"
                    style="width:70%; float:left;margin-left:1px;margin-right:2px;"> </select>
            <a href="#" class="easyui-linkbutton" data-options="iconCls:'eu-icon-user'" style="width: 100px;"
               onclick="_selectUser();">选择</a>
        </div>
        <div  id="noticeOrganIds_div" style="clear:both">
            <label>接收部门：</label>
            <input type="select" id="_noticeOrganIds" name="_noticeOrganIds" style="width: 260px;height: 28px;"
                   data-options="missingMessage:'请选择部门.'"/>
        </div>

        <table style="border: 0px;width: 100%;">
            <tr>
                <td style="width: 96px; vertical-align: top;">通知内容：</td>
                <td><textarea id="content_kindeditor" name="content">${model.content}</textarea></td>
            </tr>
        </table>
        <div>
            <label>有效期：</label>
            <input id="_effectTime" name="effectTime" placeholder="开始时间" type="text"
                   class="easyui-my97" value="${effectTime}" data-options="dateFmt:'yyyy-MM-dd HH:mm',required:true,missingMessage:'请设置开始时间.'">&nbsp;
            ~&nbsp;<input id="_endTime" name="endTime" placeholder="截止时间" type="text" class="easyui-my97"
                          data-options="dateFmt:'yyyy-MM-dd HH:mm'"value= "${model.endTime}"/>
            <span class="tree-icon tree-file easyui-icon-tip easyui-tooltip"
                  title="截止时间不设置，则一直有效。" ></span>
        </div>
        <div>
            <label>置顶：</label>
            <label style="text-align: left;width: 80px;">
                <input type="radio" name="isTop" style="width: 20px;" value="0" /> 不置顶
            </label>
            <label style="text-align: left;width: 80px;">
                <input type="radio" name="isTop" style="width: 20px;" value="1" /> 置顶
            </label>
            <span id="endTopDay_span">
                <input id="endTopDay" name="endTopDay" value="${model.endTopDay}" style="width:80px;height: 28px;"
                       class="easyui-numberspinner" data-options="min:0,max:999" >
                &nbsp;天
                <span class="tree-icon tree-file easyui-icon-tip easyui-tooltip"
                      title="设置为0，则永久置顶。" ></span>
            </span>
        </div>
        <table style="border: 0px;width: 100%;">
            <tr>
                <td style="display: inline-block; width: 96px; vertical-align: top;">附件：</td>
                <td><input id="uploadify" name="file" type="file" multiple="true">

                    <div id="queue"></div>
                    <input id="_fileIds" name="_fileIds" type="hidden" />
                    <c:if test="${not empty files}">
                        <div>
                            <c:forEach items="${files}" begin="0" var="file" varStatus="i">
                                <div id='${file.id}' style="font-size: 14px;">${i.index +1}、<a href="#" onclick="loadOrOpen(${file.id});" style="color: #0000ff;">${file.name}</a>&nbsp;&nbsp;
                                    <a href="#" onclick="delUpload(${file.id});" style="color: red;">删除</a></div>
                            </c:forEach>
                        </div>
                    </c:if>
                </td>
            </tr>
        </table>

    </form>
    <iframe id="annexFrame" style="display:none" src=""></iframe>
</div>
