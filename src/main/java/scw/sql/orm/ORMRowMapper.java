package scw.sql.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import scw.sql.ResultSetMapper;
import scw.sql.orm.result.DefaultResultSet;

public class ORMRowMapper<T> implements ResultSetMapper<List<T>> {
	private final Class<T> type;

	public ORMRowMapper(Class<T> type) {
		this.type = type;
	}

	public List<T> mapper(ResultSet resultSet) throws SQLException {
		DefaultResultSet rs = new DefaultResultSet(resultSet);
		return rs.getList(type);
	}
}
