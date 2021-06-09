package scw.sql;

import java.sql.SQLException;
import java.sql.Statement;

@FunctionalInterface
public interface StatementProcessor<P extends Statement, T> {
	T processStatement(P statement) throws SQLException;
}
