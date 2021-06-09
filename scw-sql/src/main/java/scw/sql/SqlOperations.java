package scw.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface SqlOperations extends ConnectionFactory {
	default <T> T process(ConnectionProcessor<T> process) throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection();
			return process.processConnection(connection);
		} finally {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	/**
	 * @see #process(ConnectionProcessor)
	 * @param callback
	 * @throws SQLException
	 */
	default void process(ConnectionCallback callback) throws SQLException {
		process(new ConnectionProcessor<Void>() {

			@Override
			public Void processConnection(Connection connection)
					throws SQLException {
				callback.doInConnection(connection);
				return null;
			}
		});
	}

	default PreparedStatement create(Connection connection, Sql sql)
			throws SQLException {
		PreparedStatement ps;
		if (sql instanceof StoredProcedure) {
			ps = connection.prepareCall(sql.getSql());
		} else {
			ps = connection.prepareStatement(sql.getSql());
		}
		SqlUtils.setSqlParams(ps, sql.getParams());
		return ps;
	}

	default <T> T process(Connection connection, Sql sql,
			StatementProcessor<PreparedStatement, T> processor)
			throws SQLException {
		return SqlUtils.process(connection,
				new StatementCreator<PreparedStatement>() {

					@Override
					public PreparedStatement create(Connection connection)
							throws SQLException {
						return SqlOperations.this.create(connection, sql);
					}
				}, processor);
	}

	default <T> T process(Sql sql,
			StatementProcessor<PreparedStatement, T> processor)
			throws SqlException {
		try {
			return process(new ConnectionProcessor<T>() {

				@Override
				public T processConnection(Connection connection)
						throws SQLException {
					return process(connection, sql, processor);
				}
			});
		} catch (SQLException e) {
			throw new SqlException(sql, e);
		}
	}

	default void process(Connection connection, Sql sql,
			StatementCallback<PreparedStatement> callback) throws SQLException {
		SqlUtils.process(connection, new StatementCreator<PreparedStatement>() {

			@Override
			public PreparedStatement create(Connection connection)
					throws SQLException {
				return SqlOperations.this.create(connection, sql);
			}
		}, callback);
	}

	default void process(Sql sql, StatementCallback<PreparedStatement> callback)
			throws SqlException {
		try {
			process(new ConnectionCallback() {

				@Override
				public void doInConnection(Connection connection)
						throws SQLException {
					process(connection, sql, callback);
				}
			});
		} catch (SQLException e) {
			throw new SqlException(sql, e);
		}
	}

	default boolean execute(Connection connection, Sql sql) throws SqlException {
		try {
			return SqlUtils.execute(connection, sql,
					new StatementCreator<PreparedStatement>() {

						@Override
						public PreparedStatement create(Connection connection)
								throws SQLException {
							return SqlOperations.this.create(connection, sql);
						}
					});
		} catch (SQLException e) {
			throw new SqlException(sql, e);
		}
	}

	/**
	 * 执行一条sql语句
	 * 
	 * @param sql
	 * @return 返回结果并不代表是否执行成功，意义请参考jdk文档<br/>
	 *         true if the first result is a ResultSet object; false if the
	 *         first result is an update count or there is no result
	 */
	default boolean execute(Sql sql) throws SqlException {
		try {
			return process(new ConnectionProcessor<Boolean>() {

				@Override
				public Boolean processConnection(Connection connection)
						throws SQLException {
					return execute(connection, sql);
				}
			});
		} catch (SQLException e) {
			throw new SqlException(sql, e);
		}
	}

	default <T> T query(Sql sql, ResultSetMapper<T> resultSetMapper)
			throws SqlException {
		return process(sql, new StatementProcessor<T>() {

			@Override
			public T processPreparedStatement(PreparedStatement ps)
					throws SQLException {
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
		});
	}

	default void query(Sql sql, ResultSetCallback resultSetCallback)
			throws SqlException {
		query(sql, new ResultSetMapper<Void>() {

			@Override
			public Void mapper(ResultSet resultSet) throws SQLException {
				resultSetCallback.process(resultSet);
				return null;
			}
		});
	}

	default void query(Sql sql, RowCallback rowCallback) throws SqlException {
		query(sql, new DefaultResultSetCallback(rowCallback));
	}

	default <T> List<T> query(Sql sql, RowMapper<T> rowMapper)
			throws SqlException {
		return query(sql, new DefaultResultSetMapper<T>(rowMapper));
	}

	default int update(Sql sql) throws SqlException {
		return process(sql, new StatementProcessor<Integer>() {

			@Override
			public Integer processPreparedStatement(PreparedStatement ps)
					throws SQLException {
				return ps.executeUpdate();
			}
		});
	}

	default List<Object[]> query(Sql sql) throws SqlException {
		return query(sql, new RowMapper<Object[]>() {

			public Object[] mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				return SqlUtils.getRowValues(rs, rs.getMetaData()
						.getColumnCount());
			}
		});
	}

	default int[] batchUpdate(String sql, Collection<Object[]> batchArgs)
			throws SqlException {
		return process(sql, new StatementProcessor<int[]>() {

			@Override
			public int[] processPreparedStatement(PreparedStatement ps)
					throws SQLException {
				for (Object[] args : batchArgs) {
					SqlUtils.setSqlParams(ps, args);
					ps.addBatch();
				}
				return ps.executeBatch();
			}
		});
	}
}
