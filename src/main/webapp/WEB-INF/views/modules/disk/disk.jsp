<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/uploadify.jsp"%>
<script type="text/javascript">
var $folder_tree;
var contextMenuNode;
var $folder_form;
var $folder_dialog;
var $folder_file_search_form;
var $folder_file_datagrid;
var operate_all;
var folder_file_toolbar = [{
    text:'上传文件',
    iconCls:'easyui-icon-add',
    handler:function(){addFolderFile()}
},'-',{
    text:'删除文件',
    iconCls:'easyui-icon-remove',
    handler:function(){delFolderFile()}
}];
$(function(){
    //组织机构树
    var selectedNode = null;//存放被选中的节点对象 临时变量
    $folder_tree = $("#folder_tree").tree({
        url : ctxAdmin+"/disk/diskTree",
        formatter:function(node){
            return node.text;
        },
        onClick:function(node){
//                $(this).tree('beginEdit',node.target);
        },
        onBeforeSelect:function(node){
            selectedNode = node;
            return true;
        },
        onBeforeLoad:function(node, param){
            $("#folder_tree").html("数据加载中...");
        },
        onSelect:function(node){
            var nType = node.attributes["nType"];
            operate_all = node.attributes["operate"];
            var parentNode = $folder_tree.tree("getParent",node.target);
            while(operate_all == null && parentNode != null){//查找父级权限控制
                operate_all = parentNode.attributes["operate"];
                parentNode = $folder_tree.tree("getParent",parentNode.target);
            }

            var _toolbar = null;
            var _queryParams = {};
            if(true == operate_all){
                _toolbar = folder_file_toolbar
            }
            if("Folder" == nType){
                _queryParams  = {folderId:node.id}//文件夹ID
            }
            $folder_file_datagrid.datagrid({
                toolbar:_toolbar,
                queryParams:_queryParams
            });
        },
        onContextMenu: function (e, node) {
            e.preventDefault();
            contextMenuNode = node;
            var nType = node.attributes["nType"];
            var treeName = 'folder_treeMenu_add';
            if("Folder" == nType && true == node.attributes.operate){
                treeName = 'folder_treeMenu_all';
            }else if("Folder" == nType){
                treeName = 'folder_treeMenu';
            }

            $("#"+treeName).menu({onClick:function(item){
                if("addFolder" == item.name ){
                    showFolderDialog();
                }else if("editFolder" == item.name){
                    showFolderDialog(node.id);
                }else if("deleteFolder" == item.name){
                    delFolder(node.id,node.text);
                }
            }}).menu('show', {
                left: e.pageX,
                top: e.pageY
            });

        },
        onLoadSuccess:function(node, data){
            contextMenuNode = undefined;
            var folderId = '';
            if(selectedNode !=null){
                selectedNode = $(this).tree('find', selectedNode.id);
                if(selectedNode !=null){//刷新树后 如果临时变量中存在被选中的节点 则重新将该节点置为被选状态
                    $(this).tree('select', selectedNode.target);
                    folderId = selectedNode.id;
                }
            }
            $(this).tree("collapseAll");
            var rootNodes = $(this).tree("getRoots");
            $.each(rootNodes,function(i,node){
                $folder_tree.tree("expand",node.target);
            });
//            $(this).tree("expandAll");
        }
    });

    //数据列表
    $folder_file_datagrid = $('#folder_file_datagrid').datagrid({
        url:'${ctxAdmin}/disk/folderFileDatagrid',
        fit:true,
        checkOnSelect:false,
        selectOnCheck:false,
        fitColumns:false,//自适应列宽
        striped:true,//显示条纹
        remoteSort:false,//是否通过远程服务器对数据排序
        idField : 'id',
        frozen:true,
        collapsible: true,
        showFooter:true,
        pagination: true,//底部分页
        rownumbers: true,//显示行数
        pageList:[10,20,50,500,9999],
        frozenColumns:[[
            {field:'ck',checkbox:true},
            {field: 'name', title: '文件名',sortable:true, width: 360,
                formatter: function (value, rowData, rowIndex) {
                    return "<a onclick='downloadFile(\"" + rowData.id + "\")'>" + value + "</a>";
                },
                editor: {
                    type: 'validatebox', options: {
                        required : true,
                        missingMessage:'请输入名称！',
                        validType:['minLength[1]','length[1,200]']
                    }
                }
            }
        ]],
        columns:[[
            {field:'id',title:'主键',hidden:true,sortable:true,align:'right',width:80} ,
            {field:'prettyFileSize',title:'文件大小',sortable:true,align:'right',width:120,
                formatter: function (value, rowData, rowIndex) {
                    return value;
                }
            } ,
            {field:'userName',title:'上传人',width:100} ,
            {field:'createTime',title:'创建时间',sortable:true,width:146} ,
            {field:'operate',title:'操作',
                formatter: function (value, rowData, rowIndex) {
                    var operateHtml = "";
                    if(rowData.id){
                        if (rowData.editing){
                            operateHtml = "<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-save\"' onclick='saveFileName(this,"+rowIndex+",\""+rowData.id+"\")' >保存 </a>";
                            if(rowData.id){
                                operateHtml+="&nbsp;<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-cancel\"' onclick='rejectChanges("+rowIndex+");' >取消  </a>";
                            }
                        }else {
                            operateHtml = "<a class='easyui-linkbutton' data-options='iconCls:\"eu-icon-disk_download\"' onclick='downloadFile(\""+rowData.id+"\")'>下载</a>";
                            if(true == operate_all){
                                operateHtml += "&nbsp;<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-edit\"' onclick='beginEdit("+rowIndex+")'>重命名</a>" +
                                        "&nbsp;<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-remove\"' onclick='delFolderFile(\""+rowData.id+"\",\""+rowData.name+"\")'>删除</a>&nbsp;";
                            }
                        }
                    }
                    return operateHtml;
                }}
        ]],
//        onDblClickRow : function(index,row) {
//            $(this).datagrid('beginEdit', index);
//            $(this).datagrid('unselectAll');
//        },
        onBeforeEdit:function(index,row){
            row.editing = true;
            updateActions(index);
            $.parser.parse($(".easyui-linkbutton").parent());
        },
        onAfterEdit:function(index,row){
            row.editing = false;
            updateActions(index);
            $.parser.parse($(".easyui-linkbutton").parent());
        },
        onCancelEdit:function(index,row){
            row.editing = false;
            updateActions(index);
            $.parser.parse($(".easyui-linkbutton").parent());
        },
        onLoadSuccess: function () {
            $(this).datagrid('clearSelections');//取消所有的已选择项
            $(this).datagrid('clearChecked');//取消所有的选中的择项
            $(this).datagrid('unselectAll');//取消全选按钮为全选状态
        }
    }).datagrid('showTooltip');
});
function saveFileName(target,index,id){
    $folder_file_datagrid.datagrid('unselectAll');
    $folder_file_datagrid.datagrid('selectRow', index);
    $folder_file_datagrid.datagrid('endEdit', index);
    var selectRow = $folder_file_datagrid.datagrid('getSelected');
    var valid = $folder_file_datagrid.datagrid('validateRow', index);
    if(!valid){
        var validateEdit = $folder_file_datagrid.datagrid('getEditor',{index:index,field:'name'});
        $(validateEdit.target).focus();
        return false;
    }
    $.ajax({
        type:'POST',
        url:'${ctxAdmin}/disk/fileSave',
        data: $.extend({id:selectRow.id,modelType:'File'},selectRow),
        traditional: true,
        dataType: 'json',
        success:function(data){
            if (data.code == 1) {
                $folder_file_datagrid.datagrid('reload');
                eu.showMsg(data.msg);//操作结果提示
            }
        }

    });
}
function updateActions(index){
    $folder_file_datagrid.datagrid('updateRow',{
        index: index,
        row:{}
    });
}
//开始编辑
function beginEdit(index){
    $folder_file_datagrid.datagrid('beginEdit', index);
}
//撤销
function rejectChanges(index){
    $folder_file_datagrid.datagrid('endEdit', index);
    $folder_file_datagrid.datagrid('rejectChanges', index);
}

