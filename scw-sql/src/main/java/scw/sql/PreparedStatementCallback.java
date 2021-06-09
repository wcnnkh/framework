package scw.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementCallback {
	void doInPreparedStatement(PreparedStatement ps) throws SQLException;
}
