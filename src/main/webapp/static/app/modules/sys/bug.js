var $bug_datagrid;
var $bug_form;
var $bug_search_form;
var $bug_dialog;

var $bug_import_dialog;//bug导入表单弹出对话框
var $bug_import_form;
$(function () {
    $bug_form = $('#bug_form').form();
    $bug_search_form = $('#bug_search_form').form();
    //数据列表
    $bug_datagrid = $('#bug_datagrid').datagrid({
        url: ctxAdmin + '/sys/bug/datagrid',
        fit: true,
        pagination: true,//底部分页
        rownumbers: true,//显示行数
        fitColumns: false,//自适应列宽
        striped: true,//显示条纹
        nowrap: true,
        pageSize: 20,//每页记录数
        remoteSort: false,//是否通过远程服务器对数据排序
        sortName: 'id',//默认排序字段
        sortOrder: 'asc',//默认排序方式 'desc' 'asc'
        idField: 'id',
        frozenColumns: [
            [
                {field: 'ck', checkbox: true},
                {field: 'title', title: '标题', width: 360, formatter: function (value, rowData, rowIndex) {
                    var html = $.formatString("<span style='color:{0}'>{1}</span>", rowData.color, value);
                    return html;
                }}
            ]
        ],
        columns: [
            [
                {field: 'id', title: '主键', hidden: true, sortable: true, align: 'right', width: 80},
                {field: 'typeName', title: '类型', width: 120 },
                {field: 'operater', title: '操作', width: 260, formatter: function (value, rowData, rowIndex) {
                    var url = $.formatString(ctxAdmin + '/sys/bug/view?id={0}', rowData.id);
                    var operaterHtml = "<a class='easyui-linkbutton' iconCls='easyui-icon-add'  " +
                        "onclick='view(\"" + rowData.title + "\",\"" + url + "\")' >查看</a>"
                        + "&nbsp;<a class='easyui-linkbutton' iconCls='easyui-icon-edit'  href='#' " +
                        "onclick='edit(" + rowIndex + ");' >编辑</a>"
                        + "&nbsp;<a class='easyui-linkbutton' iconCls='easyui-icon-remove'  href='#' " +
                        "onclick='del(" + rowIndex + ");' >删除</a>";
                    return operaterHtml;
                }}
            ]
        ],
        toolbar: [
            {
                text: '新增',
                iconCls: 'easyui-icon-add',
                handler: function () {
                    showDialog()
                }
            },
            '-',
            {
                text: '编辑',
                iconCls: 'easyui-icon-edit',
                handler: function () {
                    edit()
                }
            },
            '-',
            {
                text: '删除',
                iconCls: 'easyui-icon-remove',
                handler: function () {
                    del()
                }
            },
            '-',
            {
                text: 'Excel导出',
                iconCls: 'easyui-icon-edit',
                handler: function () {
                    exportExcel()
                }
            },
            '-',
            {
                text: 'Excel导入',
                iconCls: 'easyui-icon-edit',
                handler: function () {
                    importExcel()
                }
            }
        ],
        onLoadSuccess: function () {
            $(this).datagrid('clearSelections');//取消所有的已选择项
            $(this).datagrid('unselectAll');//取消全选按钮为全选状态
        },
        onRowContextMenu: function (e, rowIndex, rowData) {
            e.preventDefault();
            $(this).datagrid('unselectAll');
            $(this).datagrid('selectRow', rowIndex);
            $('#bug_datagrid_menu').menu('show', {
                left: e.pageX,
                top: e.pageY
            });
        },
        onDblClickRow: function (rowIndex, rowData) {
            edit(rowIndex, rowData);
        }
    }).datagrid('showTooltip');
});
//查看
function view(title, url) {
    if (window.parent.$layout_center_tabs) {
        $bug_datagrid.datagrid('unselectAll');
        eu.addTab(window.parent.$layout_center_tabs, title, url, true);
    }
}

