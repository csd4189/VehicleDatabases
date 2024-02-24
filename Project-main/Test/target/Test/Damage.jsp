<!-- Damage.jsp -->

<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="database.init.InitDatabase" %>
<%@ page import="database.DatabaseConnection" %>
<%@ page import="java.util.Vector" %>
<%@ page import="database.init.VehicleRentalSystem" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Report Damage/Maintenance</title>
</head>
<body>

<h1>Report Damage/Maintenance</h1>

<%
    // Check if the form is submitted
    if (request.getMethod().equals("POST")) {
        // Get form parameters
        int rentId = Integer.parseInt(request.getParameter("rentId"));
        String flag = request.getParameter("flag");

        Connection connection = null;
        try {
            // Obtain a database connection
            connection = DatabaseConnection.getConnection();

            // Call the reportDamageAndRepair method
            VehicleRentalSystem.reportDamageAndRepair(connection, rentId, flag);

            out.println("Damage/Maintenance Reported Successfully");
        } catch (SQLException e) {
            out.println("Error: " + e.getMessage());
        } finally {
            // Close the database connection
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
%>

<form method="post" action="Damage.jsp">
    <%--@declare id="rentid"--%><%--@declare id="flag"--%>
    <label for="rentId">Rental ID:</label>
    <input type="number" name="rentId" required><br>

    <label for="flag">Report Type:</label>
    <select name="flag" required>
        <option value="Damaged">Damage</option>
        <option value="Maintenance">Maintenance</option>
    </select><br>

    <input type="submit" value="Report Damage/Maintenance">
</form>

</body>
</html>
