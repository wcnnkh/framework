package scw.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetCallback {

	void process(ResultSet rs) throws SQLException;
}
