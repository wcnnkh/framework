package io.basc.framework.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.function.ConsumeProcessor;
import io.basc.framework.util.function.Processor;
import io.basc.framework.util.function.RunnableProcessor;
import io.basc.framework.util.function.Source;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;

public class ConnectionOperations extends Operations<Connection, ConnectionOperations> {
	private static Logger logger = LoggerFactory.getLogger(ConnectionOperations.class);

	public static ConnectionOperations of(Connection connection) {
		return new ConnectionOperations(() -> connection);
	}

	public static ConnectionOperations of(Source<? extends Connection, SQLException> source) {
		return new ConnectionOperations((e) -> {
			return source.get();
		}, (e) -> e.close(), null);
	}

	public ConnectionOperations(
			Processor<? super ConnectionOperations, ? extends Connection, ? extends SQLException> sourceProcesor) {
		super(sourceProcesor);
	}

	public ConnectionOperations(
			Processor<? super ConnectionOperations, ? extends Connection, ? extends SQLException> sourceProcesor,
			@Nullable ConsumeProcessor<? super Connection, ? extends SQLException> closeProcessor,
			@Nullable RunnableProcessor<? extends SQLException> closeHandler) {
		super(sourceProcesor, closeProcessor, closeHandler);
	}

	public ConnectionOperations(Source<? extends Connection, ? extends SQLException> source) {
		super(source);
	}

	public ConnectionOperations(Source<? extends Connection, ? extends SQLException> source,
			@Nullable ConsumeProcessor<? super Connection, ? extends SQLException> closeProcessor,
			@Nullable RunnableProcessor<? extends SQLException> closeHandler) {
		super(source, closeProcessor, closeHandler);
	}

	public <T extends PreparedStatement> PreparedStatementOperations<T, ?> prepare(
			Processor<? super Connection, ? extends T, ? extends SQLException> processor) {
		return new PreparedStatementOperations<>(this, processor, (e) -> e.close(), null);
	}

	public final PreparedStatementOperations<PreparedStatement, ?> prepare(Sql sql) {
		return prepare(sql, (conn, ddl) -> SqlUtils.preparedStatement(conn, ddl));
	}

	public final <T extends PreparedStatement> PreparedStatementOperations<T, ?> prepare(Sql sql,
			PreparedStatementCreator<? extends T> preparedStatementCreator) {
		PreparedStatementOperations<T, ?> preparedStatementOperations = prepare((source) -> {
			if (logger.isDebugEnabled()) {
				logger.debug(SqlUtils.toString(sql));
			}
			return preparedStatementCreator.createPreparedStatement(source, sql);
		});

		preparedStatementOperations.setToString(() -> SqlUtils.toString(sql));
		return preparedStatementOperations;
	}

	public final PreparedStatementOperations<PreparedStatement, ?> prepare(String sql) {
		return prepare(new SimpleSql(sql));
	}

	public final PreparedStatementOperations<PreparedStatement, ?> prepare(String sql, Object... params) {
		return prepare(new SimpleSql(sql, params));
	}

	public <T extends Statement, R extends StatementOperations<T, R>> StatementOperations<T, R> statement(
			Processor<? super Connection, ? extends T, ? extends SQLException> processor) {
		return new StatementOperations<>(this, processor, (e) -> e.close(), null);
	}
}
