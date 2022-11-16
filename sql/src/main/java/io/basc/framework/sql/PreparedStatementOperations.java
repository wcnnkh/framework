package io.basc.framework.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Source;
import io.basc.framework.util.StreamOperations;

public class PreparedStatementOperations<T extends PreparedStatement, C extends PreparedStatementOperations<T, C>>
		extends StatementOperations<T, C> {

	public PreparedStatementOperations(Source<? extends T, ? extends SQLException> source) {
		super(source);
	}

	/**
	 * @return 返回结果并不代表是否执行成功，意义请参考jdk文档<br/>
	 *         true if the first result is a ResultSet object; false if the first
	 *         result is an update count or there is no result
	 * @throws SQLException
	 */
	public boolean execute() throws SQLException {
		return process((e) -> e.execute());
	}

	public int executeUpdate() throws SQLException {
		return process((e) -> e.executeUpdate());
	}

	public final ResultSetOperations query() throws SQLException {
		return query((e) -> e.executeQuery());
	}

	public PreparedStatementOperations<T, C> batch(ConsumeProcessor<? super T, ? extends SQLException> batchProcessor) {
		StreamOperations<T, SQLException> stream = stream((e) -> {
			batchProcessor.process(e);
			return e;
		}).onClose((e) -> e.addBatch());
		PreparedStatementOperations<T, C> opeations = new PreparedStatementOperations<>(stream);
		return opeations.onClose((e) -> stream.close(e));
	}

	public PreparedStatementOperations<T, C> batch(Object... args) {
		return batch((e) -> SqlUtils.setSqlParams(e, args));
	}
}