//下载文件
function downloadFile(fileId){
    $("#annexFrame").attr("src","${ctxAdmin}/disk/fileDownload/"+fileId);
}



function folderFormInit(){
    $folder_form = $('#folder_form').form({
        url: ctxAdmin+'/disk/saveFolder',
        onSubmit: function(param){
            $.messager.progress({
                title : '提示信息！',
                text : '数据处理中，请稍后....'
            });
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');
            }else{
                param.modelType = "Folder";
            }
            return isValid;
        },
        success: function(data){
            $.messager.progress('close');
            var json = $.parseJSON(data);
            if (json.code ==1){
                $folder_dialog.dialog('destroy');//销毁对话框
                $folder_tree.tree("reload");
                eu.showMsg(json.msg);//操作结果提示
            }else if(json.code == 2){
                $.messager.alert('提示信息！', json.msg, 'warning',function(){
                    if(json.obj){
                        $('#$folder_form input[name="'+json.obj+'"]').focus();
                    }
                });
            }else {
                eu.showAlertMsg(json.msg,'error');
            }
        }
    });
}
//文件夹 弹窗 新增、修改
function showFolderDialog(folderId){
    var inputUrl = ctxAdmin + "/disk/folderInput";
    if (folderId != undefined) {
        inputUrl += "?folderId=" + folderId;
    }else{
        var selectedNode = $folder_tree.tree("getSelected");
        selectedNode = (contextMenuNode != undefined) ? contextMenuNode:selectedNode;
        if(selectedNode){
            var folderAuthorize = '';
            var nType = selectedNode.attributes['nType'];
            var organType = selectedNode.attributes['type'];
            var organId = '';
            var parentFolderId = '';
            if("FolderAuthorize" == nType){//一级目录
                folderAuthorize = selectedNode.id;
            }else if("Organ" == nType || "1" == organType){//部门
                folderAuthorize = '2';//部门
                organId = selectedNode.id;
            }else if("Folder" == nType){//文件夹目录
                parentFolderId = selectedNode.id;
                var parentNode = $folder_tree.tree("getParent",selectedNode.target);
                if(parentNode != undefined && "Organ" == parentNode.attributes['nType']){
                    organId = parentNode.id;
                }
                var nodeLevel = $folder_tree.tree("getLevel",selectedNode.target);
                while(parentNode != undefined && nodeLevel != 2){
                    nodeLevel = $folder_tree.tree("getLevel",parentNode.target);
                    parentNode = $folder_tree.tree("getParent",parentNode.target);
                    if('' == organId && "Organ" == parentNode.attributes['nType']){
                        organId = parentNode.id;
                    }
                }
                if(parentNode != undefined){
                    folderAuthorize = parentNode.id;
                }
            }
            inputUrl += "?folderAuthorize=" + folderAuthorize+"&parentFolderId="+parentFolderId+"&organId="+organId;
        }

    }

    //弹出对话窗口
    $folder_dialog = $('<div/>').dialog({
        title:'文件夹信息',
        top:20,
        width : 500,
        height: 360,
        modal : true,
        maximizable:true,
        href : inputUrl,
        buttons : [ {
            text : '保存',
            iconCls : 'easyui-icon-save',
            handler : function() {
                $folder_form.submit();
            }
        },{
            text : '关闭',
            iconCls : 'easyui-icon-cancel',
            handler : function() {
                $folder_dialog.dialog('destroy');
            }
        }],
        onClose : function() {
            $(this).dialog('destroy');
        },
        onLoad:function(){
            folderFormInit();
        }
    }).dialog('open');

}
/**
 * 删除文件夹
 */
