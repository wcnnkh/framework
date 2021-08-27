package io.basc.framework.sql;

import io.basc.framework.util.stream.CallableProcessor;
import io.basc.framework.util.stream.Processor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Supplier;

public class PreparedStatementProcessor implements SqlProcessor<PreparedStatement> {
	private final CallableProcessor<Connection, SQLException> connectionSupplier;
	private final Processor<Connection, PreparedStatement, SQLException> preparedStatementCreator;
	private final Supplier<String> desc;
	private final boolean closeConnection;

	public PreparedStatementProcessor(CallableProcessor<Connection, SQLException> connectionSupplier,
			boolean closeConnection, Processor<Connection, PreparedStatement, SQLException> preparedStatementCreator,
			Supplier<String> desc) {
		this.connectionSupplier = connectionSupplier;
		this.preparedStatementCreator = preparedStatementCreator;
		this.desc = desc;
		this.closeConnection = closeConnection;
	}

	@Override
	public <T> T process(Processor<PreparedStatement, ? extends T, ? extends Throwable> processor) throws SqlException {
		Connection connection = null;
		try {
			connection = connectionSupplier.process();
			return SqlUtils.process(connection, preparedStatementCreator, processor);
		} catch (Throwable e) {
			throw SqlUtils.throwableSqlException(e, desc);
		} finally {
			if (closeConnection) {
				try {
					if (connection != null && !connection.isClosed()) {
						connection.close();
					}
				} catch (Throwable e) {
					throw SqlUtils.throwableSqlException(e, desc);
				}
			}
		}
	}

	public SqlQueryProcessor query() throws SqlException {
		return new SqlQueryProcessor(connectionSupplier, closeConnection, preparedStatementCreator, desc);
	}

	/**
	 * @return 返回结果并不代表是否执行成功，意义请参考jdk文档<br/>
	 *         true if the first result is a ResultSet object; false if the first
	 *         result is an update count or there is no result
	 * @throws SqlException
	 */
	public boolean execute() throws SqlException {
		return process((ps) -> (Boolean) ps.execute());
	}

	public int update() throws SqlException {
		return process((ps) -> (int) ps.executeUpdate());
	}
}
