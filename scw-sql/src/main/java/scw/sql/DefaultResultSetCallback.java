package scw.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class DefaultResultSetCallback implements SqlCallback<ResultSet> {
	private final RowCallback rowCallback;

	public DefaultResultSetCallback(RowCallback rowCallback) {
		this.rowCallback = rowCallback;
	}

	@Override
	public void call(ResultSet rs) throws SQLException {
		for (int i = 1; rs.next(); i++) {
			if (!rowCallback.processRow(rs, i)) {
				break;
			}
		}
	}

}
