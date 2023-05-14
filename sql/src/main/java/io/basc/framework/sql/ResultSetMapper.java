package io.basc.framework.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.orm.support.DefaultEntityMapper;

public class ResultSetMapper extends DefaultEntityMapper {

	public ResultSetMapper() {
		registerObjectAccessFactory(ResultSet.class, (s, e) -> new ResultSetAccess(s, e));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (source instanceof ResultSet) {
			ResultSet resultSet = (ResultSet) source;
			if (!isConverterRegistred(targetType.getType())) {
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

					return getConversionService().convert(array, targetType);
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
				return getConversionService().convert(value, targetType);
			}
		}
		return super.convert(source, sourceType, targetType);
	}
}
