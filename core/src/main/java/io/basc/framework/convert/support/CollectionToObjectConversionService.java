package io.basc.framework.convert.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import io.basc.framework.convert.ConditionalConversionService;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConvertiblePair;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.AbstractConversionService;

class CollectionToObjectConversionService extends AbstractConversionService implements ConditionalConversionService {

	public CollectionToObjectConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Collection.class, Object.class));
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		if (sourceType.isAssignableTo(targetType)) {
			return source;
		}
		Collection<?> sourceCollection = (Collection<?>) source;
		if (sourceCollection.isEmpty()) {
			return null;
		}
		Object firstElement = sourceCollection.iterator().next();
		return getConversionService().convert(firstElement, sourceType.elementTypeDescriptor(firstElement), targetType);
	}

}
