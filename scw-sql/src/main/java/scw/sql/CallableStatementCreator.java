package scw.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class CallableStatementCreator implements SqlProcessor<Connection, CallableStatement> {
	private static Logger logger = LoggerFactory.getLogger(CallableStatementCreator.class);
	private final String sql;

	public CallableStatementCreator(String sql) {
		this.sql = sql;
	}

	@Override
	public CallableStatement process(Connection connection) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		return connection.prepareCall(sql);
	}

}
