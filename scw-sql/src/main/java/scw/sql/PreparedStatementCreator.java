package scw.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementCreator<T extends PreparedStatement> {
	T create(Connection connection, Sql sql) throws SQLException;
}
