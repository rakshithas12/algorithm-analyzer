package com.algorithm.dao;

import com.algorithm.model.AlgorithmInfo;
import com.algorithm.algorithm.AlgorithmRegistry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

public class AlgorithmDAO {
    private static boolean initialized;

    private final String jdbcUrl;
    private final String jdbcUser;
    private final String jdbcPassword;

    public AlgorithmDAO(ServletContext context) {
        this.jdbcUrl = getRequiredParam(context, "jdbcUrl");
        this.jdbcUser = getRequiredParam(context, "jdbcUser");
        this.jdbcPassword = getOptionalParam(context, "jdbcPassword");
    }

    public AlgorithmInfo findByKey(String algorithmKey) throws SQLException {
        initializeDatabase();

        String sql = "SELECT algorithm_key, name, category, description, complexity "
                + "FROM algorithms WHERE algorithm_key = ?";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, algorithmKey);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return new AlgorithmInfo(
                        resultSet.getString("algorithm_key"),
                        resultSet.getString("name"),
                        resultSet.getString("category"),
                        resultSet.getString("description"),
                        resultSet.getString("complexity"),
                        findSteps(connection, algorithmKey));
            }
        }
    }

    private List<String> findSteps(Connection connection, String algorithmKey) throws SQLException {
        String sql = "SELECT step_text FROM algorithm_steps WHERE algorithm_key = ? ORDER BY step_order";
        List<String> steps = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, algorithmKey);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    steps.add(resultSet.getString("step_text"));
                }
            }
        }

        return steps;
    }

    private synchronized void initializeDatabase() throws SQLException {
        if (initialized) {
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException("MySQL JDBC driver not found. Run Maven > Update Project in Eclipse.", ex);
        }

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS algorithms ("
                    + "algorithm_key VARCHAR(40) PRIMARY KEY, "
                    + "name VARCHAR(80) NOT NULL, "
                    + "category VARCHAR(30) NOT NULL, "
                    + "description VARCHAR(500) NOT NULL, "
                    + "complexity VARCHAR(30) NOT NULL"
                    + ")");

            statement.execute("CREATE TABLE IF NOT EXISTS algorithm_steps ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "algorithm_key VARCHAR(40) NOT NULL, "
                    + "step_order INT NOT NULL, "
                    + "step_text VARCHAR(500) NOT NULL, "
                    + "CONSTRAINT fk_algorithm_steps_algorithm "
                    + "FOREIGN KEY (algorithm_key) REFERENCES algorithms(algorithm_key) "
                    + "ON DELETE CASCADE"
                    + ")");

            seedAlgorithms(connection);
            initialized = true;
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
    }

    private void seedAlgorithms(Connection connection) throws SQLException {
        upsertAlgorithm(connection, "bubbleSort", "Bubble Sort", "Sort",
                "Repeatedly compares adjacent elements and swaps them when they are in the wrong order.", complexityFor("bubbleSort"));
        replaceSteps(connection, "bubbleSort", new String[] {
                "Start from the first element of the array.",
                "Compare the current element with the next element.",
                "Swap both elements if the current element is greater than the next element.",
                "Continue until the largest unsorted value moves to the end.",
                "Repeat the passes until no unsorted elements remain."
        });

        upsertAlgorithm(connection, "linearSearch", "Linear Search", "Search",
                "Checks every element one by one until the target value is found or the array ends.", complexityFor("linearSearch"));
        replaceSteps(connection, "linearSearch", new String[] {
                "Read the target element to search.",
                "Start checking from index 0.",
                "Compare each array element with the target element.",
                "Return the current index when a match is found.",
                "Return not found if the loop reaches the end without a match."
        });

        upsertAlgorithm(connection, "selectionSort", "Selection Sort", "Sort",
                "Finds the smallest value from the unsorted part and places it at the current position.", complexityFor("selectionSort"));
        replaceSteps(connection, "selectionSort", new String[] {
                "Treat the first unsorted position as the minimum position.",
                "Scan the remaining unsorted elements to find the smallest value.",
                "Swap the smallest value with the first unsorted position.",
                "Move the sorted boundary one position forward.",
                "Repeat until the full array is sorted."
        });

        upsertAlgorithm(connection, "insertionSort", "Insertion Sort", "Sort",
                "Builds a sorted portion of the array by inserting each new value into its correct place.", complexityFor("insertionSort"));
        replaceSteps(connection, "insertionSort", new String[] {
                "Assume the first element is already sorted.",
                "Pick the next element as the key value.",
                "Move larger sorted elements one position to the right.",
                "Insert the key into the open correct position.",
                "Repeat until all elements have been inserted."
        });

        upsertAlgorithm(connection, "binarySearch", "Binary Search", "Search",
                "Searches a sorted array by repeatedly dividing the search range in half.", complexityFor("binarySearch"));
        replaceSteps(connection, "binarySearch", new String[] {
                "Sort the input array before searching.",
                "Set the left pointer to the first index and the right pointer to the last index.",
                "Find the middle index of the current range.",
                "Compare the middle value with the target element.",
                "Search the left or right half until the target is found or the range becomes empty."
        });

        upsertAlgorithm(connection, "addition", "Addition", "Math",
                "Adds two input numbers and displays the sum.", complexityFor("addition"));
        replaceSteps(connection, "addition", new String[] {
                "Add a.",
                "Add b.",
                "sum = a + b."
        });
    }

    private String complexityFor(String algorithmKey) {
        return AlgorithmRegistry.getDefault().get(algorithmKey).getComplexity().getWorstCaseTime();
    }

    private void upsertAlgorithm(Connection connection, String key, String name, String category,
            String description, String complexity) throws SQLException {
        String sql = "INSERT INTO algorithms (algorithm_key, name, category, description, complexity) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE name = VALUES(name), category = VALUES(category), "
                + "description = VALUES(description), complexity = VALUES(complexity)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, key);
            statement.setString(2, name);
            statement.setString(3, category);
            statement.setString(4, description);
            statement.setString(5, complexity);
            statement.executeUpdate();
        }
    }

    private void replaceSteps(Connection connection, String algorithmKey, String[] steps) throws SQLException {
        try (PreparedStatement deleteStatement = connection.prepareStatement(
                "DELETE FROM algorithm_steps WHERE algorithm_key = ?")) {
            deleteStatement.setString(1, algorithmKey);
            deleteStatement.executeUpdate();
        }

        try (PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO algorithm_steps (algorithm_key, step_order, step_text) VALUES (?, ?, ?)")) {
            for (int i = 0; i < steps.length; i++) {
                insertStatement.setString(1, algorithmKey);
                insertStatement.setInt(2, i + 1);
                insertStatement.setString(3, steps[i]);
                insertStatement.addBatch();
            }
            insertStatement.executeBatch();
        }
    }

    private String getRequiredParam(ServletContext context, String name) {
        String value = context.getInitParameter(name);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing web.xml context-param: " + name);
        }
        return value.trim();
    }

    private String getOptionalParam(ServletContext context, String name) {
        String value = context.getInitParameter(name);
        return value == null ? "" : value;
    }
}
