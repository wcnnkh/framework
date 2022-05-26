package io.basc.framework.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.orm.support.AbstractObjectMapper;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.value.Value;

public class ResultSetMapper extends AbstractObjectMapper<ResultSet, SQLException> {

	@SuppressWarnings("unchecked")
	@Override
	public <R> R convert(ResultSet source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws SQLException, ConverterNotFoundException {
		if (!isConverterRegistred(targetType.getType())) {
			if (ResultSet.class == targetType.getType()) {
				return (R) source;
			}

			if (targetType.isArray() || targetType.isCollection()) {
				Object[] array = SqlUtils.getRowValues(source, source.getMetaData().getColumnCount());
				return getConversionService().convert(array, targetType);
			}

			if (!Value.isBaseType(targetType.getType()) && targetType.getType() != Object.class
					&& (isEntity(targetType.getType()) || ReflectionUtils.isInstance(targetType.getType()))) {
				return super.convert(source, sourceType, targetType);
			}

			int columnCount = source.getMetaData().getColumnCount();
			if (columnCount == 0) {
				return null;
			}

			Object value = source.getObject(1);
			return getConversionService().convert(value, targetType);
		}
		return super.convert(source, sourceType, targetType);
	}

	@Override
	protected Processor<String, Object, SQLException> getValueProcessor(ResultSet source, TypeDescriptor sourceType) {
		return (name) -> {
			try {
				return source.getObject(name);
			} catch (SQLException e) {
				// 如果字段不存在就返回空
				return null;
			}
		};
	}
	
	@Override
	protected void writeValue(Object value, ParameterDescriptor descriptor, ResultSet target) throws SQLException {
		target.updateObject(descriptor.getName(), value);
	}
}
