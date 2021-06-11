package scw.orm.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import scw.sql.Sql;

public interface TableStructureMapping {
	Sql getSql();
	
	String getName(ResultSet resultSet) throws SQLException;
}
