package scw.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import scw.util.stream.Processor;

public class ResultSetRowMapperProcessor<T> implements Processor<ResultSet, List<T>, SQLException> {
	private RowMapper<T> rowMapper;

	public ResultSetRowMapperProcessor(RowMapper<T> rowMapper) {
		this.rowMapper = rowMapper;
	}

	public List<T> process(ResultSet resultSet) throws SQLException {
		List<T> list = new ArrayList<T>();
		for (int i = 1; resultSet.next(); i++) {
			T t = rowMapper.mapRow(resultSet, i);
			list.add(t);
		}
		return list;
	}

}
