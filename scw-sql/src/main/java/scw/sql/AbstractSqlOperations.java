package scw.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import scw.logger.Logger;
import scw.logger.LoggerFactory;

public abstract class AbstractSqlOperations implements SqlOperations, ConnectionFactory {
	private static Logger logger = LoggerFactory.getLogger(AbstractSqlOperations.class);

	@Override
	public <T> T process(String sql, PreparedStatementProcessor<T> processor) throws SqlException {
		if (logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		try {
			return process(new ConnectionProcessor<T>() {

				@Override
				public T processConnection(Connection connection) throws SQLException {
					PreparedStatement ps = null;
					try {
						ps = connection.prepareStatement(sql);
						return processor.processPreparedStatement(ps);
					} finally {
						if (ps != null && !ps.isClosed()) {
							ps.close();
						}
					}
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
					CallableStatement cs = null;
					try {
						cs = connection.prepareCall(storedProcedure);
						return processor.processCallableStatement(cs);
					} finally {
						if (cs != null && !cs.isClosed()) {
							cs.close();
						}
					}
				}
			});
		} catch (SQLException e) {
			throw new SqlException(storedProcedure, e);
		}
	}
}
