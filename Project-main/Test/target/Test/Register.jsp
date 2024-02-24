<!-- Register.jsp -->

<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="database.init.InitDatabase" %>
<%@ page import="database.DatabaseConnection" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Customer Registration</title>
</head>
<body>

<%
    String name = request.getParameter("name");
    String address = request.getParameter("address");
    String dateOfBirth = request.getParameter("date_of_birth");
    String driverLicense = request.getParameter("driver_license");
    String cardDetails = request.getParameter("card_details");

    // Check if the form is submitted
    if (request.getMethod().equals("POST")) {
        Connection connection = null;
        try {
            // Obtain a database connection
            connection = DatabaseConnection.getConnection();

            // Call the RegisterCustomer method
            InitDatabase.RegisterCustomer(name, address, dateOfBirth, driverLicense, cardDetails, connection);

            out.println("User Registered Successfully");
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

<form method="post" action="Register.jsp">
    <label for="name">Name:</label>
    <input type="text" name="name" required><br>

    <label for="address">Address:</label>
    <input type="text" name="address" required><br>

    <label for="date_of_birth">Date of Birth:</label>
    <input type="text" name="date_of_birth" required><br>

    <label for="driver_license">Driver's License:</label>
    <input type="text" name="driver_license" required><br>

    <label for="card_details">Card Details:</label>
    <input type="text" name="card_details" required><br>

    <input type="submit" value="Register">
</form>

</body>
</html>
