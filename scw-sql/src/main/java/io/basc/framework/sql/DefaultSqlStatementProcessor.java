package io.basc.framework.sql;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DefaultSqlStatementProcessor implements SqlStatementProcessor {
	private static Logger logger = LoggerFactory.getLogger(DefaultSqlStatementProcessor.class);

	protected PreparedStatement statementCall(Connection connection, String sql) throws SQLException {
		return connection.prepareCall(sql);
	}

	protected PreparedStatement statementSql(Connection connection, String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}

	@Override
	public PreparedStatement statement(Connection connection, Sql sql) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug(sql.toString());
		}
		PreparedStatement ps = sql.isStoredProcedure() ? statementCall(connection, sql.getSql())
				: statementSql(connection, sql.getSql());
		try {
			SqlUtils.setSqlParams(ps, sql.getParams());
		} catch (Exception e) {
			ps.close();
			throw e;
		}
		return ps;
	}
}
