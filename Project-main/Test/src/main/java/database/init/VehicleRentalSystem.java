package database.init;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static database.DatabaseConnection.getConnection;


public class VehicleRentalSystem {

    public static void main(String[] args) {
        // Simulated customer and vehicle IDs for demonstration
        int customerId = 3;
        int vehicleId = 1;
        String rentalStartDate = "2022-01-01 00:00:00";
        String rentalEndDate = "2022-01-10 00:00:00";

        String DriverLicense = "DL2"; // Set to the driver's license if applicable

        try (Connection connection = getConnection()) {
            // Record the vehicle rental
            recordVehicleRental(connection, customerId, vehicleId, rentalStartDate, rentalEndDate, DriverLicense, true);


            System.out.println("Vehicle rental recorded successfully!");

        } catch (Exception e) {

            e.printStackTrace();
        }

        try (Connection connection = getConnection()) {
            // Record the vehicle rental
            recordVehicleRental(connection, customerId - 1, vehicleId + 1, rentalStartDate, rentalEndDate, "DL1", false);


            System.out.println("Vehicle rental recorded successfully!");

        } catch (Exception e) {

            e.printStackTrace();
        }

        try (Connection connection = getConnection()) {
            // Record the vehicle rental
            reportDamageAndRepair(connection, 1, "Damaged");

            System.out.println("Vehicle rental recorded successfully!");

        } catch (Exception e) {

            e.printStackTrace();
        }

        try (Connection connection = getConnection()) {
            // Record the vehicle rental
            reportDamageAndRepair(connection, 2, "Damaged");

            System.out.println("Vehicle rental recorded successfully!");

        } catch (Exception e) {

            e.printStackTrace();
        }

//        try (Connection connection = getConnection()) {
//            returnRentedVehicle(connection, 12, rentalEndDate); //Edw 8a exei allo rentalID oxi hardcoded
//            System.out.println("Vehicle returned successfully!");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

    }

