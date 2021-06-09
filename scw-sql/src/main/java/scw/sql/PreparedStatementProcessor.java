package scw.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementProcessor<T> {
	T processPreparedStatement(PreparedStatement ps) throws SQLException;
}
