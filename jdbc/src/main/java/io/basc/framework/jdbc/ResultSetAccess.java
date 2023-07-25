package io.basc.framework.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.element.ElementList;
import io.basc.framework.util.element.Elements;

public class ResultSetAccess implements ObjectAccess {
	private final ResultSet resultSet;
	private final TypeDescriptor typeDescriptor;

	public ResultSetAccess(ResultSet resultSet, TypeDescriptor typeDescriptor) {
		this.resultSet = resultSet;
		this.typeDescriptor = typeDescriptor;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor;
	}

	@Override
	public Elements<String> keys() {
		try {
			ResultSetMetaData metaData = resultSet.getMetaData();
			String[] names = SqlUtils.getColumnNames(metaData, metaData.getColumnCount());
			return new ElementList<>(Arrays.asList(names));
		} catch (SQLException e) {
			throw new ConversionException(e);
		}
	}

	@Override
	public Parameter get(String name) {
		try {
			return new Parameter(name, resultSet.getObject(name));
		} catch (SQLException e) {
			// 如果字段不存在就返回空
			return null;
		}
	}

	@Override
	public void set(Parameter parameter) {
		try {
			resultSet.updateObject(parameter.getName(), parameter.getSource());
		} catch (SQLException e) {
			throw new ConversionException(e);
		}
	}

}
