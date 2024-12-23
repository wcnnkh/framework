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
import io.basc.framework.util.Assert;
import lombok.NonNull;

class ObjectToArrayConversionService extends AbstractConversionService implements ConditionalConversionService {
	public ObjectToArrayConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Object[].class));
	}

	@Override
	public Object convert(@NonNull Value value, @NonNull TypeDescriptor targetType) throws ConversionException {
		Object source = value.get();
		if (source == null) {
			return null;
		}

		TypeDescriptor sourceType = value.getTypeDescriptor();
		TypeDescriptor targetElementType = targetType.getElementTypeDescriptor();
		Assert.state(targetElementType != null, "No target element type");
		Object target = Array.newInstance(targetElementType.getType(), 1);
		Object targetElement = getConversionService().convert(source, sourceType, targetElementType);
		Array.set(target, 0, targetElement);
		return target;
	}
}
