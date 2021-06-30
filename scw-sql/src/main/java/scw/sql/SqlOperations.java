package scw.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import scw.util.stream.Cursor;
import scw.util.stream.Processor;

public interface SqlOperations extends ConnectionFactory, SqlStatementProcessor {

	default PreparedStatementProcessor prepare(Connection connection, Sql sql,
			SqlStatementProcessor statementProcessor) {
		return SqlUtils.prepare(connection, sql, statementProcessor);
	}

	default PreparedStatementProcessor prepare(Sql sql, SqlStatementProcessor statementProcessor) {
		return SqlUtils.prepare(this, sql, statementProcessor);
	}

	default PreparedStatementProcessor prepare(Connection connection, Sql sql) {
		return prepare(connection, sql, this);
	}

	default PreparedStatementProcessor prepare(Sql sql) {
		return prepare(sql, this);
	}

	/**
	 * 返回受影响的行数
	 * 
	 * @see #prepare(Sql)
	 * @param sql
	 * @return
	 * @throws SqlException
	 */
	default int update(Sql sql) throws SqlException {
		return prepare(sql).update();
	}

	/**
	 * @see #prepare(Sql)
	 * @return 返回结果并不代表是否执行成功，意义请参考jdk文档<br/>
	 *         true if the first result is a ResultSet object; false if the first
	 *         result is an update count or there is no result
	 * @throws SqlException
	 */
	default boolean execute(Sql sql) throws SqlException {
		return prepare(sql).execute();
	}

	default <T> Cursor<T> query(Connection connection, Sql sql, SqlStatementProcessor statementProcessor,
			Processor<ResultSet, ? extends T, ? extends Throwable> processor) {
		return prepare(connection, sql, statementProcessor).query().stream(processor);
	}

	default <T> Cursor<T> query(Connection connection, Sql sql,
			Processor<ResultSet, ? extends T, ? extends Throwable> processor) {
		return prepare(connection, sql).query().stream(processor);
	}

	default <T> Cursor<T> query(Sql sql, SqlStatementProcessor statementProcessor,
			Processor<ResultSet, ? extends T, ? extends Throwable> processor) {
		return prepare(sql, statementProcessor).query().stream(processor);
	}

	default <T> Cursor<T> query(Sql sql, Processor<ResultSet, ? extends T, ? extends Throwable> processor) {
		return prepare(sql).query().stream(processor);
	}

	default int[] executeBatch(Connection connection, String sql, Collection<Object[]> batchArgs) throws SqlException {
		try {
			return SqlUtils.executeBatch(connection, (conn) -> this.statement(conn, new SimpleSql(sql)), batchArgs);
		} catch (Throwable e) {
			if (e instanceof SqlException) {
				throw (SqlException) e;
			}
			throw new SqlException(sql, e);
		}
	}

	default int[] executeBatch(String sql, Collection<Object[]> batchArgs) throws SqlException {
		try {
			return process(new Processor<Connection, int[], SQLException>() {

				@Override
				public int[] process(Connection connection) throws SQLException {
					return executeBatch(connection, sql, batchArgs);
				}
			});
		} catch (Throwable e) {
			if (e instanceof SqlException) {
				throw (SqlException) e;
			}
			throw new SqlException(sql, e);
		}
	}
}
