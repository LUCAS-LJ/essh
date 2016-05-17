var $dictionary_datagrid;
var editRow = undefined;
var editRowData = undefined;
var $dictionary_search_form;
var dictionaryTypeCode = undefined;
var dictionary_filter_EQS_dictionaryType__code;
$(function() {
    $dictionary_search_form = $('#dictionary_search_form').form();
    //数据列表
    $dictionary_datagrid = $('#dictionary_datagrid').datagrid({
        url:ctxAdmin+'/sys/dictionary/datagrid',
        fit:true,
        pagination:true,//底部分页
        pagePosition:'bottom',//'top','bottom','both'.
        fitColumns:false,//自适应列宽
        striped:true,//显示条纹
        pageSize:20,//每页记录数
        singleSelect:false,//单选模式
        rownumbers:true,//显示行数
        checkbox:true,
        nowrap : true,
        border : false,
        remoteSort:false,//是否通过远程服务器对数据排序
        sortName:'orderNo',//默认排序字段
        sortOrder:'asc',//默认排序方式 'desc' 'asc'
        idField : 'id',
        frozenColumns:[[
            {field : 'ck',checkbox : true,width : 60},{
                field : 'dictionaryTypeCode',
                title : '字典类型',
                width : 150,
                formatter : function(value, rowData, rowIndex) {
                    return rowData.dictionaryTypeName;
                },
                editor : {
                    type : 'combobox',
                    options : {
                        url:ctxAdmin+'/sys/dictionary-type/combobox',
                        required : true,
                        missingMessage:'请选择字典类型(如果不存在,可以选择[字典类型管理]按钮,添加字典类型)！',
                        editable:false,//是否可编辑
                        groupField:'group',
                        onSelect:function(record){
                            dictionaryTypeCode = record.value;
                            var dictionaryTypeEditor = $dictionary_datagrid.datagrid('getEditor',{index:editRow,field:'parentDictionaryCode'});
                            $(dictionaryTypeEditor.target).combotree('clear').combotree('reload');
                            var codeEditor = $dictionary_datagrid.datagrid('getEditor',{index:editRow,field:'code'});
                            var vallueEditor = $dictionary_datagrid.datagrid('getEditor',{index:editRow,field:'value'})
                            $(codeEditor.target).textbox("setValue",dictionaryTypeCode);
                            $(vallueEditor.target).textbox("setValue",dictionaryTypeCode);
                        }
                    }
                }
            }, {
                field : 'name',
                title : '名称',
                width : 200,
                editor : {
                    type : 'textbox',
                    options : {
                        required : true,
                        missingMessage:'请输入名称！',
                        validType:['minLength[1]','length[1,64]','legalInput']
                    }
                }
            }
        ]],
        columns:[ [
            {field : 'id',title : '主键',hidden : true,sortable:true,align : 'right',width : 80},{
                field : 'parentDictionaryCode',
                title : '上级节点',
                width : 200,
                formatter : function(value, rowData, rowIndex) {
                    return rowData.parentDictionaryName;
                },
                editor : {
                    type : 'combotree',
                    options : {
                        url:ctxAdmin+'/sys/dictionary/combotree?selectType=select',
                        onBeforeLoad:function(node,param){
                            if(dictionaryTypeCode != undefined){
                                param.dictionaryTypeCode = dictionaryTypeCode;
                            }
                            if(editRowData != undefined){
                                param.id = editRowData.id;
                            }
                        }
                    }
                }
            },{
                field : 'code',
                title : '编码',
                width : 100,
                sortable:true,
                editor : {
                    type : 'textbox',
                    options : {
                        required : true,
                        missingMessage:'请输入编码！',
                        validType:['minLength[1]','length[1,36]','legalInput']
                    }
                }
            },{
                field : 'value',
                title : '属性值',
                width : 120,
                sortable:true,
                editor : {
                    type : 'textbox',
                    options : {
                    }
                }
            }, {
                field : 'remark',
                title : '备注',
                width : 200,
                editor : {
                    type : 'textbox',
                    options : {
                    }
                }
            }, {
                field : 'orderNo',
                title : '排序',
                align : 'right',
                width : 60,
                sortable:true,
                editor : {
                    type : 'numberspinner',
                    options : {
                        required : true
                    }
                }
            }] ],
        toolbar:[{
            text:'新增',
            iconCls:'easyui-icon-add',
            handler:function(){add()}
        },'-',{
            text:'编辑',
            iconCls:'easyui-icon-edit',
            handler:function(){edit()}
        },'-',{
            text:'删除',
            iconCls:'easyui-icon-remove',
            handler:function(){del()}
        },'-',{
            text:'保存',
            iconCls:'easyui-icon-save',
            handler:function(){save()}
        },'-',{
            text:'取消编辑',
            iconCls:'easyui-icon-undo',
            handler:function(){cancelEdit()}
        },'-',{
            text:'取消选中',
            iconCls:'easyui-icon-undo',
            handler:function(){cancelSelect()}
        }],
        onDblClickRow : function(rowIndex, rowData) {
            if (editRow != undefined) {
                eu.showMsg("请先保存正在编辑的数据！");
                //dictionary_datagrid.datagrid('endEdit', editRow);
            }else{
                $(this).datagrid('beginEdit', rowIndex);
                $(this).datagrid('unselectAll');
                bindCodeEvent(rowIndex);
            }
        },
        onBeforeEdit:function(rowIndex, rowData) {
            editRow = rowIndex;
            editRowData = rowData;
            dictionaryTypeCode = rowData.dictionaryTypeCode;
        },
        onAfterEdit : function(rowIndex, rowData, changes) {
            $.messager.progress({
                title : '提示信息！',
                text : '数据处理中，请稍后....'
            });
            var inserted = $dictionary_datagrid.datagrid('getChanges', 'inserted');
            var updated = $dictionary_datagrid.datagrid('getChanges', 'updated');
            if (inserted.length < 1 && updated.length < 1) {
                editRow = undefined;
                editRowData = undefined;
                $(this).datagrid('unselectAll');
                $.messager.progress('close');
                eu.showMsg("数据未更新!");
                return;
            }
            $.post(ctxAdmin+'/sys/dictionary/_save',rowData,
                function(data) {
                    $.messager.progress('close');
                    if (data.code == 1) {
                        $dictionary_datagrid.datagrid('acceptChanges');
                        cancelSelect();
                        $dictionary_datagrid.datagrid('reload');
                        eu.showMsg(data.msg);
                    }else{// 警告信息
                        $.messager.alert('提示信息！', data.msg, 'warning',function(){
                            $dictionary_datagrid.datagrid('beginEdit', editRow);
                            if(data.obj){//校验失败字段 获取焦点
                                var validateEdit = $dictionary_datagrid.datagrid('getEditor',{index:rowIndex,field:data.obj});
                                $(validateEdit.target).focus();
                            }
                        });
                    }
                }, 'json');
        },
        onLoadSuccess:function(data){
            $(this).datagrid('clearSelections');//取消所有的已选择项
            $(this).datagrid('unselectAll');//取消全选按钮为全选状态
            editRow = undefined;
            editRowData = undefined;
            dictionaryTypeCode = undefined;

        },
        onRowContextMenu : function(e, rowIndex, rowData) {
            e.preventDefault();
            $(this).datagrid('unselectAll');
            $(this).datagrid('selectRow', rowIndex);
            $('#dictionary_menu').menu('show', {
                left : e.pageX,
                top : e.pageY
            });
        }
    }).datagrid('showTooltip');

    dictionary_filter_EQS_dictionaryType__code = $('#filter_EQS_dictionaryType__code').combobox({
        url:ctxAdmin+'/sys/dictionary-type/combobox?selectType=all',
        multiple:false,//是否可多选
        editable:false,//是否可编辑
        height:28,
        width:120,
        groupField:'group'
    });
});

