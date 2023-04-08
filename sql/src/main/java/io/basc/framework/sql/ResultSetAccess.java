package io.basc.framework.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.ElementList;
import io.basc.framework.util.Elements;

public class ResultSetAccess implements ObjectAccess<SQLException> {
	private final ResultSet resultSet;

	public ResultSetAccess(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	@Override
	public Elements<String> keys() throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();
		String[] names = SqlUtils.getColumnNames(metaData, metaData.getColumnCount());
		return new ElementList<>(Arrays.asList(names));
	}

	@Override
	public Parameter get(String name) throws SQLException {
		try {
			return new Parameter(name, resultSet.getObject(name));
		} catch (SQLException e) {
			// 如果字段不存在就返回空
			return null;
		}
	}

	@Override
	public void set(Parameter parameter) throws SQLException {
		resultSet.updateObject(parameter.getName(), parameter.getSource());
	}

}
