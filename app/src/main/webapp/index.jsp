<%@ page import="org.keycloak.admin.client.Keycloak" %>
<%@ page import="org.keycloak.admin.client.resource.ClientsResource" %>
<%@ page import="org.keycloak.common.util.UriUtils" %>
<%@ page import="org.keycloak.representations.idm.ClientRepresentation" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page session="false" %>
<html>
<head>
    <title>Applications</title>

</head>
<body>
    <jsp:useBean id="controller" class="org.keycloak.quickstart.profilejee.Controller" scope="request"/>
<% controller.handleLogout(request); %>
<br><br>
</body>
</html>
