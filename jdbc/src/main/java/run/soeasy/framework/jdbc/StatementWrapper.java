package run.soeasy.framework.jdbc;

import java.sql.SQLException;
import java.sql.Statement;

import run.soeasy.framework.core.function.stream.Source;

public class StatementWrapper<T extends Statement> extends JdbcWrapper<T> {

	public StatementWrapper(Source<T, SQLException> source) {
		super(source);
	}

	public StatementWrapper<T> clearBatch() {
		return new StatementWrapper<T>(map((e) -> {
			e.clearBatch();
			return e;
		}));
	}

	public int[] executeBatch() throws SQLException {
		return optional().map((e) -> e.executeBatch()).get();
	}

	public long[] executeLargeBatch() throws SQLException, UnsupportedOperationException {
		return optional().map((e) -> e.executeLargeBatch()).get();
	}
}
