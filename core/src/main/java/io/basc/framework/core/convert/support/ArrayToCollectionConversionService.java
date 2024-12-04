package io.basc.framework.core.convert.support;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.ConvertiblePair;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConditionalConversionService;
import io.basc.framework.core.convert.lang.AbstractConversionService;
import io.basc.framework.util.CollectionUtils;

class ArrayToCollectionConversionService extends AbstractConversionService implements ConditionalConversionService {
	public ArrayToCollectionConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object[].class, Collection.class));
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}

		int length = Array.getLength(source);
		TypeDescriptor elementDesc = targetType.getElementTypeDescriptor();
		Collection<Object> target = CollectionUtils.createCollection(targetType.getType(),
				(elementDesc != null ? elementDesc.getType() : null), length);

		if (elementDesc == null) {
			for (int i = 0; i < length; i++) {
				Object sourceElement = Array.get(source, i);
				target.add(sourceElement);
			}
		} else {
			for (int i = 0; i < length; i++) {
				Object sourceElement = Array.get(source, i);
				Object targetElement = getConversionService().convert(sourceElement,
						sourceType.elementTypeDescriptor(sourceElement), elementDesc);
				target.add(targetElement);
			}
		}
		return target;
	}
}
