package io.basc.framework.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Processor;
import io.basc.framework.util.ResultSet;

@FunctionalInterface
public interface ConnectionFactory {
	Connection getConnection() throws SQLException;

	default ConnectionOperations operations() {
		return new ConnectionOperations(() -> getConnection());
	}

	default <T> T process(Sql sql,
			Processor<? super PreparedStatement, ? extends T, ? extends SQLException> processor) {
		try {
			return operations().prepare(sql).process(processor);
		} catch (SQLException e) {
			throw SqlUtils.throwableSqlException(e, () -> SqlUtils.toString(sql));
		}
	}

	default void consume(Sql sql, ConsumeProcessor<? super PreparedStatement, ? extends SQLException> processor) {
		try {
			operations().prepare(sql).consume(processor);
		} catch (SQLException e) {
			throw SqlUtils.throwableSqlException(e, () -> SqlUtils.toString(sql));
		}
	}

	default <T> ResultSet<T> query(Sql sql,
			Processor<? super java.sql.ResultSet, ? extends T, ? extends Throwable> rowMapper) {
		return ResultSet.of(() -> {
			try {
				return operations().prepare(sql).query().rows(rowMapper);
			} catch (SQLException e) {
				throw SqlUtils.throwableSqlException(e, () -> SqlUtils.toString(sql));
			}
		});
	}
}
