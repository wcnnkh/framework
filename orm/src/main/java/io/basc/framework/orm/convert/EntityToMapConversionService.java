package io.basc.framework.orm.convert;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.orm.ObjectRelationalFactory;
import io.basc.framework.orm.support.OrmUtils;
import io.basc.framework.util.CollectionFactory;

class EntityToMapConversionService extends ConditionalConversionService {
	private ObjectRelationalFactory mapper;

	public ObjectRelationalFactory getMapper() {
		return mapper == null ? OrmUtils.getMapper() : mapper;
	}

	public void setMapper(ObjectRelationalFactory mapper) {
		this.mapper = mapper;
	}

	@SuppressWarnings("unchecked")
	public <R> R convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		Map<Object, Object> sourceMap = CollectionFactory.createMap(targetType.getType(),
				targetType.getMapKeyTypeDescriptor().getType(), 16);
		getMapper().getStructure(sourceType.getType()).stream().filter((p) -> p.isSupportGetter())
				.forEach((property) -> {
					Object value = property.get(source);
					TypeDescriptor valuetype = new TypeDescriptor(property.getGetter());
					value = getConversionService().convert(value, valuetype, targetType.getMapValueTypeDescriptor());
					Object key = property.getName();
					key = getConversionService().convert(key, TypeDescriptor.valueOf(String.class),
							targetType.getMapKeyTypeDescriptor());
					sourceMap.put(key, value);
				});
		return (R) sourceMap;
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return super.canConvert(sourceType, targetType) && OrmUtils.getMapper().isEntity(sourceType.getType());
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Map.class));
	}

}
