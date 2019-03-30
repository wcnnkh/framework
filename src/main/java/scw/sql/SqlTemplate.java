package scw.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import scw.logger.Logger;
import scw.logger.LoggerFactory;

public abstract class SqlTemplate implements SqlOperations {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private boolean debug;

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	protected abstract Connection getUserConnection() throws SQLException;

	protected void close(Connection connection) throws SqlException {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new SqlException(e);
			}
		}
	}

	public void execute(Sql sql) throws SqlException {
		Connection connection = null;
		log(sql);
		try {
			connection = getUserConnection();
			SqlUtils.execute(connection, sql);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSqlId(sql), e);
		} finally {
			close(connection);
		}
	}

	public void query(Sql sql, ResultSetCallback resultSetCallback)
			throws SqlException {
		Connection connection = null;
		log(sql);
		try {
			connection = getUserConnection();
			SqlUtils.query(connection, sql, resultSetCallback);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSqlId(sql), e);
		} finally {
			close(connection);
		}
	}

	public void query(Sql sql, final RowCallback rowCallback)
			throws SqlException {
		query(sql, new ResultSetCallback() {

			public void process(ResultSet rs) throws SQLException {
				for (int i = 1; rs.next(); i++) {
					rowCallback.processRow(rs, i);
				}
			}
		});
	}

	public <T> T query(Sql sql, ResultSetMapper<T> resultSetMapper)
			throws SqlException {
		Connection connection = null;
		log(sql);
		try {
			connection = getUserConnection();
			return SqlUtils.query(connection, sql, resultSetMapper);
		} catch (SQLException e) {
			throw new SqlException(SqlUtils.getSqlId(sql), e);
		} finally {
			close(connection);
		}
	}

	public <T> List<T> query(Sql sql, final RowMapper<T> rowMapper)
			throws SqlException {
		return query(sql, new ResultSetMapper<List<T>>() {

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

	public int update(Sql sql) throws SqlException {
		Connection connection = null;
		log(sql);
		try {
			connection = getUserConnection();
			return SqlUtils.update(connection, sql);
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
