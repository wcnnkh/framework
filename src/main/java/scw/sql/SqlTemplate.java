package scw.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;

public abstract class SqlTemplate implements SqlOperations {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public abstract boolean isDebug();
	
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

	protected boolean execute(Sql sql, Connection connection)
			throws SQLException {
		log(sql);
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

	public void execute(Sql sql) throws SqlException {
		Connection connection = null;
		try {
			connection = getUserConnection();
			execute(sql, connection);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSqlId(sql), e);
		} finally {
			close(connection);
		}
	}

	private void query(PreparedStatement statement,
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

	protected void query(Sql sql, Connection connection,
			ResultSetCallback resultSetCallback) throws SQLException {
		log(sql);
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

	public void query(Sql sql, ResultSetCallback resultSetCallback)
			throws SqlException {
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

	protected void query(Sql sql, Connection connection,
			final RowCallback rowCallback) throws SQLException {
		query(sql, connection, new ResultSetCallback() {

			public void process(ResultSet rs) throws SQLException {
				for (int i = 1; rs.next(); i++) {
					rowCallback.processRow(rs, i);
				}
			}
		});
	}

	public void query(Sql sql, RowCallback rowCallback)
			throws SqlException {
		Connection connection = null;
		try {
			connection = getUserConnection();
			query(sql, connection, rowCallback);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSqlId(sql), e);
		} finally {
			close(connection);
		}
	}

	private <T> T query(PreparedStatement statement,
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

	protected <T> T query(Sql sql, Connection connection,
			ResultSetMapper<T> resultSetMapper) throws SQLException {
		log(sql);
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

	public <T> T query(Sql sql, ResultSetMapper<T> resultSetMapper)
			throws SqlException {
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

	protected <T> List<T> query(Sql sql, Connection connection,
			final RowMapper<T> rowMapper) throws SQLException {
		return query(sql, connection, new ResultSetMapper<List<T>>() {

			public List<T> mapper(ResultSet resultSet) throws SQLException {
				List<T> list = new LinkedList<T>();
				for (int i = 1; resultSet.next(); i++) {
					T t = rowMapper.mapRow(resultSet, i);
					if (t != null) {
						list.add(t);
					}
				}
				return list;
			}
		});
	}

	public <T> List<T> query(Sql sql, RowMapper<T> rowMapper)
			throws SqlException {
		Connection connection = null;
		try {
			connection = getUserConnection();
			return query(sql, connection, rowMapper);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSqlId(sql), e);
		} finally {
			close(connection);
		}
	}

	protected int update(Sql sql, Connection connection) throws SQLException {
		log(sql);
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

	protected void log(Sql sql) {
		if (isDebug()) {
			logger.debug(SqlUtils.getSqlId(sql));
		}
	}
}
