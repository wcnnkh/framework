package io.basc.framework.mapper;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.stereotype.MappingFactory;
import io.basc.framework.transform.PropertiesTransformer;

public interface ObjectMapper extends ReversibleMapper<Object, Object, ConversionException>, ConversionService,
		PropertiesTransformer<Object, ConversionException>, MappingFactory {
	@Override
	default boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return ReversibleMapper.super.canConvert(sourceType, targetType);
	}
}
