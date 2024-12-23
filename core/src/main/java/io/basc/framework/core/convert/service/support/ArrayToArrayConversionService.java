package io.basc.framework.core.convert.service.support;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.lang.AbstractConversionService;
import io.basc.framework.core.convert.service.ConditionalConversionService;
import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.service.ConvertiblePair;
import lombok.NonNull;

class ArrayToArrayConversionService extends AbstractConversionService implements ConditionalConversionService {

	public ArrayToArrayConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object[].class, Object[].class));
	}

	@Override
	public Object convert(@NonNull Value value, @NonNull TypeDescriptor requiredTypeDescriptor)
			throws ConversionException {
		Object source = value.get();
		TypeDescriptor sourceTypeDescriptor = value.getTypeDescriptor();
		int len = Array.getLength(source);
		TypeDescriptor targetElementType = requiredTypeDescriptor.getElementTypeDescriptor();
		Object targetArray = Array.newInstance(targetElementType.getType(), len);
		for (int i = 0; i < len; i++) {
			Object sourceElement = Array.get(source, i);
			Object targetElement = getConversionService().convert(sourceElement,
					sourceTypeDescriptor.elementTypeDescriptor(sourceElement), targetElementType);
			Array.set(targetArray, i, targetElement);
		}
		return targetArray;
	}
}
