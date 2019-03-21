package scw.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public final class SqlUtils {
	private SqlUtils(){};
	
	public static String getSqlId(Sql sql) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(sql.getSql());
		sb.append("]");
		sb.append(" - ");
		sb.append(sql.getParams() == null ? "[]" : Arrays.toString(sql.getParams()));
		return sb.toString();
	}

	public static PreparedStatement createPreparedStatement(Connection connection, Sql sql) throws SQLException {
		PreparedStatement statement;
		if (sql.isStoredProcedure()) {
			statement = connection.prepareCall(sql.getSql());
		} else {
			statement = connection.prepareStatement(sql.getSql());
		}

		try {
			setSqlParams(statement, sql.getParams());
		} catch (SQLException e) {
			statement.close();
			throw e;
		}
		return statement;
	}

	public static void setSqlParams(PreparedStatement preparedStatement, Object[] args) throws SQLException {
		if (args != null && args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);
			}
		}
	}

	public static PreparedStatement createPreparedStatement(Connection connection, Sql sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		PreparedStatement preparedStatement;
		if (sql.isStoredProcedure()) {
			preparedStatement = connection.prepareCall(sql.getSql(), resultSetType, resultSetConcurrency);
		} else {
			preparedStatement = connection.prepareStatement(sql.getSql(), resultSetType, resultSetConcurrency);
		}

		try {
			setSqlParams(preparedStatement, sql.getParams());
		} catch (SQLException e) {
			preparedStatement.close();
			throw e;
		}
		return preparedStatement;
	}

	public static PreparedStatement createPreparedStatement(Connection connection, Sql sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		PreparedStatement preparedStatement;
		if (sql.isStoredProcedure()) {
			preparedStatement = connection.prepareCall(sql.getSql(), resultSetType, resultSetConcurrency,
					resultSetHoldability);
		} else {
			preparedStatement = connection.prepareStatement(sql.getSql(), resultSetType, resultSetConcurrency,
					resultSetHoldability);
		}

		try {
			setSqlParams(preparedStatement, sql.getParams());
		} catch (SQLException e) {
			preparedStatement.close();
			throw e;
		}
		return preparedStatement;
	}

}
