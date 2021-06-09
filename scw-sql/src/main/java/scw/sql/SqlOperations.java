package scw.sql;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface SqlOperations {
	<T> T process(String sql, PreparedStatementProcessor<T> processor) throws SqlException;

	default void process(String sql, PreparedStatementCallback callback) throws SqlException {
		process(sql, new PreparedStatementProcessor<Void>() {

			@Override
			public Void processPreparedStatement(PreparedStatement ps) throws SQLException {
				callback.doInPreparedStatement(ps);
				return null;
			}
		});
	}

	<T> T process(String storedProcedure, CallableStatementProcessor<T> processor) throws SqlException;

	default void process(String storedProcedure, CallableStatementCallback callback) throws SqlException {
		process(storedProcedure, new CallableStatementProcessor<Void>() {

			@Override
			public Void processCallableStatement(CallableStatement cs) throws SQLException {
				callback.doInCallableStatement(cs);
				return null;
			}
		});
	}

	default <T> T process(Sql sql, PreparedStatementProcessor<T> processor) throws SqlException {
		if (sql instanceof StoredProcedure) {
			return process(sql.getSql(), new CallableStatementProcessor<T>() {

				@Override
				public T processCallableStatement(CallableStatement cs) throws SQLException {
					SqlUtils.setSqlParams(cs, sql.getParams());
					return processor.processPreparedStatement(cs);
				}
			});
		} else {
			return process(sql.getSql(), new PreparedStatementProcessor<T>() {

				@Override
				public T processPreparedStatement(PreparedStatement ps) throws SQLException {
					SqlUtils.setSqlParams(ps, sql.getParams());
					return processor.processPreparedStatement(ps);
				}
			});
		}
	}

	default void process(Sql sql, PreparedStatementCallback callback) throws SqlException {
		if (sql instanceof StoredProcedure) {
			process(sql.getSql(), new CallableStatementCallback() {

				@Override
				public void doInCallableStatement(CallableStatement cs) throws SQLException {
					SqlUtils.setSqlParams(cs, sql.getParams());
					callback.doInPreparedStatement(cs);
				}
			});
		} else {
			process(sql.getSql(), new PreparedStatementCallback() {

				@Override
				public void doInPreparedStatement(PreparedStatement ps) throws SQLException {
					SqlUtils.setSqlParams(ps, sql.getParams());
					callback.doInPreparedStatement(ps);
				}
			});
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
		return process(sql, new PreparedStatementProcessor<Boolean>() {

			@Override
			public Boolean processPreparedStatement(PreparedStatement ps) throws SQLException {
				return ps.execute();
			}
		});
	}

	default <T> T query(Sql sql, ResultSetMapper<T> resultSetMapper) throws SqlException {
		return process(sql, new PreparedStatementProcessor<T>() {

			@Override
			public T processPreparedStatement(PreparedStatement ps) throws SQLException {
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

	default void query(Sql sql, ResultSetCallback resultSetCallback) throws SqlException {
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

	default <T> List<T> query(Sql sql, RowMapper<T> rowMapper) throws SqlException {
		return query(sql, new DefaultResultSetMapper<T>(rowMapper));
	}

	default int update(Sql sql) throws SqlException {
		return process(sql, new PreparedStatementProcessor<Integer>() {

			@Override
			public Integer processPreparedStatement(PreparedStatement ps) throws SQLException {
				return ps.executeUpdate();
			}
		});
	}

	default List<Object[]> query(Sql sql) throws SqlException {
		return query(sql, new RowMapper<Object[]>() {

			public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				return SqlUtils.getRowValues(rs, rs.getMetaData().getColumnCount());
			}
		});
	}

	default int[] batchUpdate(String sql, Collection<Object[]> batchArgs) throws SqlException {
		return process(sql, new PreparedStatementProcessor<int[]>() {

			@Override
			public int[] processPreparedStatement(PreparedStatement ps) throws SQLException {
				for (Object[] args : batchArgs) {
					SqlUtils.setSqlParams(ps, args);
					ps.addBatch();
				}
				return ps.executeBatch();
			}
		});
	}
}
