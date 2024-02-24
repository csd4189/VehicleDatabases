<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.Map" %>
<%@ page import="database.init.InitDatabase" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Vehicle Search Results</title>
</head>
<body>

<%
    InitDatabase.searchAvailableVehicles();
    InitDatabase.availableRentedVehicles();
%>

</body>
</html>
