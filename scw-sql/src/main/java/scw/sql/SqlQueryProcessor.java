package scw.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

import scw.util.stream.CallableProcessor;
import scw.util.stream.Cursor;
import scw.util.stream.Processor;

public class SqlQueryProcessor implements SqlProcessor<ResultSet> {
	private final CallableProcessor<Connection, SQLException> connectionSupplier;
	private final Processor<Connection, PreparedStatement, SQLException> preparedStatementCreator;
	private final Supplier<String> desc;
	private final boolean closeConnection;

	public SqlQueryProcessor(CallableProcessor<Connection, SQLException> connectionSupplier, boolean closeConnection,
			Processor<Connection, PreparedStatement, SQLException> preparedStatementCreator, Supplier<String> desc) {
		this.connectionSupplier = connectionSupplier;
		this.preparedStatementCreator = preparedStatementCreator;
		this.desc = desc;
		this.closeConnection = closeConnection;
	}

	@Override
	public <T> T process(Processor<ResultSet, ? extends T, ? extends Throwable> processor) throws SqlException {
		Connection connection = null;
		try {
			connection = connectionSupplier.process();
			return SqlUtils.query(connection, preparedStatementCreator, (ps) -> ps.executeQuery(), processor);
		} catch (Throwable e) {
			throw SqlUtils.throwableSqlException(e, desc);
		} finally {
			close(connection);
		}
	}

	public <V> Cursor<V> stream(Processor<ResultSet, ? extends V, ? extends Throwable> processor) throws SqlException {
		Connection connection;
		try {
			connection = connectionSupplier.process();
		} catch (Throwable e) {
			throw SqlUtils.throwableSqlException(e, desc);
		}

		Cursor<V> rows;
		try {
			rows = SqlUtils.query(connection, preparedStatementCreator, (ps) -> ps.executeQuery(), processor, desc);
			return rows.onClose(() -> close(connection));
		} catch (Throwable e) {
			close(connection);
			throw SqlUtils.throwableSqlException(e, desc);
		}
	}

	private void close(Connection connection) {
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

	public void process(RowCallback callback) throws SqlException {
		process(new ResultSetRowCallbackProcessor(callback));
	}

	public <T> List<T> process(RowMapper<T> rowMapper) throws SqlException {
		return process(new ResultSetRowMapperProcessor<T>(rowMapper));
	}
}
