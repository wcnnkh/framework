package scw.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RowMapperProcessor<T> implements SqlProcessor<ResultSet, List<T>> {
	private RowMapper<T> rowMapper;

	public RowMapperProcessor(RowMapper<T> rowMapper) {
		this.rowMapper = rowMapper;
	}

	public List<T> process(ResultSet resultSet) throws SQLException {
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
