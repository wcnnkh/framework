package io.basc.framework.convert.support;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

import io.basc.framework.convert.ConditionalConversionService;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConvertiblePair;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.AbstractConversionService;

class ArrayToArrayConversionService extends AbstractConversionService implements ConditionalConversionService {

	public ArrayToArrayConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object[].class, Object[].class));
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}

		int len = Array.getLength(source);
		TypeDescriptor targetElementType = targetType.getElementTypeDescriptor();
		Object targetArray = Array.newInstance(targetElementType.getType(), len);
		for (int i = 0; i < len; i++) {
			Object sourceElement = Array.get(source, i);
			Object targetElement = getConversionService().convert(sourceElement,
					sourceType.elementTypeDescriptor(sourceElement), targetElementType);
			Array.set(targetArray, i, targetElement);
		}
		return targetArray;
	}
}
