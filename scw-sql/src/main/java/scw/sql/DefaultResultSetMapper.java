package scw.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DefaultResultSetMapper<T> implements ResultSetMapper<List<T>> {
	private RowMapper<T> rowMapper;

	public DefaultResultSetMapper(RowMapper<T> rowMapper) {
		this.rowMapper = rowMapper;
	}

	public List<T> mapper(ResultSet resultSet) throws SQLException {
		List<T> list = new ArrayList<T>();
		for (int i = 1; resultSet.next(); i++) {
			T t = rowMapper.mapRow(resultSet, i);
			if (t != null) {
				list.add(t);
			}
		}
		return list;
	}

}
