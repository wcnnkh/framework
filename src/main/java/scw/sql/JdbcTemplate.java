package scw.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class JdbcTemplate implements SqlOperations, ConnectionFactory {

	protected void close(Connection connection) throws SqlException {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new SqlException(e);
			}
		}
	}

	protected PreparedStatement createStatement(Sql sql, Connection connection) throws SQLException {
		PreparedStatement statement;
		if (sql.isStoredProcedure()) {
			statement = connection.prepareCall(sql.getSql());
		} else {
			statement = connection.prepareStatement(sql.getSql());
		}

		setParams(statement, sql.getParams());
		return statement;
	}

	protected void setParams(PreparedStatement preparedStatement, Object[] args) throws SQLException {
		if (args != null && args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);
			}
		}
	}

	private boolean execute(Sql sql, Connection connection) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = createStatement(sql, connection);
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
			connection = getConnection();
			return execute(sql, connection);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSQLId(sql), e);
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

	private void query(Sql sql, Connection connection, ResultSetCallback resultSetCallback) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = createStatement(sql, connection);
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
			connection = getConnection();
			query(sql, connection, resultSetCallback);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSQLId(sql), e);
		} finally {
			close(connection);
		}
	}

	private void query(PreparedStatement statement, RowCallback rowCallback) throws SQLException {
		ResultSet resultSet = null;
		try {
			resultSet = statement.executeQuery();
			for (int i = 1; resultSet.next(); i++) {
				rowCallback.processRow(resultSet, i);
			}
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}

	private void query(Sql sql, Connection connection, RowCallback rowCallback) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = createStatement(sql, connection);
			query(statement, rowCallback);
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public void query(Sql sql, RowCallback rowCallback) throws SqlException {
		Connection connection = null;
		try {
			connection = getConnection();
			query(sql, connection, rowCallback);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSQLId(sql), e);
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

	private <T> T query(Sql sql, Connection connection, ResultSetMapper<T> resultSetMapper) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = createStatement(sql, connection);
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
			connection = getConnection();
			return query(sql, connection, resultSetMapper);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSQLId(sql), e);
		} finally {
			close(connection);
		}
	}

	private <T> List<T> query(PreparedStatement statement, RowMapper<T> rowMapper) throws SQLException {
		ResultSet resultSet = null;
		List<T> list;
		T t;
		int row;
		try {
			resultSet = statement.executeQuery();
			row = resultSet.getRow();
			list = (row == 0 ? new ArrayList<T>() : new ArrayList<T>(row));
			for (int i = 1; resultSet.next(); i++) {
				t = rowMapper.mapRow(resultSet, i);
				if (t != null) {
					list.add(t);
				}
			}
			return list;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}

	private <T> List<T> query(Sql sql, Connection connection, RowMapper<T> rowMapper) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = createStatement(sql, connection);
			return query(statement, rowMapper);
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public <T> List<T> query(Sql sql, RowMapper<T> rowMapper) throws SqlException {
		Connection connection = null;
		try {
			connection = getConnection();
			return query(sql, connection, rowMapper);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSQLId(sql), e);
		} finally {
			close(connection);
		}
	}

	private int update(Sql sql, Connection connection) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = createStatement(sql, connection);
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
			connection = getConnection();
			return update(sql, connection);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSQLId(sql), e);
		} finally {
			close(connection);
		}
	}
}
