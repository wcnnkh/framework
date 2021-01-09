package scw.convert.support;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.ResolvableType;
import scw.mapper.MapperUtils;

public class EntityToMapConversionService extends ConditionalConversionService {
	private ConversionService conversionService;

	public EntityToMapConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		Map<String, Object> valueMap = MapperUtils.getMapper()
				.getFieldValueMap(source);
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
