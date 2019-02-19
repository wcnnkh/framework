package scw.sql.orm;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import scw.sql.ResultSetMapper;

public class ORMRowMapper<T> implements ResultSetMapper<List<T>> {
	private TableInfo tableInfo;

	public ORMRowMapper(Class<T> type, String tableName) {
		this.tableInfo = ORMUtils.getTableInfo(type);
	}

	public List<T> mapper(ResultSet resultSet) throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();
		for (int i = 1; i < metaData.getColumnCount(); i++) {
			
		}
		return null;
	}
}
