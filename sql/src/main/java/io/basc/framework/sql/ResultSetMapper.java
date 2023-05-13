package io.basc.framework.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.orm.repository.DefaultRepositoryMapper;

public class ResultSetMapper extends DefaultRepositoryMapper<ResultSet, SQLException> {

	public ResultSetMapper() {
		registerObjectAccessFactory(ResultSet.class, (s, e) -> new ResultSetAccess(s));
	}

	@Override
	public Object convert(ResultSet source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws SQLException, ConverterNotFoundException {
		if (!isConverterRegistred(targetType.getType())) {
			if (ResultSet.class == targetType.getType()) {
				return source;
			}

			if (targetType.isArray() || targetType.isCollection()) {
				Object[] array = SqlUtils.getRowValues(source, source.getMetaData().getColumnCount());
				return getConversionService().convert(array, targetType);
			}

			if (isEntity(targetType.getType())) {
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
}
