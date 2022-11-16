package io.basc.framework.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

import io.basc.framework.util.Cursor;
import io.basc.framework.util.Processor;
import io.basc.framework.util.Source;

public class ResultSetOperations extends Operations<ResultSet, ResultSetOperations> {

	public ResultSetOperations(Source<? extends ResultSet, ? extends SQLException> source) {
		super(source);
	}

	public <E> Cursor<E> map(Processor<? super ResultSet, ? extends E, ? extends Throwable> rowMapper)
			throws SQLException {
		ResultSet resultSet = get();
		Stream<E> stream = SqlUtils.stream(resultSet, rowMapper, () -> ResultSetOperations.this.toString());
		return Cursor.create(stream.onClose(() -> {
			try {
				close(resultSet);
			} catch (SQLException e) {
				throw SqlUtils.throwableSqlException(e, () -> ResultSetOperations.this.toString());
			}
		})).onClose(() -> {
			try {
				close();
			} catch (SQLException e) {
				throw SqlUtils.throwableSqlException(e, () -> ResultSetOperations.this.toString());
			}
		});
	}
}
