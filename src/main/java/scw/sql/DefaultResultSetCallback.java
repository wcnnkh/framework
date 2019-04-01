package scw.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class DefaultResultSetCallback implements ResultSetCallback {
	private final RowCallback rowCallback;

	public DefaultResultSetCallback(RowCallback rowCallback) {
		this.rowCallback = rowCallback;
	}

	public void process(ResultSet rs) throws SQLException {
		for (int i = 1; rs.next(); i++) {
			rowCallback.processRow(rs, i);
		}
	}

}
