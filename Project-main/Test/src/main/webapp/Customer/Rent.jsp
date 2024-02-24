<!-- Rent.jsp -->

<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="database.init.InitDatabase" %>
<%@ page import="database.DatabaseConnection" %>
<%@ page import="database.init.VehicleRentalSystem" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Rent Vehicle</title>
</head>
<body>

<h1>Rent Vehicle</h1>

<%
    // Check if the form is submitted
    if (request.getMethod().equals("POST")) {
        // Get form parameters
        int customerId = Integer.parseInt(request.getParameter("customerId"));
        int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
        String rentalStartDate = request.getParameter("rentalStartDate");
        String rentalEndDate = request.getParameter("rentalEndDate");
        String driverLicense = request.getParameter("driverLicense");
        boolean insurance = request.getParameter("insurance") != null;

        out.println(request.getParameter("driverLicense"));
        Connection connection = null;
        try {
            // Obtain a database connection
            connection = DatabaseConnection.getConnection();

            // Call the recordVehicleRental method
            VehicleRentalSystem.recordVehicleRental(connection, customerId, vehicleId,
                    rentalStartDate, rentalEndDate, driverLicense, insurance);

            out.println("Vehicle Rental Recorded Successfully");
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

<form method="post" action="Rent.jsp">
    <%--@declare id="customerid"--%><%--@declare id="vehicleid"--%><%--@declare id="rentalstartdate"--%><%--@declare id="rentalenddate"--%><%--@declare id="driverlicense"--%><%--@declare id="insurance"--%>
    <label for="customerId">Customer ID:</label>
    <input type="number" name="customerId" required><br>

    <label for="vehicleId">Vehicle ID:</label>
    <input type="number" name="vehicleId" required><br>

    <label for="rentalStartDate">Rental Start Date:</label>
    <input type="text" name="rentalStartDate" placeholder="YYYY-MM-DD HH:MM:SS" required><br>

    <label for="rentalEndDate">Rental End Date:</label>
    <input type="text" name="rentalEndDate" placeholder="YYYY-MM-DD HH:MM:SS" required><br>

    <label for="driverLicense">Driver's License:</label>
    <input type="text" name="driverLicense"><br>

    <label for="insurance">Insurance:</label>
    <input type="checkbox" name="insurance"><br>

    <input type="submit" value="Record Vehicle Rental">
</form>

</body>
</html>