//字典编码 editor绑定change事件
function bindCodeEvent(rowIndex){
    // 绑定事件监听
    var codeEditor = $dictionary_datagrid.datagrid('getEditor', {index:rowIndex,field:'code'});
    var valueEditor =  $dictionary_datagrid.datagrid('getEditor', {index:rowIndex,field:'value'});
    codeEditor.target.bind('change', function(){
        $(valueEditor.target).val($(this).val())
    });
}
//字典类型管理
function dictionaryType(){
    //widow.parent.$layout_center_tabs 指向父级$layout_center_tabs选项卡(center.jsp)
    eu.addTab(window.parent.$layout_center_tabs,"字典类型管理",ctxAdmin+"/sys/dictionary-type",true,"easyui-icon-folder");
}

//设置排序默认值
function setSortValue(target) {
    $.get(ctxAdmin+'/sys/dictionary/maxSort', function(data) {
        if (data.code == 1) {
            $(target).numberbox({value:data.obj + 1});
            $(target).numberbox('validate');
        }
    }, 'json');
}

//新增
function add() {
    if (editRow != undefined) {
        eu.showMsg("请先保存正在编辑的数据！");
        //结束编辑 自动保存
        //dictionary_datagrid.datagrid('endEdit', editRow);
    }else{
        cancelSelect();
        var row = {id : ''};
        $dictionary_datagrid.datagrid('appendRow', row);
        editRow = $dictionary_datagrid.datagrid('getRows').length - 1;
        $dictionary_datagrid.datagrid('selectRow', editRow);
        $dictionary_datagrid.datagrid('beginEdit', editRow);
        var rowIndex = $dictionary_datagrid.datagrid('getRowIndex',row);//返回指定行的索引
        var sortEdit = $dictionary_datagrid.datagrid('getEditor',{index:rowIndex,field:'orderNo'});
        setSortValue(sortEdit.target);
        bindCodeEvent(rowIndex);
    }
}

