package io.basc.framework.convert.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.core.Assert;
import io.basc.framework.lang.Nullable;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

class ObjectToArrayConversionService extends ConditionalConversionService{
	private final ConversionService conversionService;


	public ObjectToArrayConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}


	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Object[].class));
	}

	@Nullable
	public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		
		TypeDescriptor targetElementType = targetType.getElementTypeDescriptor();
		Assert.state(targetElementType != null, "No target element type");
		Object target = Array.newInstance(targetElementType.getType(), 1);
		Object targetElement = this.conversionService.convert(source, sourceType, targetElementType);
		Array.set(target, 0, targetElement);
		return target;
	}
}
