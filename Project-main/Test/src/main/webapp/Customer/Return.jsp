<!-- Return.jsp -->

<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="database.init.InitDatabase" %>
<%@ page import="database.DatabaseConnection" %>
<%@ page import="database.init.VehicleRentalSystem" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Return Vehicle</title>
</head>
<body>

<h1>Return Vehicle</h1>

<%
    // Check if the form is submitted
    if (request.getMethod().equals("POST")) {
        // Get form parameters
        int rentalId = Integer.parseInt(request.getParameter("rentalId"));

        // Get the current time and format it
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String returnTime = currentDateTime.format(formatter);

        Connection connection = null;
        try {
            // Obtain a database connection
            connection = DatabaseConnection.getConnection();

            // Call the returnRentedVehicle method
            VehicleRentalSystem.returnRentedVehicle(connection, rentalId, returnTime);

            out.println("Vehicle Returned Successfully");
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

<form method="post" action="Return.jsp">
    <%--@declare id="rentalid"--%>
    <label for="rentalId">Rental ID:</label>
    <input type="number" name="rentalId" required><br>

    <!-- Declare the formatter variable -->
    <% DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); %>

    <!-- Display the current time -->
    <p>Current Time: <%= LocalDateTime.now().format(formatter) %>
    </p>

    <!-- Use a hidden input field to submit the current time along with the form -->
    <input type="hidden" name="returnTime" value="<%= LocalDateTime.now().format(formatter) %>">

    <input type="submit" value="Return Vehicle">
</form>

</body>
</html>
