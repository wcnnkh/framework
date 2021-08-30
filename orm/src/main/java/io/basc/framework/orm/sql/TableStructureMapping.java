package io.basc.framework.orm.sql;

import io.basc.framework.sql.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface TableStructureMapping {
	Sql getSql();
	
	ColumnDescriptor getName(ResultSet resultSet) throws SQLException;
}
