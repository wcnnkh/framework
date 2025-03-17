package run.soeasy.framework.jdbc;

import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.data.domain.Query;
import run.soeasy.framework.orm.EntityMapper;

@SuppressWarnings("unchecked")
public interface JdbcOperations extends ConnectionFactory {

	default boolean execute(Sql sql) {
		return process(sql, (e) -> e.execute());
	}

	default boolean execute(String sql) {
		return execute(new SimpleSql(sql));
	}

	default boolean execute(String sql, Object... sqlParams) {
		return execute(new SimpleSql(sql, sqlParams));
	}

	EntityMapper getMapper();

	default <T> Query<T> query(Class<? extends T> resultType, Sql sql) {
		return query(sql, (rs) -> getMapper().convert(rs, resultType));
	}

	default <T> Query<T> query(Class<? extends T> resultType, String sql) {
		return query(resultType, new SimpleSql(sql));
	}

	default <T> Query<T> query(Class<? extends T> resultType, String sql, Object... sqlParams) {
		return query(resultType, new SimpleSql(sql, sqlParams));
	}

	default <T> Query<T> query(TypeDescriptor resultType, Sql sql) {
		return query(sql, (rs) -> (T) getMapper().convert(rs, resultType));
	}

	default <T> Query<T> query(TypeDescriptor resultType, String sql) {
		return query(resultType, new SimpleSql(sql));
	}

	default <T> Query<T> query(TypeDescriptor resultType, String sql, Object... sqlParams) {
		return query(resultType, new SimpleSql(sql, sqlParams));
	}

	default int update(Sql sql) {
		return process(sql, (e) -> e.executeUpdate());
	}

	default int update(String sql) throws SqlException {
		return update(new SimpleSql(sql));
	}

	default int update(String sql, Object... sqlParams) throws SqlException {
		return update(sql, sqlParams);
	}
}
