package database.init;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static database.DatabaseConnection.getConnection;
import static database.DatabaseConnection.getInitialConnection;

public class InitDatabase {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");
        InitDatabase init = new InitDatabase();
        init.createDatabase();
        init.initDatabase();

    }

    private void createDatabase() throws Exception {
        Connection connection = getInitialConnection();
        Statement statement = connection.createStatement();
        statement.execute("CREATE DATABASE IF NOT EXISTS EVOL");
        statement.close();
    }

    private void initDatabase() throws Exception {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        createTables(connection);

        insertSampleCustomers(connection);
        insertSampleVehicles(connection);
        statement.close();

    }

    public static void createTables(Connection connection) {
        try {
            Statement statement = connection.createStatement();


            // Creating Vehicle table
            String createVehicleTable = "CREATE TABLE IF NOT EXISTS Vehicle (" +
                    "vehicle_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "brand VARCHAR(255) NOT NULL," +
                    "model VARCHAR(255) NOT NULL," +
                    "color VARCHAR(255) NOT NULL," +
                    "range_in_km INT NOT NULL," +
                    "registration_number VARCHAR(255) UNIQUE," +
                    "category VARCHAR(50) NOT NULL," +
                    "status ENUM('Available', 'Rented', 'Damaged','Maintenance')" +
                    ")";
            statement.executeUpdate(createVehicleTable);

            // Creating Car table
            String createCarTable = "CREATE TABLE IF NOT EXISTS Car (" +
                    "vehicle_id INT PRIMARY KEY," +
                    "type VARCHAR(255) NOT NULL," +
                    "passenger_number INT NOT NULL," +
                    "daily_rental_cost DECIMAL(10, 2)," +
                    "insurance_cost DECIMAL(10, 2)," +
                    "FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id)" +
                    ")";
            statement.executeUpdate(createCarTable);


            // Creating Motorcycle table
            String createMotorcycleTable = "CREATE TABLE IF NOT EXISTS Motorcycle (" +
                    "vehicle_id INT PRIMARY KEY," +
                    "daily_rental_cost DECIMAL(10, 2)," +
                    "insurance_cost DECIMAL(10, 2)," +
                    "FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id)" +
                    ")";
            statement.executeUpdate(createMotorcycleTable);

            // Creating Bike table
            String createBikeTable = "CREATE TABLE IF NOT EXISTS Bike (" +
                    "vehicle_id INT PRIMARY KEY," +
                    // Add additional attributes for bikes here
                    "daily_rental_cost DECIMAL(10, 2)," +
                    "insurance_cost DECIMAL(10, 2)," +
                    "FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id)" +
                    ")";
            statement.executeUpdate(createBikeTable);

            // Creating Scooter table
            String createScooterTable = "CREATE TABLE IF NOT EXISTS Scooter (" +
                    "vehicle_id INT PRIMARY KEY," +
                    // Add additional attributes for scooters here
                    "daily_rental_cost DECIMAL(10, 2)," +
                    "insurance_cost DECIMAL(10, 2)," +
                    "FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id)" +
                    ")";
            statement.executeUpdate(createScooterTable);

            String createCustomerTable = "CREATE TABLE IF NOT EXISTS Customer (" +
                    "customer_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(100) NOT NULL," +
                    "address VARCHAR(255) NOT NULL," +
                    "date_of_birth DATE NOT NULL," +
                    "driver_license VARCHAR(255) UNIQUE," +
                    "card_details VARCHAR(100)" +
                    ")";
            statement.executeUpdate(createCustomerTable);

            String createRentTable = "CREATE TABLE IF NOT EXISTS Rent (" +
                    "rent_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "customer_id INT NOT NULL," +
                    "vehicle_id INT NOT NULL," +
                    "date_of_rent DATETIME NOT NULL," +
                    "date_of_return DATETIME NOT NULL," +
                    "rent_duration INT NOT NULL," +
                    "total_cost DECIMAL(10,2) NOT NULL," +
                    "insurance BOOLEAN NOT NULL," +
                    "driver_license VARCHAR(255)," +
                    "status ENUM('Active', 'Completed', 'Cancelled')," +
                    "FOREIGN KEY(customer_id) REFERENCES Customer(customer_id)," +
                    "FOREIGN KEY(vehicle_id) REFERENCES Vehicle(vehicle_id), " +
                    "UNIQUE(status,vehicle_id)," +
                    "UNIQUE(status, driver_license)" +
                    ")";
            statement.executeUpdate(createRentTable);

            String createRepairTable = "CREATE TABLE IF NOT EXISTS Repair (" +
                    "repair_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "vehicle_id INT NOT NULL," +
                    "date_of_enter DATETIME NOT NULL," +
                    "date_of_exit DATETIME NOT NULL," +
                    "cost DECIMAL(10,2) NOT NULL," +
                    "status ENUM( 'Damaged', 'Being Repaired', 'Maintenance')," +
                    "FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id)" +
                    ")";
            statement.executeUpdate(createRepairTable);

            System.out.println("Tables created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void RegisterRepair(int vehicle_id, String date_of_enter, String date_of_exit, int cost, String status, Connection connection) {
        String sql = "INSERT INTO Repair (repair_id, vehicle_id, date_of_enter, date_of_exit, cost, status) " +
                "VALUES (NULL, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // Set values for the placeholders
            preparedStatement.setInt(1, vehicle_id);
            preparedStatement.setString(2, date_of_enter);
            preparedStatement.setString(3, date_of_exit);
            preparedStatement.setInt(4, cost);
            preparedStatement.setString(5, status);

            // Execute the update
            preparedStatement.executeUpdate();
            System.out.println("Repair Registered Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void RegisterCustomer(String name, String address, String date_of_birth, String driver_license, String card_details, Connection connection) {
        String sql = "INSERT INTO Customer (customer_id, name, address, date_of_birth, driver_license, card_details) " +
                "VALUES (NULL, ?, ?, ?, ?, ?)";


        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // Set values for the placeholders
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, date_of_birth);
            preparedStatement.setString(4, driver_license);
            preparedStatement.setString(5, card_details);

            // Execute the update
            preparedStatement.executeUpdate();
            System.out.println("User Registered Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void RegisterCar(String brand, String model, String color, int range_in_km, String registration_number, String type, int passenger_number, int daily_rental_cost, int insurance_cost, Connection connection) {
        String sql = "INSERT INTO Vehicle (vehicle_id, brand, model, color, range_in_km, registration_number,category,status) " +
                "VALUES (NULL, ?, ?, ?, ?, ?,?,?)";
        String sql2 = "INSERT INTO Car (vehicle_id, type, passenger_number,daily_rental_cost, insurance_cost) " +
                "VALUES (LAST_INSERT_ID(), ?, ?,?,?)";

        try {
            PreparedStatement VehicleStatement = connection.prepareStatement(sql);
            PreparedStatement CarStatement = connection.prepareStatement(sql2);
            // Set values for the placeholders
            VehicleStatement.setString(1, brand);
            VehicleStatement.setString(2, model);
            VehicleStatement.setString(3, color);
            VehicleStatement.setInt(4, range_in_km);
            VehicleStatement.setString(5, registration_number);
            VehicleStatement.setString(6, "Car");
            VehicleStatement.setString(7, "Available");

            CarStatement.setString(1, type);
            CarStatement.setInt(2, passenger_number);
            CarStatement.setInt(3, daily_rental_cost);
            CarStatement.setInt(4, insurance_cost);

            // Execute the update
            VehicleStatement.executeUpdate();
            CarStatement.executeUpdate();
            System.out.println("Car Registered Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void RegisterMotorcycle(String brand, String model, String color, int range_in_km, String registration_number, int daily_rental_cost, int insurance_cost, Connection connection) {
        String sql = "INSERT INTO Vehicle (vehicle_id, brand, model, color, range_in_km, registration_number,category,status) " +
                "VALUES (NULL, ?, ?, ?, ?, ?,?,?)";
        String sql2 = "INSERT INTO Motorcycle (vehicle_id,daily_rental_cost, insurance_cost) " +
                "VALUES (LAST_INSERT_ID(), ?, ?)";

        try {
            PreparedStatement VehicleStatement = connection.prepareStatement(sql);
            PreparedStatement MotorcycleStatement = connection.prepareStatement(sql2);
            // Set values for the placeholders
            VehicleStatement.setString(1, brand);
            VehicleStatement.setString(2, model);
            VehicleStatement.setString(3, color);
            VehicleStatement.setInt(4, range_in_km);
            VehicleStatement.setString(5, registration_number);
            VehicleStatement.setString(6, "Motorcycle");
            VehicleStatement.setString(7, "Available");


            MotorcycleStatement.setInt(1, daily_rental_cost);
            MotorcycleStatement.setInt(2, insurance_cost);


            // Execute the update
            VehicleStatement.executeUpdate();
            MotorcycleStatement.executeUpdate();
            System.out.println("Motorcycle Registered Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void RegisterBike(String brand, String model, String color, int range_in_km, String registration_number, int daily_rental_cost, int insurance_cost, Connection connection) {
        String sql = "INSERT INTO Vehicle (vehicle_id, brand, model, color, range_in_km, registration_number,category,status) " +
                "VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)";
        String sql2 = "INSERT INTO Bike (vehicle_id,daily_rental_cost, insurance_cost) " +
                "VALUES (LAST_INSERT_ID(), ?, ?)";

        try {
            PreparedStatement VehicleStatement = connection.prepareStatement(sql);
            PreparedStatement BikeStatement = connection.prepareStatement(sql2);
            // Set values for the placeholders
            VehicleStatement.setString(1, brand);
            VehicleStatement.setString(2, model);
            VehicleStatement.setString(3, color);
            VehicleStatement.setInt(4, range_in_km);
            VehicleStatement.setString(5, registration_number);
            VehicleStatement.setString(6, "Bike");
            VehicleStatement.setString(7, "Available");


            BikeStatement.setInt(1, daily_rental_cost);
            BikeStatement.setInt(2, insurance_cost);

            // Execute the update
            VehicleStatement.executeUpdate();
            BikeStatement.executeUpdate();
            System.out.println("Bike Registered Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void RegisterScooter(String brand, String model, String color, int range_in_km, String registration_number, int daily_rental_cost, int insurance_cost, Connection connection) {
        String sql = "INSERT INTO Vehicle (vehicle_id, brand, model, color, range_in_km, registration_number,category,status) " +
                "VALUES (NULL, ?, ?, ?, ?, ?,?,?)";
        String sql2 = "INSERT INTO Scooter (vehicle_id,daily_rental_cost, insurance_cost) " +
                "VALUES (LAST_INSERT_ID(), ?, ?)";

        try {
            PreparedStatement VehicleStatement = connection.prepareStatement(sql);
            PreparedStatement ScooterStatement = connection.prepareStatement(sql2);
            // Set values for the placeholders
            VehicleStatement.setString(1, brand);
            VehicleStatement.setString(2, model);
            VehicleStatement.setString(3, color);
            VehicleStatement.setInt(4, range_in_km);
            VehicleStatement.setString(5, registration_number);
            VehicleStatement.setString(6, "Scooter");
            VehicleStatement.setString(7, "Available");


            ScooterStatement.setInt(1, daily_rental_cost);
            ScooterStatement.setInt(2, insurance_cost);

            // Execute the update
            VehicleStatement.executeUpdate();
            ScooterStatement.executeUpdate();
            System.out.println("Scooter Registered Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertSampleCustomers(Connection connection) {
        RegisterCustomer("Orestis", "Ntilinta", "2000-05-15", "DL1", "1234-5678-9012-3456", connection);
        RegisterCustomer("John Doe", "123 Main St", "1990-05-15", "DL2", "1234-5678-9012-3456", connection);
        RegisterCustomer("Alice Smith", "456 Elm St", "1985-09-20", null, "9876-5432-1098-7654", connection);

    }

    public static void insertSampleVehicles(Connection connection) {

        RegisterCar("Toyota", "Prius", "Blue", 100, "ABC1", "Sedan", 5, 50, 10, connection);
        RegisterBike("Shi", "Motren", "Red", 100, "ABC2", 1, 50, connection);
        RegisterMotorcycle("Honda", "CBR", "Red", 100, "ABC3", 1, 50, connection);
        RegisterScooter("Kymco", "Agility", "Red", 100, "ABC4", 1, 50, connection);
        RegisterCar("BMW", "Prius", "Blue", 100, "ABC5", "Sedan", 5, 50, 10, connection);

        System.out.println("Sample vehicles inserted successfully!");
    }


//    private static Map<String, String> getStringAvailableRented(Connection connection, String query) throws SQLException {
//        return getStringStringMap(connection, query);
//    }
//
//    private static Map<String, String> getStringMap(Connection connection, String query) throws SQLException {
//        return getStringStringMap(connection, query);
//    }

    private static Map<String, String> getStringStringMap(Connection connection, String query) throws SQLException {
        Map<String, String> map = new HashMap<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();

                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String columnValue = resultSet.getString(i);
                        map.put(columnName, columnValue);
                    }

                    System.out.print(map.get("category") + " ");
                    System.out.print(map.get("brand") + " ");
                    System.out.print(map.get("model") + " ");
                    System.out.print(map.get("color") + " ");
                    System.out.print(map.get("range_in_km") + " ");
                    System.out.print(map.get("registration_number"));


                    System.out.println();
                    System.out.println();
                    System.out.println();
                    System.out.println();
                }
                return map;

            }
        }
    }

    public static Map<String, String> searchAvailableVehicles() throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String searchQuery = "SELECT *\n" +
                "FROM vehicle\n" +
                "WHERE status = 'available'\n" +
                "ORDER BY\n" +
                "  CASE category\n" +
                "    WHEN 'CAR' THEN 1\n" +
                "    WHEN 'MOTORCYCLE' THEN 2\n" +
                "    WHEN 'BIKE' THEN 3\n" +
                "    -- Add more categories as needed\n" +
                "    ELSE 4 -- Default order for unknown categories\n" +
                "  END;\n";

        return getStringStringMap(connection, searchQuery);
    }

    public static Map<String, String> availableRentedVehicles() throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String query = "-- All available vehicles\n" +
                "SELECT \n" +
                "FROM vehicle\n" +
                "WHERE status = 'available'\n" +
                "\n" +
                "UNION ALL\n" +
                "\n" +
                "-- All rented vehicles\n" +
                "SELECT\n" +
                "FROM vehicle\n" +
                "WHERE status = 'rented';\n";
        return getStringStringMap(connection, query);
    }


}