    public static void recordVehicleRental(Connection connection, int customerId, int vehicleId,
                                           String rentalStartDate, String rentalEndDate,
                                           String DriverLicense, boolean insurance) throws SQLException {

        Map<String, String> vehicleDetails = fetchVehicleDetails(connection, vehicleId);
        //1. range_in_km 2.color 3.registration_number 4.model 5.category 6.vehicle_id 7.brand 8.status


        Map<String, String> customerDetails = fetchCustomerDetails(connection, customerId);
        //1.card_details 2.address 3.date_of_birth 4.name 5.customer_id 6.driver_license

        // Check age and driver's license requirements
        boolean ageAndLicenseCheck;
        if (DriverLicense == null) {
            ageAndLicenseCheck = checkAgeAndLicenseRequirements(vehicleDetails.get("category"), customerDetails.get("date_of_birth"), customerDetails.get("driver_license"));
            DriverLicense = customerDetails.get("driver_license");
        }else
            ageAndLicenseCheck = checkAgeAndLicenseRequirements(vehicleDetails.get("category"), customerDetails.get("date_of_birth"), DriverLicense);

        if (ageAndLicenseCheck) {

            // Using a prepared statement to avoid SQL injection
            String insertRentQuery = "INSERT INTO Rent (customer_id, vehicle_id, date_of_rent, date_of_return,rent_duration,insurance, total_cost, driver_license, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Active')";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertRentQuery)) {
                // Calculate rent duration in days
                int rentDuration = calculateRentDuration(rentalStartDate, rentalEndDate);

                // Fetch daily_rental_cost from the respective vehicle type table based on vehicle_id
                double rentCost = fetchDailyRentalCost(connection, vehicleId, insurance);

                // Calculate total cost based on daily rental cost and rental duration
                double totalCost = rentCost * rentDuration;

                // Insert the rental record into the Rent table
                preparedStatement.setInt(1, customerId);
                preparedStatement.setInt(2, vehicleId);
                preparedStatement.setString(3, rentalStartDate);
                preparedStatement.setString(4, rentalEndDate);
                preparedStatement.setInt(5, rentDuration);
                preparedStatement.setBoolean(6, insurance);
                preparedStatement.setDouble(7, totalCost);
                preparedStatement.setString(8, DriverLicense);

                preparedStatement.executeUpdate();

                // Update the status of the rented vehicle in the Vehicle table to 'Rented'
                updateVehicleStatus(connection, vehicleId, "Rented");
            }
        } else {
            System.out.println("Customer does not meet age and/or driver's license requirements for the selected vehicle");
        }
    }

    private static Map<String, String> fetchVehicleDetails(Connection connection, int vehicleId) throws SQLException {
        // Fetch vehicle details including type from the Vehicle table
        String selectVehicleQuery = "SELECT * FROM Vehicle WHERE vehicle_id = ?";
        return getStringStringMap(connection, vehicleId, selectVehicleQuery);
    }

    public static Map<String, String> fetchCustomerDetails(Connection connection, int customerId) throws SQLException {
        // Fetch all customer details from the customer_id
        String selectCustomerQuery = "SELECT * FROM Customer WHERE customer_id = ?";

        return getStringStringMap(connection, customerId, selectCustomerQuery);
    }

    private static Map<String, String> getStringStringMap(Connection connection, int customerId, String selectCustomerQuery) throws SQLException {
        Map<String, String> map = new HashMap<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectCustomerQuery)) {
            preparedStatement.setInt(1, customerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();

                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String columnValue = resultSet.getString(i);
                        //1. range_in_km 2.color 3.registration_number 4.model 5.category 6.vehicle_id 7.brand 8.status

                        // Store column metadata in the map
                        map.put(columnName, columnValue);
                    }
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        System.out.println("Column Name: " + entry.getKey() + ", Column Value: " + entry.getValue());
                    }
                }
                return map;

            }
        }
    }

    private static boolean checkAgeAndLicenseRequirements(String vehicleDetails, String customerAge, String
            driverLicense) {
//        System.out.println("vehicleDetails: " + vehicleDetails);
//        System.out.println("customerAge: " + customerAge);
//        System.out.println("driverLicense: " + driverLicense);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateOfBirth = LocalDate.parse(customerAge, formatter);

        LocalDate currentDate = LocalDate.now();
        Period age = Period.between(dateOfBirth, currentDate);

        if ("Car".equals(vehicleDetails) || "Motorcycle".equals(vehicleDetails)) {
            return age.getYears() >= 18 && driverLicense != null;
        } else if ("Bike".equals(vehicleDetails) || "Scooter".equals(vehicleDetails)) {
            return age.getYears() >= 16;
        } else {
            return false; // Handle other vehicle types as needed
        }
    }

    private static double fetchDailyRentalCost(Connection connection, int vehicleId, boolean includeInsurance) throws
            SQLException {
        // Using a prepared statement to avoid SQL injection
//        String selectDailyRentalCostQuery = "SELECT v.daily_rental_cost";


        String selectDailyRentalCostQuery =
                "SELECT total_cost FROM " +
                        "(SELECT CASE " +
                        "WHEN v.category = 'Car' THEN c.daily_rental_cost " +
                        "WHEN v.category = 'Bike' THEN b.daily_rental_cost " +
                        "WHEN v.category = 'Scooter' THEN s.daily_rental_cost " +
                        "WHEN v.category = 'Motorcycle' THEN m.daily_rental_cost " +
                        " END + " +
                        "(IF(?, " +
                        "CASE " +
                        "WHEN v.category = 'Car' THEN c.insurance_cost " +
                        "WHEN v.category = 'Bike' THEN b.insurance_cost " +
                        "WHEN v.category = 'Scooter' THEN s.insurance_cost " +
                        "WHEN v.category = 'Motorcycle' THEN m.insurance_cost " +
                        " END" +
                        ", 0)) AS total_cost " +
                        "FROM Vehicle v " +
                        "LEFT JOIN Car c ON v.vehicle_id = c.vehicle_id " +
                        "LEFT JOIN Bike b ON v.vehicle_id = b.vehicle_id " +
                        "LEFT JOIN Scooter s ON v.vehicle_id = s.vehicle_id " +
                        "LEFT JOIN Motorcycle m ON v.vehicle_id = m.vehicle_id " +
                        "WHERE v.vehicle_id = ?) AS result";


        try (PreparedStatement preparedStatement = connection.prepareStatement(selectDailyRentalCostQuery)) {
            preparedStatement.setBoolean(1, includeInsurance);
            preparedStatement.setInt(2, vehicleId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("total_cost");
                } else {
                    throw new SQLException("Vehicle not found with ID: " + vehicleId);
                }
            }
        }
    }

    private static int calculateRentDuration(String rentalStartDate, String rentalEndDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date startDate = dateFormat.parse(rentalStartDate);
            java.util.Date endDate = dateFormat.parse(rentalEndDate);

            // Calculate duration in days
            long durationInMillis = endDate.getTime() - startDate.getTime();
            return (int) (durationInMillis / (24 * 60 * 60 * 1000));
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Return 0 in case of error
        }
    }

    public static void returnRentedVehicle(Connection connection, int rentalId, String returnTime) throws SQLException {
        // Using a prepared statement to avoid SQL injection
        String updateRentQuery = "UPDATE Rent SET total_cost =?,date_of_return = ?,  status = ? WHERE rent_id = ?";
        String selectRentQuery = "SELECT rent_duration,vehicle_id, date_of_rent,date_of_return, total_cost FROM Rent WHERE rent_id = ?";

        try (PreparedStatement selectStatement = connection.prepareStatement(selectRentQuery);
             PreparedStatement updateStatement = connection.prepareStatement(updateRentQuery)) {

            // Fetch rent details
            selectStatement.setInt(1, rentalId);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    int rentDuration = resultSet.getInt("rent_duration");
                    String rentalStartDate = resultSet.getString("date_of_rent");
//                    String rentalEndDate = returnTime;
                    double totalCost = resultSet.getDouble("total_cost");

                    // Calculate additional charges for late return
                    double additionalCharges = calculateAdditionalCharges(rentalStartDate, returnTime, rentDuration);

                    // Update return time, status, and total cost in Rent table
                    updateStatement.setDouble(1, totalCost + additionalCharges);
                    updateStatement.setString(2, returnTime);
                    updateStatement.setString(3, "Completed");
                    updateStatement.setInt(4, rentalId);

                    updateStatement.executeUpdate();
                    updateVehicleStatus(connection, resultSet.getInt("vehicle_id"), "Available");

                    System.out.println("Vehicle returned successfully!");
                    System.out.println("Additional charges: $" + additionalCharges);

                } else {
                    System.out.println("Rental not found with ID: " + rentalId);
                }
            }
        }
    }

    private static double calculateAdditionalCharges(String rentalStartDate, String returnTime, int rentDuration) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date startDate = dateFormat.parse(rentalStartDate);
            java.util.Date endTime = dateFormat.parse(returnTime);

            // Calculate the duration in hours
            long durationInMillis = endTime.getTime() - startDate.getTime();
            int durationInHours = (int) (durationInMillis / (60 * 60 * 1000));

            // Calculate additional charges for each hour beyond the rental duration
            int lateHours = Math.max(0, durationInHours - rentDuration);
            double hourlyLateFee = 10.0; // Replace with your actual late fee per hour

            return lateHours * hourlyLateFee;

        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Return 0 in case of error
        }
    }


    private static void updateVehicleStatus(Connection connection, int vehicleId, String newStatus) throws
            SQLException {
        // Using a prepared statement to avoid SQL injection
        String updateVehicleStatusQuery = "UPDATE Vehicle SET status = ? WHERE vehicle_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateVehicleStatusQuery)) {
            preparedStatement.setString(1, newStatus);
            preparedStatement.setInt(2, vehicleId);

            preparedStatement.executeUpdate();
        }
    }

    public static void reportDamageAndRepair(Connection connection, int rentId, String flag) {

        // Check if the rent exists and retrieve the vehicle details
        String checkRentQuery = "SELECT Rent.vehicle_id, Rent.insurance,Rent.total_cost ,Vehicle.category FROM Rent JOIN Vehicle ON Rent.vehicle_id = Vehicle.vehicle_id WHERE rent_id = ?";

        try (PreparedStatement checkRentStatement = connection.prepareStatement(checkRentQuery)) {
            checkRentStatement.setInt(1, rentId);

            // Execute the query and retrieve the result
            try (ResultSet resultSet = checkRentStatement.executeQuery()) {
                // Check if the result set is not empty
                if (resultSet.next()) {
                    // Extract vehicle_id and category from the result
                    int damagedVehicleId = resultSet.getInt("vehicle_id");
                    String category = resultSet.getString("category");
                    boolean insurance = resultSet.getBoolean("insurance");
                    double totalCost = resultSet.getDouble("total_cost");

                    System.out.println("insurance: " + insurance);
                    System.out.println("flag: " + flag);
                    System.out.println("rentId: " + rentId);

                    if (flag.equals("Maintenance") || (flag.equals("Damaged") && insurance)) {
                        System.out.println("Insurance Brother.");
                        int newVehicleId = getFirstAvailableVehicleWithCategory(category, connection);

                        // Assign a new vehicle of the same category to the rent
                        if (newVehicleId != -1) {
                            String assignNewVehicleQuery = "UPDATE Rent SET vehicle_id = ? WHERE rent_id = ?";
                            try (PreparedStatement assignNewVehicleStatement = connection.prepareStatement(assignNewVehicleQuery)) {
                                assignNewVehicleStatement.setInt(1, newVehicleId);
                                assignNewVehicleStatement.setInt(2, rentId);
                                updateVehicleStatus(connection, newVehicleId, "Rented");

                                // Execute the query to assign the new vehicle
                                int rowsAffected = assignNewVehicleStatement.executeUpdate();
                                if (rowsAffected > 0) {
                                    // Successfully assigned new vehicle, now register for repair
                                    registerVehicleForRepair(damagedVehicleId, category, flag, connection);
                                } else {
                                    // Handle the case where no rows were affected (e.g., invalid rentId)
                                    System.out.println("No rows affected. Invalid rentId?");
                                }
                            }
                        } else {
                            System.out.println("No available vehicle with the same category found.");
                        }
                    } else {
                        System.out.println("No Insurance Brother.");
                        String updateTotalCostQuery = "UPDATE Rent SET total_cost = ?, status = 'Completed' WHERE rent_id = ?";
                        updateVehicleStatus(connection, damagedVehicleId, "Damaged");
                        try (PreparedStatement updateTotalCostStatement = connection.prepareStatement(updateTotalCostQuery)) {
                            updateTotalCostStatement.setDouble(1, totalCost * 3);
                            updateTotalCostStatement.setInt(2, rentId);

                            updateTotalCostStatement.executeUpdate();

                            registerVehicleForRepair(damagedVehicleId, category, flag, connection);
                        }
                    }
                } else {
                    // Handle the case where the rentId does not exist or the result set is empty
                    System.out.println("Rent not found or empty result set.");
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Handle the exception appropriately in a real-world scenario
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static int getFirstAvailableVehicleWithCategory(String category, Connection connection) throws SQLException {
        String query = "SELECT vehicle_id FROM Vehicle WHERE status = 'Available' AND category = ? ORDER BY vehicle_id LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, category);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("vehicle_id");
                }
            }
        }

        // Return a default value or handle the case when no available vehicle with the same category is found
        return -1;
    }

    // Method to register a vehicle for repair
    private static void registerVehicleForRepair(int vehicleId, String category, String flag, Connection connection) throws
            SQLException {
        System.out.println(vehicleId + " " + category + " " + flag);
        // Assuming you have a method to insert repair details into the Repair table
        String registerRepairQuery;

        if (flag.equals("Maintenance")) {
            registerRepairQuery = "INSERT INTO Repair (vehicle_id, date_of_enter,date_of_exit, cost, status) VALUES (?, NOW(),DATE_ADD(NOW(), INTERVAL 1 DAY), ?, 'Maintenance')";
        } else {
            registerRepairQuery = "INSERT INTO Repair (vehicle_id, date_of_enter,date_of_exit, cost, status) VALUES (?, NOW(),DATE_ADD(NOW(), INTERVAL 3 DAY), ?, 'Damaged')";
        }
        try (PreparedStatement registerRepairStatement = connection.prepareStatement(registerRepairQuery)) {
            registerRepairStatement.setInt(1, vehicleId);
            // You may set the cost based on your logic
            registerRepairStatement.setInt(2, 10);

            updateVehicleStatus(connection, vehicleId, "Damaged");

            // Execute the query to register the vehicle for repair
            // (Handle the result and implement error checking based on your needs)
            registerRepairStatement.executeUpdate();
        }
    }
}