function formInit() {
    $bug_form = $('#bug_form').form({
        url: ctxAdmin + '/sys/bug/_save',
        onSubmit: function (param) {
            $.messager.progress({
                title: '提示信息！',
                text: '数据处理中，请稍后....'
            });
            if (content_kindeditor) {
                content_kindeditor.sync();
            }
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');
            }
            return isValid;
        },
        success: function (data) {
            $.messager.progress('close');
            var json = $.parseJSON(data);
            if (json.code == 1) {
                $bug_dialog.dialog('destroy');//销毁对话框
                $bug_datagrid.datagrid('reload');//重新加载列表数据
                eu.showMsg(json.msg);//操作结果提示
            } else if (json.code == 2) {
                $.messager.alert('提示信息！', json.msg, 'warning', function () {
                    if (json.obj) {
                        $('#bug_form input[name="' + json.obj + '"]').focus();
                    }
                });
            } else {
                eu.showAlertMsg(json.msg, 'error');
            }
        }
    });
}
//显示弹出窗口 新增：row为空 编辑:row有值
function showDialog(row) {
    var inputUrl = ctxAdmin + "/sys/bug/input";
    if (row != undefined && row.id) {
        inputUrl = inputUrl + "?id=" + row.id;
    }

    //弹出对话窗口
    $bug_dialog = $('<div/>').dialog({
        title: '详细信息',
        width: document.body.clientWidth,
        height: document.body.clientHeight,
        modal: true,
        maximizable: true,
        href: inputUrl,
        buttons: [
            {
                text: '保存',
                iconCls: 'easyui-icon-save',
                handler: function () {
                    $bug_form.submit();
                }
            },
            {
                text: '关闭',
                iconCls: 'easyui-icon-cancel',
                handler: function () {
                    $bug_dialog.dialog('destroy');
                }
            }
        ],
        onClose: function () {
            $bug_dialog.dialog('destroy');
        },
        onLoad: function () {
            formInit();
            if (row) {
                $bug_form.form('load', row);
            }
            if (content_kindeditor) {
                content_kindeditor.sync();
            }
        }
    });

}

//编辑
function edit(rowIndex, rowData) {
    //响应双击事件
    if (rowIndex != undefined) {
        $bug_datagrid.datagrid('unselectAll');
        $bug_datagrid.datagrid('selectRow', rowIndex);
        var rowData = $bug_datagrid.datagrid('getSelected');
        $bug_datagrid.datagrid('unselectRow', rowIndex);
        showDialog(rowData);
        return;
    }
    //选中的所有行
    var rows = $bug_datagrid.datagrid('getSelections');
    //选中的行（第一次选择的行）
    var row = $bug_datagrid.datagrid('getSelected');
    if (row) {
        if (rows.length > 1) {
            row = rows[rows.length - 1];
            eu.showMsg("您选择了多个操作对象，默认操作第一次被选中的记录！");
        }
        showDialog(row);
    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

//删除
function del(rowIndex) {
    var rows = new Array();
    var tipMsg = "您确定要删除选中的所有行？";
    if (rowIndex != undefined) {
        $bug_datagrid.datagrid('unselectAll');
        $bug_datagrid.datagrid('selectRow', rowIndex);
        var rowData = $bug_datagrid.datagrid('getSelected');
        rows[0] = rowData;
        $bug_datagrid.datagrid('unselectRow', rowIndex);
        tipMsg = "您确定要删除？";
    } else {
        rows = $bug_datagrid.datagrid('getSelections');
    }

    if (rows.length > 0) {
        $.messager.confirm('确认提示！', tipMsg, function (r) {
            if (r) {
                var ids = new Array();
                $.each(rows, function (i, row) {
                    ids[i] = row.id;
                });
                $.ajax({
                    url: ctxAdmin + '/sys/bug/remove',
                    type: 'post',
                    data: {ids: ids},
                    traditional: true,
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == 1) {
                            $bug_datagrid.datagrid('load');	// reload the user data
                            eu.showMsg(data.msg);//操作结果提示
                        } else {
                            eu.showAlertMsg(data.msg, 'error');
                        }
                    }
                });
            }
        });
    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

//搜索
function search() {
    $bug_datagrid.datagrid('load', $.serializeObject($bug_search_form));
}

//导出Excel
function exportExcel() {
    $('#bug_temp_iframe').attr('src', ctxAdmin + '/sys/bug/exportExcel');
}

function importFormInit() {
    $bug_import_form = $('#bug_import_form').form({
        url: ctxAdmin + '/sys/bug/importExcel',
        onSubmit: function (param) {
            $.messager.progress({
                title: '提示信息！',
                text: '数据处理中，请稍后....'
            });
            return $(this).form('validate');
        },
        success: function (data) {
            $.messager.progress('close');
            var json = $.parseJSON(data);
            if (json.code == 1) {
                $bug_import_dialog.dialog('destroy');//销毁对话框
                $bug_datagrid.datagrid('reload');//重新加载列表数据
                eu.showMsg(json.msg);//操作结果提示
            } else {
                eu.showAlertMsg(json.msg, 'error');
            }
        }
    });
}

//导入
function importExcel() {
    $bug_import_dialog = $('<div/>').dialog({//基于中心面板
        title: 'Excel导入',
        top: 20,
        height: 200,
        width: 500,
        modal: true,
        maximizable: true,
        href: ctxAdmin + '/sys/bug/import',
        buttons: [
            {
                text: '保存',
                iconCls: 'easyui-icon-save',
                handler: function () {
                    $bug_import_form.submit();
                }
            },
            {
                text: '关闭',
                iconCls: 'easyui-icon-cancel',
                handler: function () {
                    $bug_import_dialog.dialog('destroy');
                }
            }
        ],
        onClose: function () {
            $bug_import_dialog.dialog('destroy');
        },
        onLoad: function () {
            importFormInit();
        }
    });
}