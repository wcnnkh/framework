package io.basc.framework.convert.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.CollectionFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

class ObjectToCollectionConversionService extends ConditionalConversionService {
	public ObjectToCollectionConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Collection.class));
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}

		TypeDescriptor elementDesc = targetType.getElementTypeDescriptor();
		Collection<Object> target = CollectionFactory.createCollection(targetType.getType(),
				(elementDesc != null ? elementDesc.getType() : null), 1);

		if (elementDesc == null || elementDesc.isCollection()) {
			target.add(source);
		} else {
			Object singleElement = getConversionService().convert(source, sourceType, elementDesc);
			target.add(singleElement);
		}
		return target;
	}
}
