package io.basc.framework.mapper.stereotype;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.ReversibleMapper;
import io.basc.framework.transform.PropertiesTransformer;

public interface ObjectMapper extends ReversibleMapper<Object, Object, ConversionException>, ConversionService,
		PropertiesTransformer<Object, ConversionException>, MappingFactory {
	@Override
	default boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return ReversibleMapper.super.canConvert(sourceType, targetType);
	}
}
