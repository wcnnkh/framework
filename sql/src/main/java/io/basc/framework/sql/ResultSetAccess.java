package io.basc.framework.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;

public class ResultSetAccess implements ObjectAccess<SQLException> {
	private final ResultSet resultSet;

	public ResultSetAccess(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	@Override
	public Enumeration<String> keys() throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();
		String[] names = SqlUtils.getColumnNames(metaData, metaData.getColumnCount());
		return Collections.enumeration(Arrays.asList(names));
	}

	@Override
	public Value get(String name) throws SQLException {
		try {
			return new AnyValue(resultSet.getObject(name));
		} catch (SQLException e) {
			// 如果字段不存在就返回空
			return null;
		}
	}

	@Override
	public void set(String name, Value value) throws SQLException {
		resultSet.updateObject(name, value.get());
	}

}