//编辑
function edit() {
    //选中的所有行
    var rows = $dictionary_datagrid.datagrid('getSelections');
    //选中的行（第一次选择的行）
    var row = $dictionary_datagrid.datagrid('getSelected');
    if (row){
        if(rows.length>1){
            row = rows[rows.length-1];
            eu.showMsg("您选择了多个操作对象，默认操作第一次被选中的记录！");
        }
        if (editRow != undefined) {
            eu.showMsg("请先保存正在编辑的数据！");
            //结束编辑 自动保存
            //dictionary_datagrid.datagrid('endEdit', editRow);
        }else{
            editRow = $dictionary_datagrid.datagrid('getRowIndex', row);
            $dictionary_datagrid.datagrid('beginEdit', editRow);
            cancelSelect();
            bindCodeEvent(editRow);
        }
    } else {
        if(editRow != undefined){
            eu.showMsg("请先保存正在编辑的数据！");
        } else{
            eu.showMsg("您未选择任何操作对象，请选择一行数据！");
        }
    }
}

//保存
function save(rowData) {
    if (editRow != undefined) {
        $dictionary_datagrid.datagrid('endEdit', editRow);
    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

//取消编辑
function cancelEdit() {
    cancelSelect();
    $dictionary_datagrid.datagrid('rejectChanges');
    editRow = undefined;
    editRowData = undefined;
    dictionaryTypeCode = undefined;
}
//取消选择
function cancelSelect() {
    $dictionary_datagrid.datagrid('unselectAll');
}

//删除
function del() {
    var rows = $dictionary_datagrid.datagrid('getSelections');
    if (rows.length > 0) {
        if(editRow != undefined){
            eu.showMsg("请先保存正在编辑的数据！");
            return;
        }
        $.messager.confirm('确认提示！', '您确定要删除当前选中的所有行？', function(r) {
            if (r) {
                var ids = new Array();
                $.each(rows,function(i,row){
                    ids[i] = row.id;
                });
                $.ajax({
                    url:ctxAdmin+'/sys/dictionary/remove',
                    type:'post',
                    data: {ids:ids},
                    traditional:true,
                    dataType:'json',
                    success:function(data) {
                        if (data.code==1){
                            $dictionary_datagrid.datagrid('clearSelections');//取消所有的已选择项
                            $dictionary_datagrid.datagrid('load');//重新加载列表数据
                            eu.showMsg(data.msg);//操作结果提示
                        } else {
                            eu.showAlertMsg(data.msg,'error');
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
    $dictionary_datagrid.datagrid('load',$.serializeObject($dictionary_search_form));
}