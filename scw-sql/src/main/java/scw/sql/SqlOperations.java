package scw.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import scw.util.stream.AutoCloseStream;
import scw.util.stream.StreamProcessorSupport;

public interface SqlOperations extends ConnectionFactory {
	default SqlProcessor<Connection, PreparedStatement> prepareStatement(String sql) {
		return new PreparedStatementCreator(sql);
	}

	default SqlProcessor<Connection, CallableStatement> prepareCall(String sql) {
		return new CallableStatementCreator(sql);
	}

	default SqlProcessor<Connection, ? extends PreparedStatement> prepare(Sql sql) {
		return new SqlPreparedStatementCreator(sql);
	}

	default <T> T process(Connection connection, Sql sql, SqlProcessor<PreparedStatement, T> processor)
			throws SqlException {
		try {
			return SqlUtils.process(connection, prepare(sql), processor);
		} catch (Throwable e) {
			if (e instanceof SqlException) {
				throw (SqlException) e;
			}
			throw new SqlException(sql, e);
		}
	}

	default <T> T process(Sql sql, SqlProcessor<PreparedStatement, T> processor) throws SqlException {
		try {
			return process(new SqlProcessor<Connection, T>() {

				@Override
				public T process(Connection connection) throws SQLException {
					return SqlOperations.this.process(connection, sql, processor);
				}
			});
		} catch (Throwable e) {
			if (e instanceof SqlException) {
				throw (SqlException) e;
			}
			throw new SqlException(sql, e);
		}
	}

	default void process(Connection connection, Sql sql, SqlCallback<PreparedStatement> callback) throws SqlException {
		try {
			SqlUtils.process(connection, prepare(sql), callback);
		} catch (Throwable e) {
			if (e instanceof SqlException) {
				throw (SqlException) e;
			}
			throw new SqlException(sql, e);
		}
	}

	default void process(Sql sql, SqlCallback<PreparedStatement> callback) throws SqlException {
		try {
			process(new SqlCallback<Connection>() {

				@Override
				public void call(Connection connection) throws SQLException {
					process(connection, sql, callback);
				}
			});
		} catch (Throwable e) {
			if (e instanceof SqlException) {
				throw (SqlException) e;
			}
			throw new SqlException(sql, e);
		}
	}

	default boolean execute(Connection connection, Sql sql) throws SqlException {
		try {
			return SqlUtils.execute(connection, prepare(sql));
		} catch (SQLException e) {
			throw new SqlException(sql, e);
		}
	}

	/**
	 * 执行一条sql语句
	 * 
	 * @param sql
	 * @return 返回结果并不代表是否执行成功，意义请参考jdk文档<br/>
	 *         true if the first result is a ResultSet object; false if the first
	 *         result is an update count or there is no result
	 */
	default boolean execute(Sql sql) throws SqlException {
		try {
			return process(new SqlProcessor<Connection, Boolean>() {

				@Override
				public Boolean process(Connection connection) throws SQLException {
					return execute(connection, sql);
				}
			});
		} catch (Throwable e) {
			if (e instanceof SqlException) {
				throw (SqlException) e;
			}
			throw new SqlException(sql, e);
		}
	}

	default int update(Connection connection, Sql sql) throws SqlException {
		return process(connection, sql, new SqlProcessor<PreparedStatement, Integer>() {

			@Override
			public Integer process(PreparedStatement statement) throws SQLException {
				return statement.executeUpdate();
			}
		});
	}

	default int update(Sql sql) throws SqlException {
		try {
			return process(new SqlProcessor<Connection, Integer>() {

				@Override
				public Integer process(Connection connection) throws SQLException {
					return update(connection, sql);
				}
			});
		} catch (Throwable e) {
			throw new SqlException(sql, e);
		}
	}

	default Stream<ResultSet> streamQuery(Connection connection, Sql sql) throws SqlException {
		try {
			return SqlUtils.streamQuery(connection, prepare(sql), (ps) -> ps.executeQuery(), () -> sql.toString());
		} catch (Throwable e) {
			throw new SqlException(sql, e);
		}
	}

