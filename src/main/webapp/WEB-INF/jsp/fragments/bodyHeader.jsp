<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<header><a href="${pageContext.request.contextPath}/"><spring:message code="app.title"/></a>&nbsp;|&nbsp;<a
        href="meals"><spring:message
        code="app.title"/></a></header>
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <a href="meals" class="navbar-brand"><spring:message code="app.title"/></a>

        <div class="collapse navbar-collapse">
            <form class="navbar-form navbar-right">
                <sec:authorize access="hasRole('ADMIN')">
                    <a class="btn btn-info" href="users"><spring:message code="users.title"/></a></sec:authorize>
                <a class="btn btn-primary" href="logout">
                    <span class="glyphicon glyphicon-log-out" aria-hidden="true"></span>
                </a>
            </form>
        </div>
    </div>
</div>