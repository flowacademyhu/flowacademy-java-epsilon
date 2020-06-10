package hu.flowacademy.epsilon._10_jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcExample {
    public static void main(String[] args) throws Exception {
        // Force loading the driver and registering with JDBC DriverManager
        Class.forName("org.h2.Driver");
        // DriverManager can now resolve jdbc:h2 URL
        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/testLocal")) {
            try (Statement st = conn.createStatement()) {
                st.execute("DROP TABLE IF EXISTS Person");
                st.execute("CREATE TABLE Person(id IDENTITY, firstName VARCHAR(255), lastName VARCHAR(255))");
            }
            try(PreparedStatement insert =
                    conn.prepareStatement("INSERT INTO Person (firstName, lastName) VALUES(?, ?)")){
                var actors = new String[] { "Mark", "Hamill", "Carrie", "Fisher" };
                for(int i = 0; i < actors.length; i += 2) {
                    insert.clearParameters();
                    insert.setString(1, actors[i]);
                    insert.setString(2, actors[i + 1]);
                    insert.execute();
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement("SELECT id, firstName, lastName FROM Person")) {
                printPeople(stmt);
            }

            try (PreparedStatement stmt =
                     conn.prepareStatement("SELECT id, firstName, lastName FROM Person WHERE lastName=?")) {
                stmt.setString(1, "Hamill");
                printPeople(stmt);

                stmt.setString(1, "Foo");
                printPeople(stmt);
            }
        }
    }

    private static void printPeople(PreparedStatement peopleQuery) throws SQLException {
        try (ResultSet people = peopleQuery.executeQuery()) {
            // ResultSet is positioned _before_ first row when it is opened.
            // next() returns false once there are no more records.
            System.out.println();
            System.out.println("id\tfirstName\tlastName");
            while (people.next()) {
                // Columns are 1 based!
                var id = people.getInt(1);
                var firstName = people.getString(2);
                var lastName = people.getString(3);
                System.out.println(String.format("%d\t%s\t%s", id, firstName, lastName));
            }
        }
    }
}
