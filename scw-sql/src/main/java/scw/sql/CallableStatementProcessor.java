package scw.sql;

import java.sql.CallableStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface CallableStatementProcessor<T> {
	T processCallableStatement(CallableStatement cs) throws SQLException;
}
