package scw.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class PreparedStatementCreator implements SqlProcessor<Connection, PreparedStatement> {
	private static Logger logger = LoggerFactory.getLogger(PreparedStatementCreator.class);

	private final String sql;

	public PreparedStatementCreator(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}

	@Override
	public PreparedStatement process(Connection connection) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		return connection.prepareStatement(sql);
	}

}
