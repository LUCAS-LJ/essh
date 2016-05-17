<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<script type="text/javascript">
    var $folder_from_organ_div = $("#organ_div");
    var $folder_from_role_div = $("#role_div");
    var $folder_from_folderAuthorize_div = $("#folderAuthorize_div");
    var $parentId_combotree,$folderAuthorize_combobox,$organId_combobox,$roleId_combobox;
    var folderAuthorizeValue = '${not empty folderAuthorize ? folderAuthorize:model.folderAuthorize}';
    var folderIdValue = '${not empty folderId ? folderId:model.id}';
    var parentIdValue = '${not empty parentFolderId ? parentFolderId:model.parentId}';
    var organIdValue = '${not empty organId ? organId:model.organId}';
    $(function () {
        $organId_combobox = undefined;
        $roleId_combobox = undefined;
        loadFolderAuthorize();
        loadParent(folderAuthorizeValue,folderIdValue,organIdValue,parentIdValue);
    });

    function loadParent(folderAuthorizeValue,folderIdValue,organIdValue,parentIdValue) {
        $parentId_combotree = $("#parentId").combotree({
            url: '${ctxAdmin}/disk/folderTree?selectType=select&folderAuthorize='+folderAuthorizeValue+'&excludeFolderId='+folderIdValue+"&organId="+organIdValue,
            value:parentIdValue
        });
    }

    function loadFolderAuthorize() {
        $folderAuthorize_combobox = $("#folderAuthorize").combobox({
            url: '${ctxAdmin}/disk/folderAuthorizeCombobox?selectType=select',
            disabled:${not empty model.id ? true:false},
            value:folderAuthorizeValue,
//            validType:['comboboxRequired[\'#folderAuthorize\']'],
            onSelect: function (record) {
                toggole(record.value,true);
            },
            onLoadSuccess: function () {
                var folderAuthorizeValue = $(this).combobox('getValue');
                toggole(folderAuthorizeValue,false);
            }
        });
    }

    function toggole(folderAuthorizeValue,clear){
        if (folderAuthorizeValue == '2') {//机构
            $folder_from_role_div.hide();
            $folder_from_organ_div.show();
            if(!$organId_combobox){
                loadOrgan();
            }

            if(clear){
                $organId_combobox.combotree({validType:null}).combobox("clear");
            }
        } else if (folderAuthorizeValue == '3') {//角色
            $folder_from_organ_div.hide();
            $folder_from_role_div.show();
            if(!$roleId_combobox){
                loadRole();
            }
            if(clear){
                $roleId_combobox.combobox({validType:null}).combobox("clear");
            }
        }else {
            $folder_from_organ_div.hide();
            $folder_from_role_div.hide();
        }
        loadParent(folderAuthorizeValue,folderIdValue,organIdValue,'');
    }

    function loadOrgan() {
        $organId_combobox = $("#organId").combotree({
            url: "${ctxAdmin}/sys/organ/tree?selectType=select",
            valueField: 'id',
            textField: 'text',
            value:organIdValue,
//            validType:['combotreeRequired["#organId"]'],
            onSelect:function(record){
                folderAuthorizeValue = $folderAuthorize_combobox.combobox('getValue');
                loadParent(folderAuthorizeValue,folderIdValue,record.id,'');
            },
            onLoadSuccess: function (a,b) {
                var organId = $organId_combobox.combotree('getValue');
                folderAuthorizeValue = $folderAuthorize_combobox.combobox('getValue');
                loadParent(folderAuthorizeValue,folderIdValue,organId,'');
            }
        });
    }
    function loadRole() {
        $roleId_combobox = $("#roleId").combobox({
            url: '${ctxAdmin}/sys/role/combobox?seletctType=select',
            value:'${model.roleId}',
            validType:['comboboxRequired[\'#roleId\']']
        });
    }

</script>
<div>
    <form id="folder_form" method="post" class="dialog-form" novalidate>
        <input type="hidden" name="id" value="${model.id}"/>
        <!-- 版本控制字段 version -->
        <input type="hidden" id="version" name="version" value="${model.version}"/>

        <div>
            <label>名称:</label>
            <input name="name" type="text" class="easyui-validatebox textbox" value="${model.name}"
                   maxLength="12"
                   data-options="required:true,missingMessage:'请输入名称.',validType:['minLength[1]','legalInput']">
        </div>
        <div>
            <label>归类:</label>
            <input id="folderAuthorize" name="folderAuthorize" style="width: 120px;height: 28px;"/>
        </div>
        <div id="organ_div" style="display: none;">
            <label>所属部门:</label>
            <input id="organId" name="organId" style="width: 260px;height: 28px;" />
        </div>
        <div id="role_div" style="display: none;">
            <label>所属角色:</label>
            <input id="roleId" name="roleId" style="width: 260px;height: 28px;" />
        </div>
        <div>
            <label>上级文件夹:</label>
            <input id="parentId" name="parentId" style="width: 260px;height: 28px;" />
        </div>
        <div>
            <label style="vertical-align: top;">备注:</label>
            <%--<textarea maxLength="255" name="remark"--%>
                      <%--style="position: relative;resize: none;height: 75px;width: 260px;">${model.remark}</textarea>--%>
            <input name="remark" class="easyui-textbox" data-options="multiline:true" style="width:260px;height:100px;" value="${model.remark}" />
        </div>

    </form>
</div>