function delFolder(folderId,folderName){
    $.messager.confirm('确认提示！', '您确定要删除['+folderName+'],包含下级文件夹以及文件?', function (r) {
        if (r) {
            $.messager.progress({
                title: '提示信息！',
                text: '数据处理中，请稍后....'
            });
            $.ajax({
                url: '${ctxAdmin}/disk/folderRemove/'+folderId,
                type: 'post',
                dataType: 'json',
                traditional: true,
                success: function (data) {
                    $.messager.progress('close');
                    if (data.code == 1) {
                        $folder_tree.tree('reload');
                        $folder_file_datagrid.datagrid('reload');
                        eu.showMsg(data.msg);//操作结果提示
                    } else {
                        eu.showAlertMsg(data.msg, 'error');
                    }
                }
            });
        }
    });
}


/**
 * 上传文件
 */
function addFolderFile(){
    var selectedNode = $folder_tree.tree("getSelected");
    if(selectedNode != undefined && selectedNode.id != undefined){
        _dialog = $("<div/>").dialog({
            title: "上传文件",
            top: 10,
            href: '${ctxAdmin}/disk/fileInput?folderId='+selectedNode.id,
            width: 500,
            height: 360,
            maximizable: true,
            iconCls: 'easyui-icon-file',
            modal: true,
            buttons: [{
                    text: '关闭',
                    iconCls: 'easyui-icon-cancel',
                    handler: function () {
                        _dialog.dialog('destroy');
                        $folder_file_datagrid.datagrid("reload");
                    }
                }
            ],
            onClose: function () {
                _dialog.dialog('destroy');
            }
        }).dialog('open');
    }

}
/**
 * 删除文件夹下的文件
 * @param fileId
 */
