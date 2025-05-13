package run.soeasy.framework.core.convert.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConditionalConversionService;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.service.ConvertiblePair;

class CollectionToObjectConversionService extends AbstractConversionService implements ConditionalConversionService {

	public CollectionToObjectConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Collection.class, Object.class));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
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
