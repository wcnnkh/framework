package scw.orm.sql.convert;

import java.sql.ResultSet;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.env.Sys;
import scw.orm.ObjectRelationalMapping;
import scw.orm.OrmUtils;
import scw.orm.convert.PropertyFactoryToEntityConversionService;
import scw.sql.DefaultMapperProcessor;

/**
 * 对ResultSet和实体之间的映射做了最大的兼容,支持任意对象的组合
 * 
 * @see ResultSetPropertyFactory
 * @see PropertyFactoryToEntityConversionService
 * @author shuchaowen
 *
 * @param <T>
 */
public class SmartMapperProcessor<T> extends DefaultMapperProcessor<T> {
	private final ObjectRelationalMapping objectRelationalMapping;

	public SmartMapperProcessor(TypeDescriptor typeDescriptor) {
		this(OrmUtils.getMapping(), typeDescriptor);
	}

	public SmartMapperProcessor(
			ObjectRelationalMapping objectRelationalMapping,
			TypeDescriptor typeDescriptor) {
		this(objectRelationalMapping, Sys.env.getConversionService(),
				typeDescriptor);
	}

	public SmartMapperProcessor(
			ObjectRelationalMapping objectRelationalMapping,
			ConversionService conversionService, TypeDescriptor typeDescriptor) {
		super(conversionService, typeDescriptor);
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
