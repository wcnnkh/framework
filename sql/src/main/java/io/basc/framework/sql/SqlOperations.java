package io.basc.framework.sql;

import java.sql.SQLException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.util.page.Paginations;

@SuppressWarnings("unchecked")
public interface SqlOperations extends ConnectionFactory {

	default boolean execute(Sql sql) {
		return process(sql, (e) -> e.execute());
	}

	default boolean execute(String sql) {
		return execute(new SimpleSql(sql));
	}

	default boolean execute(String sql, Object... sqlParams) {
		return execute(new SimpleSql(sql, sqlParams));
	}

	ObjectMapper<java.sql.ResultSet, SQLException> getMapper();

	default <T> Paginations<T> query(Class<? extends T> resultType, Sql sql) {
		return new Query<>(this, sql, (rs) -> getMapper().convert(rs, resultType));
	}

	default <T> Paginations<T> query(Class<? extends T> resultType, String sql) {
		return query(resultType, new SimpleSql(sql));
	}

	default <T> Paginations<T> query(Class<? extends T> resultType, String sql, Object... sqlParams) {
		return query(resultType, new SimpleSql(sql, sqlParams));
	}

	default <T> Paginations<T> query(TypeDescriptor resultType, Sql sql) {
		return new Query<>(this, sql, (rs) -> (T) getMapper().convert(rs, resultType));
	}

	default <T> Paginations<T> query(TypeDescriptor resultType, String sql) {
		return query(resultType, new SimpleSql(sql));
	}

	default <T> Paginations<T> query(TypeDescriptor resultType, String sql, Object... sqlParams) {
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
