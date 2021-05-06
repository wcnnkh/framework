package scw.convert.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.convert.lang.ConditionalConversionService;
import scw.convert.lang.ConvertiblePair;

class CollectionToObjectConversionService extends
		ConditionalConversionService {
	private final ConversionService conversionService;

	public CollectionToObjectConversionService(
			ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Collection.class,
				Object.class));
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
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
		return this.conversionService.convert(firstElement,
				sourceType.elementTypeDescriptor(firstElement), targetType);
	}

}
