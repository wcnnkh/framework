package scw.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import scw.logger.Logger;
import scw.logger.LoggerFactory;

/**
 * 注意并未设置parameters
 * 
 * @author shuchaowen
 *
 */
public class SqlPreparedStatementCreator implements SqlProcessor<Connection, PreparedStatement> {
	private static Logger logger = LoggerFactory.getLogger(SqlPreparedStatementCreator.class);
	private final Sql sql;

	public SqlPreparedStatementCreator(Sql sql) {
		this.sql = sql;
	}

	@Override
	public PreparedStatement process(Connection connection) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug(sql.toString());
		}
		return sql.isStoredProcedure() ? prepareCall(connection, sql.getSql())
				: prepareStatement(connection, sql.getSql());
	}

	protected PreparedStatement prepareCall(Connection connection, String sql) throws SQLException {
		return connection.prepareCall(sql);
	}

	protected PreparedStatement prepareStatement(Connection connection, String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}
}
