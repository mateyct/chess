package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.fail;

public class JunitUtils {
    public static int getTableSize(String table) {
        String statement = "SELECT COUNT(*) FROM " + table;
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (DataAccessException | SQLException e) {
            fail("Error interacting with the database");
        }
        return -1;
    }
}
