package scw.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import scw.util.stream.Callback;

public final class ResultSetRowCallbackProcessor implements Callback<ResultSet, SQLException> {
	private final RowCallback rowCallback;

	public ResultSetRowCallbackProcessor(RowCallback rowCallback) {
		this.rowCallback = rowCallback;
	}

	@Override
	public void call(ResultSet rs) throws SQLException {
		for (int i = 1; rs.next(); i++) {
			rowCallback.processRow(rs, i);
		}
	}
}
