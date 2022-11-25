package io.basc.framework.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Processor;
import io.basc.framework.util.RunnableProcessor;
import io.basc.framework.util.Source;

public class ConnectionOperations extends Operations<Connection, ConnectionOperations> {
	private static Logger logger = LoggerFactory.getLogger(ConnectionOperations.class);

	public ConnectionOperations(Source<? extends Connection, ? extends SQLException> source) {
		super(source);
	}

	public ConnectionOperations(Source<? extends Connection, ? extends SQLException> source,
			@Nullable ConsumeProcessor<? super Connection, ? extends SQLException> closeProcessor,
			@Nullable RunnableProcessor<? extends SQLException> closeHandler) {
		super(source, closeProcessor, closeHandler);
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

	public <T extends Statement> StatementOperations<T, ?> statement(
			Processor<? super Connection, ? extends T, ? extends SQLException> processor) {
		return new StatementOperations<>((operations) -> {
			Connection connection = ConnectionOperations.this.get();
			try {
				return processor.process(connection);
			} catch (Throwable e) {
				connection.close();
				throw e;
			} finally {
				operations.onClose(() -> ConnectionOperations.this.close(connection))
						.onClose(() -> ConnectionOperations.this.close());
			}
		}, (e) -> e.close(), null);
	}

	public <T extends PreparedStatement> PreparedStatementOperations<T, ?> prepare(
			Processor<? super Connection, ? extends T, ? extends SQLException> processor) {
		return new PrepareOperations<>(processor);
	}

	private class PrepareOperations<T extends PreparedStatement>
			extends PreparedStatementOperations<T, PrepareOperations<T>> {
		private final Processor<? super Connection, ? extends T, ? extends SQLException> processor;

		public PrepareOperations(Processor<? super Connection, ? extends T, ? extends SQLException> processor) {
			super((operations) -> {
				Connection connection = ConnectionOperations.this.get();
				try {
					return processor.process(connection);
				} catch (Throwable e) {
					connection.close();
					throw e;
				} finally {
					operations.onClose(() -> ConnectionOperations.this.close(connection))
							.onClose(() -> ConnectionOperations.this.close());
				}
			}, (e) -> e.close(), null);
			this.processor = processor;
		}

		@Override
		public String toString() {
			return processor.toString();
		}
	}

	public final PreparedStatementOperations<PreparedStatement, ?> prepare(Sql sql) {
		return prepare(sql, (conn, ddl) -> SqlUtils.preparedStatement(conn, ddl));
	}

	public final <T extends PreparedStatement> PreparedStatementOperations<T, ?> prepare(Sql sql,
			PreparedStatementCreator<? extends T> preparedStatementCreator) {
		return prepare(new Processor<Connection, T, SQLException>() {

			@Override
			public T process(Connection source) throws SQLException {
				if (logger.isDebugEnabled()) {
					logger.debug(SqlUtils.toString(sql));
				}
				return preparedStatementCreator.createPreparedStatement(source, sql);
			}

			@Override
			public String toString() {
				return SqlUtils.toString(sql);
			}
		});
	}

	public final PreparedStatementOperations<PreparedStatement, ?> prepare(String sql, Object... params) {
		return prepare(new SimpleSql(sql, params));
	}

	public final PreparedStatementOperations<PreparedStatement, ?> prepare(String sql) {
		return prepare(new SimpleSql(sql));
	}

	public static ConnectionOperations of(Connection connection) {
		return new ConnectionOperations(() -> connection);
	}

	public static ConnectionOperations of(Source<? extends Connection, SQLException> source) {
		return new ConnectionOperations((e) -> {
			return source.get();
		}, (e) -> e.close(), null);
	}
}
