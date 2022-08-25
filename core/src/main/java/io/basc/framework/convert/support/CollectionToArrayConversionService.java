package io.basc.framework.convert.support;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.AbstractConversionService;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.util.Assert;

class CollectionToArrayConversionService extends AbstractConversionService implements ConditionalConversionService {

	public CollectionToArrayConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Collection.class, Object[].class));
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		Collection<?> sourceCollection = (Collection<?>) source;
		TypeDescriptor targetElementType = targetType.getElementTypeDescriptor();
		Assert.state(targetElementType != null, "No target element type");
		Object array = Array.newInstance(targetElementType.getType(), sourceCollection.size());
		int i = 0;
		for (Object sourceElement : sourceCollection) {
			Object targetElement = this.getConversionService().convert(sourceElement,
					sourceType.elementTypeDescriptor(sourceElement), targetElementType);
			Array.set(array, i++, targetElement);
		}
		return array;
	}
}
