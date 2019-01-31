package scw.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowCallback {
	void processRow(ResultSet rs, int rowNum) throws SQLException;
}
