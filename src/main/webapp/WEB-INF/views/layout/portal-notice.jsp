<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<script type="text/javascript">
    function viewNotice(title, id) {
        $("#notice_icon_" + id).removeClass('tree-icon tree-file eu-icon-star_yellow').addClass("tree-icon tree-file eu-icon-star_gray")
                .attr("title", "已读");
        $("#notice_label_" + id).removeClass("tip_unread").html("[已读]");
        eu.addTab(window.parent.$layout_center_tabs, "我的通知", '${ctxAdmin}/notice/notice?noticeId=' + id, true, "eu-icon-notice_user_comment", "", true);
    }
</script>
<div class="portal-div">
    <table class="table table-striped">
        <%--<thead>--%>
        <%--<tr>--%>
        <%--<th>名称</th>--%>
        <%--</tr>--%>
        <%--</thead>--%>
        <tbody>

        <c:forEach items="${noticeScopes}" begin="0" end="19" var="noticeScope">
            <c:set var="title" value="${noticeScope.title}"></c:set>
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${noticeScope.isRead == 1}">
                            <span id="notice_icon_${noticeScope.id}" title="已读"
                                  class="tree-icon tree-file eu-icon-star_gray"></span>
                        </c:when>
                        <c:otherwise>
                            <span id="notice_icon_${noticeScope.id}" title="未读"
                                  class="tree-icon tree-file eu-icon-star_yellow"></span>
                        </c:otherwise>
                    </c:choose>
                        <%--<span class="tree-icon tree-file eu-icon-star_yellow"></span>--%>
                    <c:if test="${noticeScope.isTop eq '1'}"><span style="color: #FE6600;">[顶]</span></c:if>
                    <a href="#" id="notice_${noticeScope.id}" class="easyui-tooltip  portal-span" title="${title}"
                       onclick="viewNotice('${title}','${noticeScope.noticeId}')" data-options="position: 'right'">
                            ${fns:abbr(title,45)} <c:if test="${not empty noticeScope.typeView}">[${noticeScope.typeView}]</c:if><span id="notice_label_${noticeScope.id}"
                                                                                                                                       class="<c:if test="${noticeScope.isRead == 0}">tip_unread</c:if>">[${noticeScope.isReadView}]
                                                            </span>
                    </a>
                </td>
                <td align="right" class="portal-time">
                    <fmt:formatDate value="${noticeScope.publishTime }" pattern="MM-dd HH:mm"/>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <%--<div style="float: right;margin-right: 4px;">--%>
    <%--<button class="btn btn-primary" onclick="eu.addTab(window.parent.$layout_center_tabs,'我的通知','${ctxAdmin}/notice/notice?mode=view',true)" type="button">更 多</button>--%>
    <%--</div>--%>
</div>