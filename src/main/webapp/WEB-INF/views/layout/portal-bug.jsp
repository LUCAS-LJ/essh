<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    function viewBug(title,id){
        eu.addTab(window.parent.$layout_center_tabs, title,'${ctxAdmin}/sys/bug/view?id='+id, true,"","",true);
    }
</script>
<div style="width: 100%;height: 100%">
    <table class="table table-striped">
        <%--<thead>--%>
        <%--<tr>--%>
        <%--<th>名称</th>--%>
        <%--</tr>--%>
        <%--</thead>--%>
        <tbody>

        <c:if test="${not empty bugDatagrid.rows}">
            <c:forEach items="${bugDatagrid.rows}" begin="0" end="4" var="bug">
                <c:set var="title" value="${bug.title}" />
                <tr>
                    <td>
                        <a href="#" class="easyui-tooltip" style='color:${bug.color}'  title="${title}" onclick="viewBug('${title}','${bug.id}')">
                                ${fns:abbr(title,45)}
                        </a>
                    </td>
                    <td align="right">
                        <fmt:formatDate value="${bug.createTime }" pattern="MM-dd HH:mm"/>
                    </td>
                </tr>
            </c:forEach>
        </c:if>

        </tbody>
    </table>
</div>