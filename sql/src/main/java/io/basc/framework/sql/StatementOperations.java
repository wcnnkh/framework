package io.basc.framework.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.basc.framework.util.Processor;
import io.basc.framework.util.Source;

public class StatementOperations<T extends Statement, C extends StatementOperations<T, C>>
		extends Operations<T, C> {

	public StatementOperations(Source<? extends T, ? extends SQLException> source) {
		super(source);
	}

	public ResultSetOperations query(
			Processor<? super T, ? extends ResultSet, ? extends SQLException> queryProcessor) throws SQLException {
		T statement = get();
		ResultSetOperations streamProcessor = new ResultSetOperations(
				() -> queryProcessor.process(statement)).onClose((e) -> e.close());
		return streamProcessor.onClose(() -> close(statement)).onClose(() -> close());
	}
}
