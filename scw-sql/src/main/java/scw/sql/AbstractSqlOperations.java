package scw.sql;

import java.sql.Connection;
import java.sql.SQLException;

import scw.logger.Logger;
import scw.logger.LoggerFactory;

public abstract class AbstractSqlOperations implements SqlOperations, ConnectionFactory {
	private static Logger logger = LoggerFactory.getLogger(AbstractSqlOperations.class);

	@Override
	public <T> T process(String sql, StatementProcessor<T> processor) throws SqlException {
		if (logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		try {
			return process(new ConnectionProcessor<T>() {

				@Override
				public T processConnection(Connection connection) throws SQLException {
					return SqlUtils.process(connection, sql, processor);
				}
			});
		} catch (SQLException e) {
			throw new SqlException(sql, e);
		}
	}

	@Override
	public <T> T process(String storedProcedure, CallableStatementProcessor<T> processor) throws SqlException {
		if (logger.isDebugEnabled()) {
			logger.debug(storedProcedure);
		}
		try {
			return process(new ConnectionProcessor<T>() {

				@Override
				public T processConnection(Connection connection) throws SQLException {
					return SqlUtils.process(connection, storedProcedure, processor);
				}
			});
		} catch (SQLException e) {
			throw new SqlException(storedProcedure, e);
		}
	}
}
