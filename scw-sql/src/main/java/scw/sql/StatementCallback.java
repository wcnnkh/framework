package scw.sql;

import java.sql.SQLException;
import java.sql.Statement;

@FunctionalInterface
public interface StatementCallback<T extends Statement> {
	void doInStatement(T statement) throws SQLException;
}
