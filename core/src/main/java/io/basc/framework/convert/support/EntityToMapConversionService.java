package io.basc.framework.convert.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.mapper.MapperUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

class EntityToMapConversionService extends ConditionalConversionService {
	private ConversionService conversionService;

	public EntityToMapConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		Map<String, Object> valueMap = MapperUtils.getFields(sourceType.getType()).all()
				.getValueMap(source);
		return conversionService.convert(
				valueMap,
				new TypeDescriptor(ResolvableType.forClassWithGenerics(
						Map.class, String.class, Object.class), Map.class,
						sourceType.getAnnotations()), targetType);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class,
				Map.class));
	}

}
