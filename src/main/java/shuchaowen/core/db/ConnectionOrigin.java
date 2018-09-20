package shuchaowen.core.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionOrigin extends AutoCloseable{
	public Connection getConnection() throws SQLException;
}
