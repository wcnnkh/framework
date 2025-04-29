package run.soeasy.framework.core.convert.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConditionalConversionService;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.ConvertiblePair;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypedValue;

class CollectionToObjectConversionService extends AbstractConversionService implements ConditionalConversionService {

	public CollectionToObjectConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Collection.class, Object.class));
	}

	@Override
	public Object apply(@NonNull TypedValue value, @NonNull TypeDescriptor targetType) throws ConversionException {
		Object source = value.get();
		if (source == null) {
			return null;
		}

		TypeDescriptor sourceType = value.getReturnTypeDescriptor();
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
