<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
	$(function() {
		loadUserRole();
	});
	// 加載用户角色信息
	function loadUserRole() {
	    $('#user_role_form-roleIds').combobox({
	        multiple: true,
            height:28,
	        width: 260,
	        editable:false,
	        url: '${ctxAdmin}/sys/role/combobox'
	    });
	}	
</script>
<div>
	<form id="user_role_form" class="dialog-form" method="post" novalidate>
		<input type="hidden" name="id" /><br>
		<div >
			<label >关联角色:</label>
			<input id="user_role_form-roleIds" name="roleIds" />
		</div>
	</form>
</div>