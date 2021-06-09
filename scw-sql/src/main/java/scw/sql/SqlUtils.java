package scw.sql;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;

import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;

public final class SqlUtils {

	public static String toString(String sql, Object... args) {
		if (args == null || args.length == 0) {
			return sql;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(sql);
			sb.append("]");
			sb.append(" - ");
			sb.append(Arrays.toString(args));
			return sb.toString();
		}
	}

	public static PreparedStatement createPreparedStatement(Connection connection, Sql sql) throws SQLException {
		PreparedStatement statement;
		if (sql instanceof StoredProcedure) {
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
				Object value = args[i];
				if (value != null) {
					if (value instanceof Enum) {
						value = ((Enum<?>) value).name();
					}
				}
				preparedStatement.setObject(i + 1, value);
			}
		}
	}
	
	public static PreparedStatement prepared(Connection connection, Sql sql) throws SQLException {
		PreparedStatement preparedStatement;
		if (sql instanceof StoredProcedure) {
			preparedStatement = connection.prepareCall(sql.getSql());
		} else {
			preparedStatement = connection.prepareStatement(sql.getSql());
		}
		return preparedStatement;
	}

	public static PreparedStatement prepared(Connection connection, Sql sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		PreparedStatement preparedStatement;
		if (sql instanceof StoredProcedure) {
			preparedStatement = connection.prepareCall(sql.getSql(), resultSetType, resultSetConcurrency);
		} else {
			preparedStatement = connection.prepareStatement(sql.getSql(), resultSetType, resultSetConcurrency);
		}
		return preparedStatement;
	}

	public static PreparedStatement createPreparedStatement(Connection connection, Sql sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		PreparedStatement preparedStatement = prepared(connection, sql, resultSetType, resultSetConcurrency);
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
		if (sql instanceof StoredProcedure) {
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

	public static void query(Connection connection, Sql sql, ResultSetCallback resultSetCallback) throws SQLException {

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

	public static void query(PreparedStatement statement, ResultSetCallback resultSetCallback) throws SQLException {
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

	public static <T> T query(Connection connection, Sql sql, ResultSetMapper<T> resultSetMapper) throws SQLException {
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

	public static <T> T query(PreparedStatement statement, ResultSetMapper<T> resultSetMapper) throws SQLException {
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

	public static boolean execute(Connection connection, Sql sql) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = createPreparedStatement(connection, sql);
			return statement.execute();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public static int update(Connection connection, Sql sql) throws SQLException {
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

	public static Object[] getRowValues(ResultSet resultSet, int size) throws SQLException {
		Object[] values = new Object[size];
		for (int i = 1; i <= size; i++) {
			values[i - 1] = resultSet.getObject(i);
		}
		return values;
	}

	public static String toLikeValue(String value) {
		if (StringUtils.isEmpty(value)) {
			return "%";
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			sb.append("%");
			sb.append(value.charAt(i));
		}
		sb.append("%");
		return sb.toString();
	}

	public static boolean isDataBaseType(Class<?> type) {
		return ClassUtils.isPrimitiveOrWrapper(type) || String.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type) || java.util.Date.class.isAssignableFrom(type)
				|| Time.class.isAssignableFrom(type) || Timestamp.class.isAssignableFrom(type)
				|| Array.class.isAssignableFrom(type) || Blob.class.isAssignableFrom(type)
				|| Clob.class.isAssignableFrom(type) || BigDecimal.class.isAssignableFrom(type)
				|| Reader.class.isAssignableFrom(type) || NClob.class.isAssignableFrom(type);
	}

	/**
	 * Determine the column name to use. The column name is determined based on a
	 * lookup using ResultSetMetaData.
	 * <p>
	 * This method implementation takes into account recent clarifications expressed
	 * in the JDBC 4.0 specification:
	 * <p>
	 * <i>columnLabel - the label for the column specified with the SQL AS clause.
	 * If the SQL AS clause was not specified, then the label is the name of the
	 * column</i>.
	 * 
	 * @param resultSetMetaData the current meta-data to use
	 * @param columnIndex       the index of the column for the look up
	 * @return the column name to use
	 * @throws SQLException in case of lookup failure
	 */
	public static String lookupColumnName(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
		String name = resultSetMetaData.getColumnLabel(columnIndex);
		if (!StringUtils.hasLength(name)) {
			name = resultSetMetaData.getColumnName(columnIndex);
		}
		return name;
	}
}
