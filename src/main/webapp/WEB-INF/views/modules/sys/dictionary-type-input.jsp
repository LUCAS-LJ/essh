<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
	$(function() {
        loadGroup();
	});
	//加载分组
	function loadGroup(){
        $('#_parentId').combobox({
            url:'${ctxAdmin}/sys/dictionary-type/group_combobox?selectType=select',
            multiple:false,//是否可多选
            editable:false,//是否可编辑
            height:28,
            width:120,
            valueField:'value',
            textField:'text',
            onHidePanel:function(){
                //防止自关联
                if($('#id').val() != undefined && $(this).combobox('getValue') == $('#code').val()){
                    $(this).combobox('setValue','');
                }
            }
        });
	}


</script>
<div>
	<form id="dictionaryType_form" class="dialog-form" method="post">
		<input type="hidden" id="id" name="id" />
        <!-- 用户版本控制字段 version -->
        <input type="hidden" id="version" name="version" />
	    <div>
			<label>分组类型:</label>
			<input id="_parentId" name="groupDictionaryTypeCode" />
		</div>
		<div>
			<label>类型名称:</label>
			<input type="text" id="name" name="name"  maxLength="20"
				class="easyui-validatebox textbox"   placeholder="请输入类型名称..."
				data-options="required:true,missingMessage:'请输入类型名称.'" />
		</div>
		<div>
			<label>类型编码:</label>
			<input type="text" id="code" name="code"
				maxLength="36" class="easyui-validatebox textbox" placeholder="请输入类型编码..."
				data-options="required:true,missingMessage:'请输入类型编码.',validType:['minLength[1]']" />
		</div>
		<div>
			<label>排序:</label>
			<input type="text" id="orderNo" name="orderNo" class="easyui-numberspinner" style="width: 120px;"
                   data-options="min:1,max:99999999,size:9,maxlength:9,height:28" />
		</div>
        <div>
            <label style="vertical-align: top;">备注:</label>
            <%--<textarea maxLength="255" name="remark"--%>
                      <%--style="position: relative; resize: none; height: 75px; width: 260px;"></textarea>--%>
            <input name="remark" class="easyui-textbox" maxLength="255"  data-options="multiline:true" style="width:260px;height:75px;">
        </div>
	</form>
</div>