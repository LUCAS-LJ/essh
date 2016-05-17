<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    var categorys_combotree;
    $(function(){
        uploadify();
        $(".uploadify").css({'display': 'inline-block', 'height': '24px', 'padding-right': '18px', 'outline': 'none'});

    });
    function loadcategoryType(){
        categorys_combotree = $("#categoryIds").combotree({
            url:'${ctxAdmin}/repository/categoryR/categoryComTree',
            multiple:true,//是否可多选
            editable:false,
            cascadeCheck:false
        });

    }
    /**
     * 文件上传
     */
    function uploadify() {
        $('#file').uploadify({
            method: 'post',
            swf: '${ctxStatic}/js/uploadify/scripts/uploadify.swf',  //FLash文件路径
            buttonText: '上传文件',                                 //按钮文本
            uploader: '${ctxAdmin}/disk/fileUpload?folderId=${folderId}&jsessionid=<%=session.getId()%>',
            fileObjName: 'uploadFile',
            queueSizeLimit: 100,
            uploadLimit: 100,
            removeCompleted: false,
            multi: true,      //是否为多选，默认为true
            fileSizeLimit: '1GB', //单个文件大小，0为无限制，可接受KB,MB,GB等单位的字符串值
            fileTypeDesc: '全部文件',
            fileTypeExts: '*.*',  //上传的文件后缀过滤器
            overrideEvents:["onUploadStart",'onUploadSuccess'],
            onUploadStart : function(file) {
                // Load the swfupload settings
                var settings = this.settings;

                var timer        = new Date();
                this.timer       = timer.getTime();
                this.bytesLoaded = 0;
                if (this.queueData.uploadQueue.length == 0) {
                    this.queueData.uploadSize = file.size;
                }
                var uploadify = this;
                $.ajax({
                    type    : 'POST',
                    async   : false,
                    dataType: "json",
                    url     : "${ctxAdmin}/disk/fileLimitCheck/"+${folderId},
                    data    : {uploadFileSize: file.size,filename: file.name},
                    success : function(data) {
                        if (data.code == 0) {
                            $("#tip_msg").append(data.msg).append("<br>");
                            uploadify.cancelUpload(file.id);
                            $('#' + file.id).remove();
                            if (uploadify.queueData.uploadQueue.length > 0 && uploadify.queueData.queueLength > 0) {
                                if (uploadify.queueData.uploadQueue[0] == '*') {
                                    uploadify.startUpload();
                                } else {
                                    uploadify.startUpload(uploadify.queueData.uploadQueue.shift());
                                }
                            }

                        }
                    }
                });
            },
            //上传到服务器，服务器返回相应信息到data里
            onUploadSuccess: function (file, data, response) {
                // Load the swfupload settings
                var settings = this.settings;
                var stats    = this.getStats();
                this.queueData.uploadsSuccessful = stats.successful_uploads;
                this.queueData.queueBytesUploaded += file.size;
                var data = eval("(" + data + ")");
                $('#' + file.id).find('.data').html(' - ' +data.msg);
            }
        });
    }
</script>
<div>
    <form id="file_form" method="post"  class="dialog-form" novalidate>
        <div id="tip_msg">
        </div>
        <input type="hidden"  name="id" />
        <!-- 版本控制字段 version -->
        <input type="hidden" id="version" name="version" />
        <div>
            <label >上传到文件夹:</label>
            ${folderName}
        </div>
        <div>
            <%--提示小图标--%>
            <span class="tree-icon tree-file easyui-icon-tip easyui-tooltip"
                  title="单个文件上传限制1GB." ></span>
            <input id="file" name="file" type="file" multiple="true">

            <div id="queue"></div>
            <input id="filePath" name="filePath" type="hidden" value="${filePath}"/>
        </div>

    </form>
</div>