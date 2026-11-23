package run.soeasy.framework.data.jdbc;

import java.sql.ResultSet;

public interface ResultSetConverter<T> {
	T row(int row, ResultSet resultSet);
}
