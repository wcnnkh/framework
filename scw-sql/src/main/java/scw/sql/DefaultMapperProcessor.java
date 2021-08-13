package scw.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.env.Sys;
import scw.mapper.Field;
import scw.mapper.FieldFeature;
import scw.mapper.Fields;
import scw.mapper.MapperUtils;
import scw.util.stream.Processor;
import scw.value.Value;

public class DefaultMapperProcessor<T> implements Processor<ResultSet, T, Throwable> {
	private final ConversionService conversionService;
	private final TypeDescriptor typeDescriptor;

	public DefaultMapperProcessor(TypeDescriptor typeDescriptor) {
		this(Sys.env.getConversionService(), typeDescriptor);
	}

	public DefaultMapperProcessor(ConversionService conversionService, TypeDescriptor typeDescriptor) {
		this.conversionService = conversionService;
		this.typeDescriptor = typeDescriptor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T process(ResultSet rs) throws Throwable {
		if (typeDescriptor.isArray() || typeDescriptor.isCollection()) {
			Object[] array = SqlUtils.getRowValues(rs, rs.getMetaData().getColumnCount());
			return (T) conversionService.convert(array, TypeDescriptor.forObject(array), typeDescriptor);
		}

		if (isEntity(typeDescriptor)) {
			// 如果是一个实体类
			return (T) mapEntity(rs, typeDescriptor, conversionService);
		}

		int columnCount = rs.getMetaData().getColumnCount();
		if (columnCount == 0) {
			return null;
		}

		Object value = rs.getObject(1);
		return (T) conversionService.convert(value, TypeDescriptor.forObject(value), typeDescriptor);
	}

	protected boolean isEntity(TypeDescriptor typeDescriptor) {
		return !Value.isBaseType(typeDescriptor.getType());
	}

	protected Object mapEntity(ResultSet rs, TypeDescriptor typeDescriptor, ConversionService conversionService)
			throws Throwable {
		Object instance = typeDescriptor.getType().newInstance();
		Fields fields = MapperUtils.getFields(typeDescriptor.getType()).all().accept(FieldFeature.SUPPORT_SETTER)
				.shared();
		ResultSetMetaData metaData = rs.getMetaData();
		int cols = metaData.getColumnCount();
		for (int i = 1; i <= cols; i++) {
			String name = SqlUtils.lookupColumnName(metaData, i);
			Field field = fields.acceptSetter(name, null).first();
			if (field == null) {
				continue;
			}

			Object value = rs.getObject(i);
			value = conversionService.convert(value, TypeDescriptor.forObject(value),
					new TypeDescriptor(field.getSetter()));
			field.getSetter().set(instance, value);
		}
		return instance;
	}
}
