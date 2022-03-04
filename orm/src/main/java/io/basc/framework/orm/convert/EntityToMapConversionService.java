package io.basc.framework.orm.convert;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.orm.ObjectRelationalMapping;
import io.basc.framework.orm.support.OrmUtils;
import io.basc.framework.util.CollectionFactory;

class EntityToMapConversionService extends ConditionalConversionService {
	private ObjectRelationalMapping objectRelationalMapping;

	public ObjectRelationalMapping getObjectRelationalMapping() {
		return objectRelationalMapping == null ? OrmUtils.getMapping() : objectRelationalMapping;
	}

	public void setObjectRelationalMapping(ObjectRelationalMapping objectRelationalMapping) {
		this.objectRelationalMapping = objectRelationalMapping;
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		Map<Object, Object> sourceMap = CollectionFactory.createMap(targetType.getType(),
				targetType.getMapKeyTypeDescriptor().getType(), 16);
		objectRelationalMapping.getStructure(sourceType.getType()).stream()
				.filter((p) -> p.getField().isSupportGetter()).forEach((property) -> {
					Object value = property.getField().getGetter().get(source);
					TypeDescriptor valuetype = new TypeDescriptor(property.getField().getGetter());
					value = getConversionService().convert(value, valuetype, targetType.getMapValueTypeDescriptor());
					Object key = property.getName();
					key = getConversionService().convert(key, TypeDescriptor.valueOf(String.class),
							targetType.getMapKeyTypeDescriptor());
					sourceMap.put(key, value);
				});
		return sourceMap;
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return super.canConvert(sourceType, targetType) && OrmUtils.getMapping().isEntity(sourceType.getType());
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Map.class));
	}

}
