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
import java.util.Collection;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.lang.Nullable;

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

	public static void setSqlParams(PreparedStatement preparedStatement, Object[] args) throws SQLException {
		if (args == null || args.length == 0) {
			return;
		}

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

	public static <P extends Statement, T> T process(Connection connection,
			SqlProcessor<Connection, ? extends P> statementCreator, SqlProcessor<P, ? extends T> processor)
			throws SQLException {
		P ps = null;
		try {
			ps = statementCreator.process(connection);
			return processor.process(ps);
		} finally {
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
		}
	}

	public static <P extends Statement> void process(Connection connection,
			SqlProcessor<Connection, ? extends P> statementCreator, SqlCallback<P> callback) throws SQLException {
		P statement = null;
		try {
			statement = statementCreator.process(connection);
			callback.call(statement);
		} finally {
			if (statement != null && !statement.isClosed()) {
				statement.close();
			}
		}
	}

	public static <P extends PreparedStatement, T> T process(Connection connection,
			SqlProcessor<Connection, ? extends P> preparedStatementCreator, @Nullable Object[] args,
			SqlProcessor<P, ? extends T> processor) throws SQLException {
		return SqlUtils.process(connection, preparedStatementCreator, new SqlProcessor<P, T>() {

			@Override
			public T process(P statement) throws SQLException {
				setSqlParams(statement, args);
				return processor.process(statement);
			}
		});
	}

	public static <P extends PreparedStatement> void process(Connection connection,
			SqlProcessor<Connection, ? extends P> preparedStatementCreator, @Nullable Object[] args,
			SqlCallback<P> callback) throws SQLException {
		SqlUtils.process(connection, preparedStatementCreator, new SqlCallback<P>() {

			@Override
			public void call(P statement) throws SQLException {
				setSqlParams(statement, args);
				callback.call(statement);
			}
		});
	}

	/**
	 * 执行一条sql语句
	 * 
	 * @return 返回结果并不代表是否执行成功，意义请参考jdk文档<br/>
	 *         true if the first result is a ResultSet object; false if the first
	 *         result is an update count or there is no result
	 * @throws SQLException
	 */
	public static boolean execute(Connection connection,
			SqlProcessor<Connection, ? extends PreparedStatement> preparedStatementCreator, Object... args)
			throws SQLException {
		return process(connection, preparedStatementCreator, args, new SqlProcessor<PreparedStatement, Boolean>() {

			@Override
			public Boolean process(PreparedStatement statement) throws SQLException {
				return statement.execute();
			}
		});
	}

	public static <T> T process(PreparedStatement ps, SqlProcessor<ResultSet, T> resultSetProcessor)
			throws SQLException {
		ResultSet resultSet = null;
		try {
			resultSet = ps.executeQuery();
			return resultSetProcessor.process(resultSet);
		} finally {
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
			}
		}
	}

	public static <T> T query(Connection connection,
			SqlProcessor<Connection, ? extends PreparedStatement> preparedStatementCreator, @Nullable Object[] args,
			SqlProcessor<ResultSet, T> resultSetProcessor) throws SQLException {
		return process(connection, preparedStatementCreator, args, new SqlProcessor<PreparedStatement, T>() {

			@Override
			public T process(PreparedStatement statement) throws SQLException {
				return SqlUtils.process(statement, resultSetProcessor);
			}
		});
	}

	public static int update(Connection connection,
			SqlProcessor<Connection, ? extends PreparedStatement> preparedStatementCreator, Object... args)
			throws SQLException {
		return process(connection, preparedStatementCreator, args, new SqlProcessor<PreparedStatement, Integer>() {

			@Override
			public Integer process(PreparedStatement statement) throws SQLException {
				return statement.executeUpdate();
			}
		});
	}

	public static <P extends PreparedStatement> int[] executeBatch(Connection connection,
			SqlProcessor<Connection, ? extends P> connectionProcessor, @Nullable Collection<Object[]> batchArgs,
			@Nullable SqlCallback<P> callback) throws SQLException {
		return process(connection, connectionProcessor, new SqlProcessor<P, int[]>() {

			@Override
			public int[] process(P statement) throws SQLException {
				if (batchArgs != null) {
					for (Object[] args : batchArgs) {
						setSqlParams(statement, args);
						statement.addBatch();
					}
				}

				if (callback != null) {
					callback.call(statement);
				}
				return statement.executeBatch();
			}
		});
	}

	public static int[] executeBatch(Connection connection,
			SqlProcessor<Connection, ? extends PreparedStatement> connectionProcessor, Collection<Object[]> batchArgs)
			throws SQLException {
		return executeBatch(connection, connectionProcessor, batchArgs, null);
	}

	public static <S> Stream<ResultSet> query(S source, SqlProcessor<S, ResultSet> query,
			@Nullable Supplier<String> desc) throws SQLException {
		ResultSet resultSet = query.process(source);
		ResultSetIterator iterator = new ResultSetIterator(resultSet);
		Spliterator<ResultSet> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
		Stream<ResultSet> stream = StreamSupport.stream(spliterator, false);
		return stream.onClose(() -> {
			try {
				if (!resultSet.isClosed()) {
					resultSet.close();
				}
			} catch (Throwable e) {
				if (e instanceof SqlException) {
					throw (SqlException) e;
				}
				if (desc == null) {
					throw new SqlException(e);
				}
				throw new SqlException(desc.get(), e);
			}
		});
	}

	public static <P extends Statement> Stream<ResultSet> query(Connection connection,
			SqlProcessor<Connection, ? extends P> statementCreate, SqlProcessor<P, ResultSet> query,
			@Nullable Supplier<String> desc) throws SQLException {
		P statement = statementCreate.process(connection);
		try {
			return query(statement, query, desc).onClose(() -> {
				try {
					if (!statement.isClosed()) {
						statement.close();
					}
				} catch (Throwable e) {
					if (e instanceof SqlException) {
						throw (SqlException) e;
					}
					if (desc == null) {
						throw new SqlException(e);
					}
					throw new SqlException(desc.get(), e);
				}
			});
		} catch (SQLException e) {
			if (!statement.isClosed()) {
				statement.close();
			}
			throw e;
		}
	}

	public static <P extends PreparedStatement> Stream<ResultSet> query(Connection connection,
			SqlProcessor<Connection, ? extends P> statementCreate, Object[] args, @Nullable Supplier<String> desc)
			throws SQLException {
		return query(connection, statementCreate, new SqlProcessor<P, ResultSet>() {

			@Override
			public ResultSet process(P source) throws SQLException {
				setSqlParams(source, args);
				return source.executeQuery();
			}
		}, desc);
	}
}
