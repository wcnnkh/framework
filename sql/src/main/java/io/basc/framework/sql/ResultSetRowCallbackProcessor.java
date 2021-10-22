package io.basc.framework.sql;

import io.basc.framework.util.stream.ConsumerProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ResultSetRowCallbackProcessor implements ConsumerProcessor<ResultSet, SQLException> {
	private final RowCallback rowCallback;

	public ResultSetRowCallbackProcessor(RowCallback rowCallback) {
		this.rowCallback = rowCallback;
	}

	@Override
	public void process(ResultSet rs) throws SQLException {
		for (int i = 1; rs.next(); i++) {
			rowCallback.processRow(rs, i);
		}
	}
}
