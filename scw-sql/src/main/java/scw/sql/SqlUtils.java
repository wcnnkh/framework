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
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;
import scw.util.stream.Callback;
import scw.util.stream.Cursor;
import scw.util.stream.Processor;
import scw.util.stream.StreamProcessor;
import scw.util.stream.StreamProcessorSupport;

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

	public static <S, P extends Statement> StreamProcessor<P, SQLException> streamProcess(S source,
			Processor<S, ? extends P, ? extends SQLException> statementCreator) throws SQLException {
		P ps = statementCreator.process(source);
		StreamProcessor<P, SQLException> streamProcessor = StreamProcessorSupport.stream(ps);
		return streamProcessor.onClose(() -> {
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
		});
	}

	public static <S, P extends Statement, T, E extends Throwable> T process(S source,
			Processor<S, ? extends P, ? extends SQLException> statementCreator,
			Processor<P, ? extends T, ? extends E> processor) throws SQLException, E {
		P ps = null;
		try {
			ps = statementCreator.process(source);
			return processor.process(ps);
		} finally {
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
		}
	}

	public static <S, P extends Statement> void process(S source,
			Processor<S, ? extends P, ? extends SQLException> statementCreator,
			Callback<P, ? extends SQLException> callback) throws SQLException {
		P statement = null;
		try {
			statement = statementCreator.process(source);
			callback.call(statement);
		} finally {
			if (statement != null && !statement.isClosed()) {
				statement.close();
			}
		}
	}

	public static <T> T process(PreparedStatement ps,
			Processor<ResultSet, ? extends T, ? extends SQLException> resultSetProcessor) throws SQLException {
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

	public static int update(Connection connection,
			Processor<Connection, ? extends PreparedStatement, ? extends SQLException> preparedStatementCreator)
			throws SQLException {
		return process(connection, preparedStatementCreator, (ps) -> {
			return ps.executeUpdate();
		});
	}

	public static <P extends PreparedStatement> int[] executeBatch(Connection connection,
			Processor<Connection, ? extends P, ? extends SQLException> connectionProcessor,
			@Nullable Collection<Object[]> batchArgs) throws SQLException {
		return process(connection, connectionProcessor, (ps) -> {
			if (batchArgs != null) {
				for (Object[] args : batchArgs) {
					setSqlParams(ps, args);
					ps.addBatch();
				}
			}
			return ps.executeBatch();
		});
	}

	public static <S> StreamProcessor<ResultSet, SQLException> streamQuery(S source,
			Processor<S, ? extends ResultSet, ? extends SQLException> queryProcessor) throws SQLException {
		ResultSet resultSet = queryProcessor.process(source);
		StreamProcessor<ResultSet, SQLException> streamProcessor = StreamProcessorSupport.stream(resultSet);
		return streamProcessor.onClose(() -> {
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
			}
		});
	}

	public static <S, P extends Statement> StreamProcessor<ResultSet, SQLException> streamQuery(S source,
			Processor<S, ? extends P, ? extends SQLException> statementCreator,
			Processor<P, ? extends ResultSet, ? extends SQLException> queryProcessor) throws SQLException {
		P statement = statementCreator.process(source);
		try {
			return streamQuery(statement, queryProcessor);
		} catch (SQLException e) {
			if (statement != null && !statement.isClosed()) {
				statement.close();
			}
			throw e;
		}
	}

	public static <S> Stream<ResultSet> streamQuery(S source, Processor<S, ResultSet, ? extends SQLException> query,
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
				throw throwableSqlException(e, desc);
			}
		});
	}

	public static <S, P extends Statement> Stream<ResultSet> streamQuery(S source,
			Processor<S, ? extends P, ? extends SQLException> statementCreator,
			Processor<P, ResultSet, ? extends SQLException> query, @Nullable Supplier<String> desc)
			throws SQLException {
		P statement = statementCreator.process(source);
		try {
			return streamQuery(statement, query, desc).onClose(() -> {
				try {
					if (!statement.isClosed()) {
						statement.close();
					}
				} catch (Throwable e) {
					throw throwableSqlException(e, desc);
				}
			});
		} catch (SQLException e) {
			if (!statement.isClosed()) {
				statement.close();
			}
			throw e;
		}
	}

	public static SqlException throwableSqlException(Throwable e, Supplier<String> desc) {
		if (e instanceof SqlException) {
			return (SqlException) e;
		}
		if (desc == null) {
			return new SqlException(e);
		}
		return new SqlException(desc.get(), e);
	}

	public static <S, T, P extends Statement, E extends Throwable> Cursor<T> query(S source,
			Processor<S, ? extends P, ? extends SQLException> statementCreator,
			Processor<P, ResultSet, ? extends SQLException> query,
			Processor<ResultSet, ? extends T, ? extends E> mapProcessor, @Nullable Supplier<String> desc)
			throws SQLException, E {
		Stream<T> stream = streamQuery(source, statementCreator, query, desc).map((rs) -> {
			try {
				return mapProcessor.process(rs);
			} catch (Throwable e) {
				throw throwableSqlException(e, desc);
			}
		});
		return StreamProcessorSupport.cursor(stream);
	}

	public static <S, T, E extends Throwable> T query(S source,
			Processor<S, ? extends ResultSet, ? extends SQLException> queryProcessor,
			Processor<ResultSet, ? extends T, ? extends E> mapProcessor) throws SQLException, E {
		ResultSet rs = queryProcessor.process(source);
		try {
			return mapProcessor.process(rs);
		} finally {
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
		}
	}

	public static <S, P extends Statement, T, E extends Throwable> T query(S source,
			Processor<S, ? extends P, ? extends SQLException> statementCreator,
			Processor<P, ? extends ResultSet, ? extends SQLException> queryProcessor,
			Processor<ResultSet, ? extends T, ? extends E> mapProcessor) throws SQLException, E {
		P statement = statementCreator.process(source);
		try {
			return query(statement, queryProcessor, mapProcessor);
		} finally {
			if (statement != null && !statement.isClosed()) {
				statement.close();
			}
		}
	}

	public static PreparedStatementProcessor prepare(Connection connection, Sql sql,
			SqlStatementProcessor statementProcessor) {
		return new PreparedStatementProcessor(() -> connection, false,
				(conn) -> statementProcessor.statement(conn, sql), () -> sql.toString());
	}

	public static PreparedStatementProcessor prepare(ConnectionFactory connectionFactory, Sql sql,
			SqlStatementProcessor statementProcessor) {
		return new PreparedStatementProcessor(() -> connectionFactory.getConnection(), true,
				(conn) -> statementProcessor.statement(conn, sql), () -> sql.toString());
	}

	public static <T> Cursor<T> query(Connection connection, Sql sql, SqlStatementProcessor statementProcessor,
			Processor<ResultSet, ? extends T, ? extends Throwable> processor) throws SqlException {
		return prepare(connection, sql, statementProcessor).query().stream(processor);
	}

	public static <T> Cursor<T> query(ConnectionFactory connectionFactory, Sql sql,
			SqlStatementProcessor statementProcessor, Processor<ResultSet, ? extends T, ? extends Throwable> processor)
			throws SqlException {
		return prepare(connectionFactory, sql, statementProcessor).query().stream(processor);
	}
	
	public static MultiValueMap<String, Object> getRowValueMap(ResultSet rs) throws SQLException{
		ResultSetMetaData metaData = rs.getMetaData();
		int cols = metaData.getColumnCount();
		MultiValueMap<String, Object> values = new LinkedMultiValueMap<String, Object>();
		for (int i = 1; i <= cols; i++) {
			String name = SqlUtils.lookupColumnName(metaData, i);
			Object value = rs.getObject(i);
			values.add(name, value);
		}
		return values;
	}
}
