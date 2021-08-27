package io.basc.framework.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowCallback {
	void processRow(ResultSet rs, int rowNum) throws SQLException;
}
