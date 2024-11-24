package io.basc.framework.core.convert.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.ConvertiblePair;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConditionalConversionService;
import io.basc.framework.core.convert.lang.AbstractConversionService;
import io.basc.framework.core.convert.transform.stractegy.CollectionFactory;
import io.basc.framework.util.reflect.ReflectionUtils;

class CollectionToCollectionConversionService extends AbstractConversionService
		implements ConditionalConversionService {

	public CollectionToCollectionConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Collection.class, Collection.class));
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return ConditionalConversionService.super.canConvert(sourceType, targetType)
				&& ReflectionUtils.isInstance(targetType.getType());
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		Collection<?> sourceCollection = (Collection<?>) source;

		// Shortcut if possible...
		boolean copyRequired = !targetType.getType().isInstance(source);
		if (!copyRequired && sourceCollection.isEmpty()) {
			return source;
		}
		TypeDescriptor elementDesc = targetType.getElementTypeDescriptor();
		if (elementDesc == null && !copyRequired) {
			return source;
		}

		// At this point, we need a collection copy in any case, even if just for
		// finding out about element copies...
		Collection<Object> target = CollectionFactory.createCollection(targetType.getType(),
				(elementDesc != null ? elementDesc.getType() : null), sourceCollection.size());

		if (elementDesc == null) {
			target.addAll(sourceCollection);
		} else {
			for (Object sourceElement : sourceCollection) {
				Object targetElement = this.getConversionService().convert(sourceElement,
						sourceType.elementTypeDescriptor(sourceElement), elementDesc);
				target.add(targetElement);
				if (sourceElement != targetElement) {
					copyRequired = true;
				}
			}
		}

		return (copyRequired ? target : source);
	}

}
