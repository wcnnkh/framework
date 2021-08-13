package scw.orm.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import scw.convert.TypeDescriptor;

@FunctionalInterface
public interface ResultSetMapper {
	Object map(TypeDescriptor type, ResultSet resultSet) throws SQLException;
}
