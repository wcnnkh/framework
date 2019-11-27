package scw.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import scw.logger.Logger;
import scw.logger.LoggerFactory;

public abstract class SqlTemplate implements SqlOperations {
	protected static Logger logger = LoggerFactory.getLogger(SqlTemplate.class);

	public abstract Connection getUserConnection() throws SQLException;

	protected void close(Connection connection) throws SqlException {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new SqlException(e);
			}
		}
	}

	protected boolean execute(Sql sql, Connection connection) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug(SqlUtils.getSqlId(sql));
		}

		PreparedStatement statement = null;
		try {
			statement = SqlUtils.createPreparedStatement(connection, sql);
			return statement.execute();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public boolean execute(Sql sql) throws SqlException {
		Connection connection = null;
		try {
			connection = getUserConnection();
			return execute(sql, connection);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSqlId(sql), e);
		} finally {
			close(connection);
		}
	}

	private void query(PreparedStatement statement, ResultSetCallback resultSetCallback) throws SQLException {
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

	protected void query(Sql sql, Connection connection, ResultSetCallback resultSetCallback) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug(SqlUtils.getSqlId(sql));
		}

		PreparedStatement statement = null;
		try {
			statement = SqlUtils.createPreparedStatement(connection, sql);
			query(statement, resultSetCallback);
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public void query(Sql sql, ResultSetCallback resultSetCallback) throws SqlException {
		Connection connection = null;
		try {
			connection = getUserConnection();
			query(sql, connection, resultSetCallback);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSqlId(sql), e);
		} finally {
			close(connection);
		}
	}

	public void query(Sql sql, RowCallback rowCallback) throws SqlException {
		Connection connection = null;
		try {
			connection = getUserConnection();
			query(sql, connection, new DefaultResultSetCallback(rowCallback));
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSqlId(sql), e);
		} finally {
			close(connection);
		}
	}

	private <T> T query(PreparedStatement statement, ResultSetMapper<T> resultSetMapper) throws SQLException {
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

	protected <T> T query(Sql sql, Connection connection, ResultSetMapper<T> resultSetMapper) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug(SqlUtils.getSqlId(sql));
		}

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

	public <T> T query(Sql sql, ResultSetMapper<T> resultSetMapper) throws SqlException {
		Connection connection = null;
		try {
			connection = getUserConnection();
			return query(sql, connection, resultSetMapper);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSqlId(sql), e);
		} finally {
			close(connection);
		}
	}

	public <T> List<T> query(Sql sql, RowMapper<T> rowMapper) throws SqlException {
		Connection connection = null;
		try {
			connection = getUserConnection();
			return query(sql, connection, new DefaultResultSetMapper<T>(rowMapper));
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSqlId(sql), e);
		} finally {
			close(connection);
		}
	}

	public List<Object[]> query(Sql sql) throws SqlException {
		return query(sql, new RowMapper<Object[]>() {

			public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				return SqlUtils.getRowValues(rs, rs.getMetaData().getColumnCount());
			}

		});
	}

	protected int update(Sql sql, Connection connection) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug(SqlUtils.getSqlId(sql));
		}

		PreparedStatement statement = null;
		try {
			statement = SqlUtils.createPreparedStatement(connection, sql);
			return statement.executeUpdate();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public int update(Sql sql) throws SqlException {
		Connection connection = null;
		try {
			connection = getUserConnection();
			return update(sql, connection);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSqlId(sql), e);
		} finally {
			close(connection);
		}
	}

	protected int[] batch(Connection connection, Collection<String> sqls) throws SQLException {
		PreparedStatement preparedStatement = null;
		String currentSql = null;
		try {
			for (String sql : sqls) {
				currentSql = sql;
				if (preparedStatement == null) {
					preparedStatement = connection.prepareStatement(sql);
				} else {
					preparedStatement.addBatch(sql);
				}
			}
			return preparedStatement.executeBatch();
		} catch (SQLException e) {
			throw currentSql == null ? e : new SQLException(currentSql, e);
		} finally {
			if (preparedStatement != null) {
				preparedStatement.clearBatch();
				preparedStatement.close();
			}
		}
	}

	public int[] batch(Collection<String> sqls) throws SqlException {
		Connection connection = null;
		try {
			connection = getUserConnection();
			return batch(connection, sqls);
		} catch (SQLException e) {
			throw new SqlException(e);
		} finally {
			close(connection);
		}
	}

	protected int[] batch(Connection connection, String sql, Collection<Object[]> batchArgs) throws SQLException {
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			for (Object[] args : batchArgs) {
				SqlUtils.setSqlParams(preparedStatement, args);
				preparedStatement.addBatch();
			}
			return preparedStatement.executeBatch();
		} catch (SQLException e) {
			throw new SQLException(sql, e);
		} finally {
			if (preparedStatement != null) {
				preparedStatement.clearBatch();
				preparedStatement.close();
			}
		}
	}

	public int[] batch(String sql, Collection<Object[]> batchArgs) throws SqlException {
		Connection connection = null;
		try {
			connection = getUserConnection();
			return batch(connection, sql, batchArgs);
		} catch (SQLException e) {
			throw new SqlException(e);
		} finally {
			close(connection);
		}
	}
}
