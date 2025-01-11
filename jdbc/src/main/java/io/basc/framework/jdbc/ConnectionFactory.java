package io.basc.framework.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import io.basc.framework.data.domain.Query;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.function.Consumer;
import io.basc.framework.util.function.Function;

@FunctionalInterface
public interface ConnectionFactory {
	Connection getConnection() throws SQLException;

	default ConnectionOperations operations() {
		return ConnectionOperations.of(() -> getConnection());
	}

	default <T> T process(Sql sql,
			Function<? super PreparedStatement, ? extends T, ? extends SQLException> processor) {
		return operations().prepare(sql).process(processor);
	}

	default void consume(Sql sql, Consumer<? super PreparedStatement, ? extends SQLException> processor) {
		operations().prepare(sql).consume(processor);
	}

	default <T> Query<T> query(Sql sql,
			Function<? super java.sql.ResultSet, ? extends T, ? extends Throwable> rowMapper) {
		Elements<T> elements = operations().prepare(sql).query().rows(rowMapper);
		// 数据库操作是耗时的，启用缓存
		return new Query<>(elements.cacheable());
	}
}
