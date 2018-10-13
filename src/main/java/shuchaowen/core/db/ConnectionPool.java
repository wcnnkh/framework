package shuchaowen.core.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionPool extends AutoCloseable{
	Connection getConnection() throws SQLException;
}
