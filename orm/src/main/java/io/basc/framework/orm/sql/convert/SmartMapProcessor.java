package io.basc.framework.orm.sql.convert;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.orm.ObjectRelationalMapping;
import io.basc.framework.orm.OrmUtils;
import io.basc.framework.orm.convert.PropertyFactoryToEntityConversionService;
import io.basc.framework.sql.ResultSetMapProcessor;

import java.sql.ResultSet;

/**
 * 对ResultSet和实体之间的映射做了最大的兼容,支持任意对象的组合
 * 
 * @see ResultSetPropertyFactory
 * @see PropertyFactoryToEntityConversionService
 * @author shuchaowen
 *
 * @param <T>
 */
public class SmartMapProcessor<T> extends ResultSetMapProcessor<T> {
	private final ObjectRelationalMapping objectRelationalMapping;

	public SmartMapProcessor(TypeDescriptor typeDescriptor) {
		this(OrmUtils.getMapping(), typeDescriptor);
	}

	public SmartMapProcessor(
			ObjectRelationalMapping objectRelationalMapping,
			TypeDescriptor typeDescriptor) {
		this(objectRelationalMapping, Sys.env.getConversionService(),
				typeDescriptor);
	}

	public SmartMapProcessor(
			ObjectRelationalMapping objectRelationalMapping,
			ConversionService conversionService, TypeDescriptor typeDescriptor) {
		super(conversionService, objectRelationalMapping, typeDescriptor);
		this.objectRelationalMapping = objectRelationalMapping;
	}

	@Override
	protected boolean isEntity(TypeDescriptor typeDescriptor) {
		return objectRelationalMapping.isEntity(typeDescriptor.getType());
	}

	@Override
	protected Object mapEntity(ResultSet rs, TypeDescriptor typeDescriptor,
			ConversionService conversionService) throws Throwable {
		ResultSetPropertyFactory propertyFactory = new ResultSetPropertyFactory(
				rs);
		return conversionService.convert(propertyFactory,
				TypeDescriptor.forObject(propertyFactory), typeDescriptor);
	}
}
