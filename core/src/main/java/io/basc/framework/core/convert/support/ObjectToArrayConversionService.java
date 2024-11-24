package io.basc.framework.core.convert.support;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.ConvertiblePair;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConditionalConversionService;
import io.basc.framework.core.convert.lang.AbstractConversionService;
import io.basc.framework.util.Assert;

class ObjectToArrayConversionService extends AbstractConversionService implements ConditionalConversionService {
	public ObjectToArrayConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Object[].class));
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
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
