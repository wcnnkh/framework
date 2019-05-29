package scw.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowCallback {
	boolean processRow(ResultSet rs, int rowNum) throws SQLException;
}
