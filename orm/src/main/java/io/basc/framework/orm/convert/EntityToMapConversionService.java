package io.basc.framework.orm.convert;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.orm.OrmUtils;

class EntityToMapConversionService extends ConditionalConversionService {

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		Map<String, Object> valueMap = MapperUtils.getFields(sourceType.getType()).all().getValueMap(source);
		return getConversionService().convert(valueMap,
				new TypeDescriptor(ResolvableType.forClassWithGenerics(Map.class, String.class, Object.class),
						Map.class, sourceType.getAnnotations()),
				targetType);
	}
	
	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return super.canConvert(sourceType, targetType) && OrmUtils.getMapping().isEntity(sourceType.getType());
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Map.class));
	}

}
