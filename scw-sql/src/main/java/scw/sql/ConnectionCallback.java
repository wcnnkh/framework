package scw.sql;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionCallback {
	void doInConnection(Connection connection) throws SQLException;
}
