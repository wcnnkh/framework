package run.soeasy.framework.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import run.soeasy.framework.lang.Nullable;
import run.soeasy.framework.util.function.Consumer;
import run.soeasy.framework.util.function.Function;
import run.soeasy.framework.util.function.Runnable;
import run.soeasy.framework.util.function.Supplier;
import run.soeasy.framework.util.logging.LogManager;
import run.soeasy.framework.util.logging.Logger;

public class ConnectionOperations extends Operations<Connection, ConnectionOperations> {
	private static Logger logger = LogManager.getLogger(ConnectionOperations.class);

	public static ConnectionOperations of(Connection connection) {
		return new ConnectionOperations(() -> connection);
	}

	public static ConnectionOperations of(Supplier<? extends Connection, SQLException> source) {
		return new ConnectionOperations((e) -> {
			return source.get();
		}, (e) -> e.close(), null);
	}

	public ConnectionOperations(
			Function<? super ConnectionOperations, ? extends Connection, ? extends SQLException> sourceProcesor) {
		super(sourceProcesor);
	}

	public ConnectionOperations(
			Function<? super ConnectionOperations, ? extends Connection, ? extends SQLException> sourceProcesor,
			@Nullable Consumer<? super Connection, ? extends SQLException> closeProcessor,
			@Nullable Runnable<? extends SQLException> closeHandler) {
		super(sourceProcesor, closeProcessor, closeHandler);
	}

	public ConnectionOperations(Supplier<? extends Connection, ? extends SQLException> source) {
		super(source);
	}

	public ConnectionOperations(Supplier<? extends Connection, ? extends SQLException> source,
			@Nullable Consumer<? super Connection, ? extends SQLException> closeProcessor,
			@Nullable Runnable<? extends SQLException> closeHandler) {
		super(source, closeProcessor, closeHandler);
	}

	public <T extends PreparedStatement> PreparedStatementOperations<T, ?> prepare(
			Function<? super Connection, ? extends T, ? extends SQLException> processor) {
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
			Function<? super Connection, ? extends T, ? extends SQLException> processor) {
		return new StatementOperations<>(this, processor, (e) -> e.close(), null);
	}
}
