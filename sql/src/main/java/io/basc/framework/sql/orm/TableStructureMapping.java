package io.basc.framework.sql.orm;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.basc.framework.sql.Sql;

public interface TableStructureMapping {
	Sql getSql();

	Column getName(ResultSet resultSet) throws SQLException;
}
