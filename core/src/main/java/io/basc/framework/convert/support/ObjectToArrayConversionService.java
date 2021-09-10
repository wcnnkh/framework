package io.basc.framework.convert.support;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

class ObjectToArrayConversionService extends ConditionalConversionService {
	public ObjectToArrayConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
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
		Object targetElement = getConversionService().convert(source, sourceType, targetElementType);
		Array.set(target, 0, targetElement);
		return target;
	}
}
