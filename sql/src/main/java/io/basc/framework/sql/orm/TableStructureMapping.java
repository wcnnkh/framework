package io.basc.framework.sql.orm;

import io.basc.framework.sql.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface TableStructureMapping {
	Sql getSql();
	
	ColumnDescriptor getName(ResultSet resultSet) throws SQLException;
}
