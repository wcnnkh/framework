package io.basc.framework.sql;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldFactory;
import io.basc.framework.mapper.FieldFeature;
import io.basc.framework.mapper.Fields;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.value.Value;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class ResultSetMapProcessor<T> implements Processor<ResultSet, T, Throwable> {
	private final ConversionService conversionService;
	private final TypeDescriptor typeDescriptor;
	private final FieldFactory fieldFactory;

	public ResultSetMapProcessor(Class<T> type) {
		this(MapperUtils.getFieldFactory(), TypeDescriptor.valueOf(type));
	}

	public ResultSetMapProcessor(FieldFactory fieldFactory, Class<T> type) {
		this(fieldFactory, TypeDescriptor.valueOf(type));
	}

	public ResultSetMapProcessor(ConversionService conversionService,
			FieldFactory fieldFactory, Class<T> type) {
		this(conversionService, fieldFactory, TypeDescriptor.valueOf(type));
	}

	public ResultSetMapProcessor(TypeDescriptor typeDescriptor) {
		this(Sys.env.getConversionService(), MapperUtils.getFieldFactory(),
				typeDescriptor);
	}

	public ResultSetMapProcessor(FieldFactory fieldFactory, TypeDescriptor typeDescriptor) {
		this(Sys.env.getConversionService(), fieldFactory, typeDescriptor);
	}

	public ResultSetMapProcessor(ConversionService conversionService,
			FieldFactory fieldFactory, TypeDescriptor typeDescriptor) {
		this.conversionService = conversionService;
		this.fieldFactory = fieldFactory;
		this.typeDescriptor = typeDescriptor;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor;
	}

	public FieldFactory getFieldFactory() {
		return fieldFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T process(ResultSet rs) throws Throwable {
		if (typeDescriptor.isArray() || typeDescriptor.isCollection()) {
			Object[] array = SqlUtils.getRowValues(rs, rs.getMetaData()
					.getColumnCount());
			return (T) conversionService.convert(array,
					TypeDescriptor.forObject(array), typeDescriptor);
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
		return (T) conversionService.convert(value,
				TypeDescriptor.forObject(value), typeDescriptor);
	}

	protected boolean isEntity(TypeDescriptor typeDescriptor) {
		return !Value.isBaseType(typeDescriptor.getType());
	}

	protected Object mapEntity(ResultSet rs, TypeDescriptor typeDescriptor,
			ConversionService conversionService)
			throws Throwable {
		Object instance = typeDescriptor.getType().newInstance();
		Fields fields = MapperUtils.getFields(typeDescriptor.getType()).all().accept(FieldFeature.SUPPORT_SETTER)
				.shared();
		ResultSetMetaData metaData = rs.getMetaData();
		int cols = metaData.getColumnCount();
		for (int i = 1; i <= cols; i++) {
			String name = SqlUtils.lookupColumnName(metaData, i);
			Field field = fields.find(name).first();
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
