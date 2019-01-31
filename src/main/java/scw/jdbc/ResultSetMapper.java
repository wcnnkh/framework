package scw.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetMapper<T> {

	T mapper(ResultSet resultSet) throws SQLException;
}
