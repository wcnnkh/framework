package scw.sql;

import java.sql.CallableStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface CallableStatementCallback {
	void doInCallableStatement(CallableStatement cs) throws SQLException;
}
