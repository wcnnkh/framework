package scw.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public final class SqlUtils {
	private SqlUtils() {
	};

	public static String getSqlId(Sql sql) {
		Object[] params = sql.getParams();
		if (params == null || params.length == 0) {
			return sql.getSql();
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(sql.getSql());
			sb.append("]");
			sb.append(" - ");
			sb.append(Arrays.toString(params));
			return sb.toString();
		}
	}

	public static PreparedStatement createPreparedStatement(
			Connection connection, Sql sql) throws SQLException {
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

	public static void setSqlParams(PreparedStatement preparedStatement,
			Object[] args) throws SQLException {
		if (args != null && args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);
			}
		}
	}

	public static PreparedStatement createPreparedStatement(
			Connection connection, Sql sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		PreparedStatement preparedStatement;
		if (sql.isStoredProcedure()) {
			preparedStatement = connection.prepareCall(sql.getSql(),
					resultSetType, resultSetConcurrency);
		} else {
			preparedStatement = connection.prepareStatement(sql.getSql(),
					resultSetType, resultSetConcurrency);
		}

		try {
			setSqlParams(preparedStatement, sql.getParams());
		} catch (SQLException e) {
			preparedStatement.close();
			throw e;
		}
		return preparedStatement;
	}

	public static PreparedStatement createPreparedStatement(
			Connection connection, Sql sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		PreparedStatement preparedStatement;
		if (sql.isStoredProcedure()) {
			preparedStatement = connection.prepareCall(sql.getSql(),
					resultSetType, resultSetConcurrency, resultSetHoldability);
		} else {
			preparedStatement = connection.prepareStatement(sql.getSql(),
					resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		try {
			setSqlParams(preparedStatement, sql.getParams());
		} catch (SQLException e) {
			preparedStatement.close();
			throw e;
		}
		return preparedStatement;
	}

	public static void query(Connection connection, Sql sql,
			ResultSetCallback resultSetCallback) throws SQLException {

		PreparedStatement statement = null;
		try {
			statement = createPreparedStatement(connection, sql);
			query(statement, resultSetCallback);
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public static void query(PreparedStatement statement,
			ResultSetCallback resultSetCallback) throws SQLException {
		ResultSet resultSet = null;
		try {
			resultSet = statement.executeQuery();
			resultSetCallback.process(resultSet);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}

	public static <T> T query(Connection connection, Sql sql,
			ResultSetMapper<T> resultSetMapper) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = SqlUtils.createPreparedStatement(connection, sql);
			return query(statement, resultSetMapper);
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public static <T> T query(PreparedStatement statement,
			ResultSetMapper<T> resultSetMapper) throws SQLException {
		ResultSet resultSet = null;
		try {
			resultSet = statement.executeQuery();
			return resultSetMapper.mapper(resultSet);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}

	public static void execute(Connection connection, Sql sql)
			throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = createPreparedStatement(connection, sql);
			statement.execute();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public static int update(Connection connection, Sql sql)
			throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = createPreparedStatement(connection, sql);
			return statement.executeUpdate();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}
}
