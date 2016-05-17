<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<script type="text/javascript">
    var user_combogrid;
    var $multiSelectUser;
    $(function () {
        initSelectUser();
//        loadUser();
    });

    function selectUser() {
        var userIds = "";
        var dataItems = $multiSelectUser.dataItems();
        if (dataItems && dataItems.length >0) {
            var num = dataItems.length;
            $.each(dataItems, function (n, value) {
                if (n == num - 1) {
                    userIds += value.id;
                } else {
                    userIds += value.id + ",";
                }

            });

        }
        _dialog = $("<div/>").dialog({
            title: "选择用户",
            top: 10,
            href: '${ctxAdmin}/sys/user/select?grade=0&userIds=' + userIds,
            width: '700',
            height: '450',
            maximizable: true,
            iconCls: 'eu-icon-user',
            modal: true,
            buttons: [
                {
                    text: '确定',
                    iconCls: 'easyui-icon-save',
                    handler: function () {
                        setSelectUser();
                        _dialog.dialog('destroy');
                    }
                },
                {
                    text: '关闭',
                    iconCls: 'easyui-icon-cancel',
                    handler: function () {
                        _dialog.dialog('destroy');

                    }
                }
            ],
            onClose: function () {
                _dialog.dialog('destroy');
            }
        });
    }

    /**
     *
     */
    function initSelectUser() {
        $.ajax({
            type: "post",
            dataType: 'json',
            contentType: "application/json",
            url: "${ctxAdmin}/sys/user/combogridAll",
//            async: true,
            success: function (data) {
                $multiSelectUser = $("#userIds").kendoMultiSelect({
                    dataTextField: "loginName",
                    dataValueField: "id",
                    dataSource: data.rows,
                    value:${userIds},
                    dataBound: function (e) {

                    }

                }).data("kendoMultiSelect");
            }
        });

    }

    function setSelectUser() {
        var selectUserIds = new Array();
        $("#selectUser option").each(function () {
            var txt = $(this).val();
            selectUserIds.push($.trim(txt));
        });
        $multiSelectUser.value(selectUserIds);
    }
</script>
<div>
    <form id="role_user_form" class="dialog-form" method="post">
        <input type="hidden" id="id" name="id"/>
        <!-- 用户版本控制字段 version -->
        <input type="hidden" id="version" name="version"/>

        <table style="border: 0px;width: 100%;">
            <tr>
                <td style="display: inline-block; width: 96px; vertical-align: top;">角色用户:</td>
                <td>
                    <select id="userIds" name="userIds" multiple="true"
                            style="width:70%; float:left;margin-left:1px;margin-right:2px;"> </select>
                    <a href="#" class="easyui-linkbutton" data-options="iconCls:'easyui-icon-user'" style="width: 100px;" onclick="selectUser();">选择</a>
                </td>
            </tr>
        </table>
    </form>
</div>