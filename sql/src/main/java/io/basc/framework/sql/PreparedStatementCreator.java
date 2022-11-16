package io.basc.framework.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementCreator {
	PreparedStatement createPreparedStatement(Connection connection, Sql sql) throws SQLException;
}
