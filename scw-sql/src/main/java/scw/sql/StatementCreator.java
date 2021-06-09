package scw.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public interface StatementCreator<T extends Statement> {
	T create(Connection connection) throws SQLException;
}
