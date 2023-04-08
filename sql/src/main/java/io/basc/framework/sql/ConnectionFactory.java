package io.basc.framework.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Processor;

@FunctionalInterface
public interface ConnectionFactory {
	Connection getConnection() throws SQLException;

	default ConnectionOperations operations() {
		return ConnectionOperations.of(() -> getConnection());
	}

	default <T> T process(Sql sql,
			Processor<? super PreparedStatement, ? extends T, ? extends SQLException> processor) {
		return operations().prepare(sql).process(processor);
	}

	default void consume(Sql sql, ConsumeProcessor<? super PreparedStatement, ? extends SQLException> processor) {
		operations().prepare(sql).consume(processor);
	}

	default <T> Elements<T> query(Sql sql,
			Processor<? super java.sql.ResultSet, ? extends T, ? extends Throwable> rowMapper) {
		return operations().prepare(sql).query().rows(rowMapper);
	}
}
