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
import java.sql.Statement;
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

	public static void setSqlParams(PreparedStatement preparedStatement,
			Object[] args) throws SQLException {
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

	public static Object[] getRowValues(ResultSet resultSet, int size)
			throws SQLException {
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
		return ClassUtils.isPrimitiveOrWrapper(type)
				|| String.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type)
				|| java.util.Date.class.isAssignableFrom(type)
				|| Time.class.isAssignableFrom(type)
				|| Timestamp.class.isAssignableFrom(type)
				|| Array.class.isAssignableFrom(type)
				|| Blob.class.isAssignableFrom(type)
				|| Clob.class.isAssignableFrom(type)
				|| BigDecimal.class.isAssignableFrom(type)
				|| Reader.class.isAssignableFrom(type)
				|| NClob.class.isAssignableFrom(type);
	}

	/**
	 * Determine the column name to use. The column name is determined based on
	 * a lookup using ResultSetMetaData.
	 * <p>
	 * This method implementation takes into account recent clarifications
	 * expressed in the JDBC 4.0 specification:
	 * <p>
	 * <i>columnLabel - the label for the column specified with the SQL AS
	 * clause. If the SQL AS clause was not specified, then the label is the
	 * name of the column</i>.
	 * 
	 * @param resultSetMetaData
	 *            the current meta-data to use
	 * @param columnIndex
	 *            the index of the column for the look up
	 * @return the column name to use
	 * @throws SQLException
	 *             in case of lookup failure
	 */
	public static String lookupColumnName(ResultSetMetaData resultSetMetaData,
			int columnIndex) throws SQLException {
		String name = resultSetMetaData.getColumnLabel(columnIndex);
		if (!StringUtils.hasLength(name)) {
			name = resultSetMetaData.getColumnName(columnIndex);
		}
		return name;
	}

	public static <P extends Statement, T> T process(Connection connection,
			StatementCreator<? extends P> statementCreator,
			StatementProcessor<P, T> processor) throws SQLException {
		P ps = null;
		try {
			ps = statementCreator.create(connection);
			return processor.processStatement(ps);
		} finally {
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
		}
	}

	public static <P extends Statement> void process(Connection connection,
			StatementCreator<? extends P> statementCreator,
			StatementCallback<P> callback) throws SQLException {
		P statement = null;
		try {
			statement = statementCreator.create(connection);
			callback.doInStatement(statement);
		} finally {
			if (statement != null && !statement.isClosed()) {
				statement.close();
			}
		}
	}

	public static <P extends PreparedStatement, T> T process(
			Connection connection, Sql sql,
			StatementCreator<? extends P> statementCreator,
			StatementProcessor<P, T> processor) throws SQLException {
		return process(connection, statementCreator, processor)
		return process(connection, sql.getSql(), preparedStatementCreator,
				new StatementProcessor<P, T>() {

					@Override
					public T processPreparedStatement(P ps) throws SQLException {
						setSqlParams(ps, sql.getParams());
						return processor.processPreparedStatement(ps);
					}

				});
	};

	public static int update(
			Connection connection,
			Sql sql,
			PreparedStatementCreator<? extends PreparedStatement> preparedStatementCreator)
			throws SQLException {
		return process(connection, sql, preparedStatementCreator,
				new StatementProcessor<PreparedStatement, Integer>() {

					@Override
					public Integer processPreparedStatement(PreparedStatement ps)
							throws SQLException {
						return ps.executeUpdate();
					}
				});
	}

	/**
	 * 执行一条sql语句
	 * 
	 * @param sql
	 * @return 返回结果并不代表是否执行成功，意义请参考jdk文档<br/>
	 *         true if the first result is a ResultSet object; false if the
	 *         first result is an update count or there is no result
	 * @throws SQLException
	 */
	public static boolean execute(Connection connection, Sql sql,
			StatementCreator<? extends PreparedStatement> statementCreator)
			throws SQLException {
		return process(connection, statementCreator,
				new StatementProcessor<PreparedStatement, Boolean>() {

					@Override
					public Boolean processStatement(PreparedStatement statement)
							throws SQLException {
						return ps.execute();
					}
				});
	}

	public static <T> T process(PreparedStatement ps,
			ResultSetMapper<T> resultSetMapper) throws SQLException {
		ResultSet resultSet = null;
		try {
			resultSet = ps.executeQuery();
			return resultSetMapper.mapper(resultSet);
		} finally {
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
			}
		}
	}

	public static <T> T query(
			Connection connection,
			Sql sql,
			PreparedStatementCreator<? extends PreparedStatement> preparedStatementCreator,
			ResultSetMapper<T> resultSetMapper) throws SQLException {
		return process(connection, sql, preparedStatementCreator,
				new StatementProcessor<PreparedStatement, T>() {

					@Override
					public T processPreparedStatement(PreparedStatement ps)
							throws SQLException {
						return process(ps, resultSetMapper);
					}
				});
	}
}
