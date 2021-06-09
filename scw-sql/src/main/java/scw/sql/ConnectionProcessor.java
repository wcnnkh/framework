package scw.sql;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionProcessor<T> {
	T processConnection(Connection connection) throws SQLException;
}
