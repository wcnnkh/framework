package io.basc.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.orm.support.DefaultEntityMapper;

public class ResultSetMapper extends DefaultEntityMapper {

	public ResultSetMapper() {
		registerPropertiesTransformer(ResultSet.class, (s, e) -> new ResultSetProperties(s));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (source instanceof ResultSet && !super.canConvert(sourceType, targetType)) {
			ResultSet resultSet = (ResultSet) source;
			if (ResultSet.class == targetType.getType()) {
				return resultSet;
			}

			if (targetType.isArray() || targetType.isCollection()) {
				Object[] array;
				try {
					array = SqlUtils.getRowValues(resultSet, resultSet.getMetaData().getColumnCount());
				} catch (SQLException e) {
					throw new ConversionException(e);
				}

				return convert(array, targetType);
			}

			if (isEntity(targetType)) {
				return super.convert(source, sourceType, targetType);
			}

			int columnCount;
			try {
				columnCount = resultSet.getMetaData().getColumnCount();
			} catch (SQLException e) {
				throw new ConversionException(e);
			}
			if (columnCount == 0) {
				return null;
			}

			Object value;
			try {
				value = resultSet.getObject(1);
			} catch (SQLException e) {
				throw new ConversionException(e);
			}
			return convert(value, targetType);
		}
		return super.convert(source, sourceType, targetType);
	}
}
