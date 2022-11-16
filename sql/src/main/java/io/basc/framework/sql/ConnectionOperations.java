package io.basc.framework.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Processor;
import io.basc.framework.util.Source;

public class ConnectionOperations extends Operations<Connection, ConnectionOperations> {
	private static Logger logger = LoggerFactory.getLogger(ConnectionOperations.class);

	public ConnectionOperations(Source<? extends Connection, ? extends SQLException> source) {
		super(source);
	}

	public <T extends Statement> StatementOperations<T, ?> statement(
			Processor<? super Connection, ? extends T, ? extends SQLException> processor) throws SQLException {
		Connection connection = get();
		StatementOperations<T, ?> statementStreamProcessor = new StatementOperations<>(
				() -> processor.process(connection));
		return statementStreamProcessor.onClose((e) -> e.close()).onClose(() -> close(connection))
				.onClose(() -> close());
	}

	public final PreparedStatementOperations<PreparedStatement, ?> prepare(Sql sql) throws SQLException {
		return prepare(sql, (conn, ddl) -> SqlUtils.preparedStatement(conn, ddl));
	}

	public PreparedStatementOperations<PreparedStatement, ?> prepare(Sql sql,
			PreparedStatementCreator preparedStatementCreator) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug(SqlUtils.toString(sql));
		}
		Connection connection = get();
		PreparedStatementOperations<PreparedStatement, ?> statementStreamProcessor = new PreparedStatementOperations<>(
				() -> preparedStatementCreator.createPreparedStatement(connection, sql));
		return statementStreamProcessor.onClose((e) -> e.close()).onClose(() -> close(connection))
				.onClose(() -> close());
	}

	public final PreparedStatementOperations<PreparedStatement, ?> prepare(String sql, Object... params)
			throws SQLException {
		return prepare(new SimpleSql(sql, params));
	}

	public final PreparedStatementOperations<PreparedStatement, ?> prepare(String sql) throws SQLException {
		return prepare(new SimpleSql(sql));
	}
}
