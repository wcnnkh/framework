package scw.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionSource{
	Connection getConnection() throws SQLException;
}