function delFolderFile(fileId,fileName) {
    var rows = $folder_file_datagrid.datagrid('getChecked');
    if (fileId != undefined || rows.length >0) {
        var tipMsg = "您确定要删除";
        if(fileName != undefined){
            tipMsg += "文件["+fileName+"]?";
        }else{
            tipMsg += "选中的所有文件?";
        }

        $.messager.confirm('确认提示！', tipMsg, function (r) {
            if (r) {
                var selectedFileIds = new Array();
                $.messager.progress({
                    title: '提示信息！',
                    text: '数据处理中，请稍后....'
                });
                if(fileId != undefined){
                    selectedFileIds.push(fileId);
                }else{
                    $.each(rows, function (i, row) {
                        selectedFileIds.push(row.id);
                    });
                }

                $.ajax({
                    url: '${ctxAdmin}/disk/delFolderFile',
                    type: 'post',
                    data: {fileIds: selectedFileIds},
                    dataType: 'json',
                    traditional: true,
                    success: function (data) {
                        $.messager.progress('close');
                        if (data.code == 1) {
                            $folder_file_datagrid.datagrid("reload");
                            eu.showMsg(data.msg);//操作结果提示
                        } else {
                            eu.showAlertMsg(data.msg, 'error');
                        }
                    }
                });
            }
        });

    } else {
        eu.showMsg("请选择要操作的对象！");
    }

}

function search(){
    var selectedNode = $folder_tree.tree("getSelected");
    var folderId = '';
    if(selectedNode){
        folderId = selectedNode.id;
    }

    $folder_file_datagrid.datagrid({
        queryParams:{folderId:folderId,fileName:$("#fileName").val()}
    });
}
</script>
<div id="folder_treeMenu_all" class="easyui-menu" style="width:120px;">
    <div name="addFolder" data-options="iconCls:'easyui-icon-add'">新建文件夹</div>
    <div name="editFolder" data-options="iconCls:'easyui-icon-edit'">编辑</div>
    <div name="deleteFolder" data-options="iconCls:'easyui-icon-remove'">删除</div>
</div>
<div id="folder_treeMenu_add" class="easyui-menu" style="width:120px;">
    <div name="addFolder" data-options="iconCls:'easyui-icon-add'">新建文件夹</div>
</div>
<div id="folder_treeMenu" class="easyui-menu" style="width:120px;">
    <div name="editFolder" data-options="iconCls:'easyui-icon-edit'">编辑</div>
    <div name="deleteFolder" data-options="iconCls:'easyui-icon-remove'">删除</div>
</div>
<%-- easyui-layout布局 --%>
<div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">

    <%-- 左边部分 菜单树形 --%>
    <div data-options="region:'west',title:'我的云盘',split:true,collapsed:false,border:false"
         style="width: 260px; text-align: left;padding:5px;">
        <div style="padding: 5px;">

            <a onclick="showFolderDialog();" class="easyui-linkbutton"
               data-options="iconCls:'eu-icon-group',toggle:true,selected:true"
               style="width: 138px;">新建文件夹</a>
            <span class="tree-icon tree-file easyui-icon-tip easyui-tooltip" data-options="position:'right'"
                  title="点击鼠标右键，你会发现惊喜." ></span>
        </div>
        <hr>
        <ul id="folder_tree"></ul>
    </div>

    <!-- 中间部分 列表 -->
    <div data-options="region:'center',split:true" style="overflow: hidden;">
        <div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">
            <div data-options="region:'center',split:true" style="overflow: hidden;">
                <table id="folder_file_datagrid" ></table>
            </div>

            <div data-options="region:'north',title:'过滤条件',split:false,collapsed:false,border:false"
                 style="width: 100%;height:70px; ">
                <form id="folder_search_form" style="padding: 5px;">
                    &nbsp;&nbsp;文件名:<input type="text" id="fileName" name="fileName" placeholder="文件名..." class="easyui-validatebox textbox eu-input"
                                           onkeydown="if(event.keyCode==13)search()"  maxLength="25" style="width: 160px"/>
                    <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-search',onClick:search">查询</a>
                    <iframe id="annexFrame" src="" frameborder="no" style="padding: 0;border: 0;width: 300px;height: 26px;"></iframe>
                    <%--<a class="easyui-linkbutton" href="#" data-options="iconCls:'icon-no'" onclick="javascript:$folder_file_search_form.form('reset');">重置查询</a>--%>
                </form>
            </div>
        </div>
    </div>
</div>