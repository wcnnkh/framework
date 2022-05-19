package io.basc.framework.sql.orm.convert;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.orm.ObjectRelationalMapper;
import io.basc.framework.orm.StructureRegistry;
import io.basc.framework.orm.convert.PropertyFactoryToEntityConversionService;
import io.basc.framework.orm.support.OrmUtils;
import io.basc.framework.sql.ResultSetMapProcessor;
import io.basc.framework.sql.orm.TableStructure;

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
public class SmartResultSetMappingProcessor<T> extends ResultSetMapProcessor<T> {
	private final ObjectRelationalMapper objectRelationalMapping;
	private ObjectMapper<ResultSet, ? extends Throwable> mapper;
	private StructureRegistry<TableStructure> structureRegistry;

	public SmartResultSetMappingProcessor(TypeDescriptor typeDescriptor) {
		this(OrmUtils.getMapping(), typeDescriptor);
	}

	public SmartResultSetMappingProcessor(
			ObjectRelationalMapper objectRelationalMapping,
			TypeDescriptor typeDescriptor) {
		this(objectRelationalMapping, Sys.env.getConversionService(),
				typeDescriptor);
	}

	public SmartResultSetMappingProcessor(
			ObjectRelationalMapper objectRelationalMapping,
			ConversionService conversionService, TypeDescriptor typeDescriptor) {
		super(conversionService, objectRelationalMapping, typeDescriptor);
		this.objectRelationalMapping = objectRelationalMapping;
	}

	public ObjectMapper<ResultSet, ? extends Throwable> getMapper() {
		return mapper;
	}

	public void setMapper(ObjectMapper<ResultSet, ? extends Throwable> mapper) {
		this.mapper = mapper;
	}

	public StructureRegistry<TableStructure> getStructureRegistry() {
		return structureRegistry;
	}

	public void setStructureRegistry(
			StructureRegistry<TableStructure> structureRegistry) {
		this.structureRegistry = structureRegistry;
	}

	@Override
	protected boolean isEntity(TypeDescriptor typeDescriptor) {
		if (structureRegistry != null
				&& !typeDescriptor.isGeneric()
				&& structureRegistry.isStructureRegistred(typeDescriptor
						.getType())) {
			return true;
		}

		if (mapper != null && !typeDescriptor.isGeneric()
				&& mapper.isMapperRegistred(typeDescriptor.getType())) {
			return true;
		}

		return objectRelationalMapping.isEntity(typeDescriptor.getType());
	}

	@Override
	protected Object mapEntity(ResultSet rs, TypeDescriptor typeDescriptor,
			ConversionService conversionService) throws Throwable {
		if (structureRegistry != null
				&& !typeDescriptor.isGeneric()
				&& structureRegistry.isStructureRegistred(typeDescriptor
						.getType())) {
			TableStructure tableStructure = structureRegistry
					.getStructure(typeDescriptor.getType());
			return new EntityStructureMapProcessor<T>(tableStructure,
					getConversionService()).process(rs);
		}

		if (mapper != null && !typeDescriptor.isGeneric()
				&& mapper.isMapperRegistred(typeDescriptor.getType())) {
			return mapper.process(typeDescriptor.getType(), rs);
		}

		ResultSetPropertyFactory propertyFactory = new ResultSetPropertyFactory(
				rs);
		return conversionService.convert(propertyFactory,
				TypeDescriptor.forObject(propertyFactory), typeDescriptor);
	}
}
