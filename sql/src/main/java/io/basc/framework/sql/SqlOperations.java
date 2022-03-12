package io.basc.framework.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.MapProcessDecorator;
import io.basc.framework.mapper.Mapper;
import io.basc.framework.orm.transfer.ExportProcessor;
import io.basc.framework.orm.transfer.Exporter;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.Processor;

public interface SqlOperations extends ConnectionFactory, SqlStatementProcessor, MapProcessorFactory {

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
	default long update(Sql sql) throws SqlException {
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
			throw new SqlException(sql, e);
		}
	}

	Mapper<ResultSet, ? extends Throwable> getMapper();

	@Override
	public default <T> Processor<ResultSet, T, ? extends Throwable> getMapProcessor(Class<? extends T> type) {
		if (getMapper().isRegistred(type)) {
			return getMapper().getProcessor(type);
		}
		return MapProcessorFactory.super.getMapProcessor(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	default <T> Processor<ResultSet, T, Throwable> getMapProcessor(TypeDescriptor type) {
		return new MapProcessDecorator<>(getMapper(), new ResultSetMapProcessor<>(type), (Class<T>) type.getType());
	}

	default <T> Cursor<T> query(Connection connection, TypeDescriptor resultType, Sql sql,
			SqlStatementProcessor statementProcessor) {
		return prepare(connection, sql, statementProcessor).query().stream(getMapProcessor(resultType));
	}

	default <T> Cursor<T> query(Connection connection, TypeDescriptor resultType, Sql sql) {
		return prepare(connection, sql).query().stream(getMapProcessor(resultType));
	}

	default <T> Cursor<T> query(TypeDescriptor resultType, Sql sql, SqlStatementProcessor statementProcessor) {
		return prepare(sql, statementProcessor).query().stream(getMapProcessor(resultType));
	}

	default <T> Cursor<T> query(TypeDescriptor resultType, Sql sql) {
		return prepare(sql).query().stream(getMapProcessor(resultType));
	}

	default <T> Cursor<T> query(Connection connection, Class<? extends T> resultType, Sql sql,
			SqlStatementProcessor statementProcessor) {
		return query(connection, sql, statementProcessor, getMapProcessor(resultType));
	}

	default <T> Cursor<T> query(Connection connection, Class<? extends T> resultType, Sql sql) {
		return query(connection, sql, getMapProcessor(resultType));
	}

	default <T> Cursor<T> query(Class<? extends T> resultType, Sql sql, SqlStatementProcessor statementProcessor) {
		return query(sql, statementProcessor, getMapProcessor(resultType));
	}

	default <T> Cursor<T> query(Class<? extends T> resultType, Sql sql) {
		return query(sql, getMapProcessor(resultType));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	default Exporter export(Sql sql, TypeDescriptor type, ExportProcessor<?> processor) {
		return (file) -> {
			Cursor<Object> cursor = query(type, sql);
			try {
				processor.process((Iterator) cursor.iterator(), file);
			} finally {
				cursor.close();
			}
		};
	}

	default <T> Exporter export(Sql sql, Class<T> type, ExportProcessor<? super T> processor) {
		return export(sql, TypeDescriptor.valueOf(type), processor);
	}
}