	default <T> AutoCloseStream<T> streamQuery(Connection connection, Sql sql,
			SqlProcessor<ResultSet, ? extends T> resultSetProcessor) throws SqlException {
		Stream<T> stream = streamQuery(connection, sql).map(new Function<ResultSet, T>() {
			@Override
			public T apply(ResultSet t) {
				try {
					return resultSetProcessor.process(t);
				} catch (SQLException e) {
					throw new SqlException(sql, e);
				}
			}
		});
		return StreamProcessorSupport.autoClose(stream);
	}

	default Stream<ResultSet> streamQuery(Sql sql) throws SqlException {
		try {
			return streamProcess((connection) -> {
				return streamQuery(connection, sql);
			}, () -> sql.toString());
		} catch (Throwable e) {
			if (e instanceof SqlException) {
				throw (SqlException) e;
			}
			throw new SqlException(sql, e);
		}
	}

	default <T> AutoCloseStream<T> streamQuery(Sql sql, SqlProcessor<ResultSet, ? extends T> resultSetProcessor)
			throws SqlException {
		Stream<T> stream = streamQuery(sql).map(new Function<ResultSet, T>() {
			@Override
			public T apply(ResultSet t) {
				try {
					return resultSetProcessor.process(t);
				} catch (SQLException e) {
					throw new SqlException(sql, e);
				}
			}
		});
		return StreamProcessorSupport.autoClose(stream);
	}

	default <T> T query(Connection connection, Sql sql, SqlProcessor<ResultSet, T> resultSetProcessor)
			throws SqlException {
		try {
			return SqlUtils.query(connection, prepare(sql), (ps) -> ps.executeQuery(), resultSetProcessor);
		} catch (Throwable e) {
			if (e instanceof SqlException) {
				throw (SqlException) e;
			}
			throw new SqlException(sql, e);
		}
	}

	default <T> T query(Sql sql, SqlProcessor<ResultSet, T> resultSetProcessor) throws SqlException {
		try {
			return process(new SqlProcessor<Connection, T>() {

				@Override
				public T process(Connection connection) throws SQLException {
					return query(connection, sql, resultSetProcessor);
				}
			});
		} catch (Throwable e) {
			if (e instanceof SqlException) {
				throw (SqlException) e;
			}
			throw new SqlException(sql, e);
		}
	}

	default void query(Sql sql, SqlCallback<ResultSet> resultSetCallback) throws SqlException {
		query(sql, new SqlProcessor<ResultSet, Void>() {

			@Override
			public Void process(ResultSet resultSet) throws SQLException {
				resultSetCallback.call(resultSet);
				return null;
			}
		});
	}

	default void query(Sql sql, RowCallback rowCallback) throws SqlException {
		query(sql, new DefaultResultSetCallback(rowCallback));
	}

	default <T> List<T> query(Sql sql, RowMapper<T> rowMapper) throws SqlException {
		return query(sql, new RowMapperProcessor<T>(rowMapper));
	}

	default int[] executeBatch(Connection connection, String sql, Collection<Object[]> batchArgs) throws SqlException {
		try {
			return SqlUtils.executeBatch(connection, prepareStatement(sql), batchArgs);
		} catch (Throwable e) {
			if (e instanceof SqlException) {
				throw (SqlException) e;
			}
			throw new SqlException(sql, e);
		}
	}

	default int[] executeBatch(String sql, Collection<Object[]> batchArgs) throws SqlException {
		try {
			return process(new SqlProcessor<Connection, int[]>() {

				@Override
				public int[] process(Connection connection) throws SQLException {
					return executeBatch(connection, sql, batchArgs);
				}
			});
		} catch (Throwable e) {
			if (e instanceof SqlException) {
				throw (SqlException) e;
			}
			throw new SqlException(sql, e);
		}
	}
}
