package run.soeasy.framework.core.convert.support;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.convert.ConditionalConversionService;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.ConvertiblePair;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.ValueAccessor;

class ArrayToCollectionConversionService extends AbstractConversionService implements ConditionalConversionService {
	public ArrayToCollectionConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object[].class, Collection.class));
	}

	@Override
	public Object convert(@NonNull ValueAccessor value, @NonNull TypeDescriptor requiredTypeDescriptor)
			throws ConversionException {
		Object source = value.get();
		TypeDescriptor sourceType = value.getTypeDescriptor();
		int length = Array.getLength(source);
		TypeDescriptor elementDesc = requiredTypeDescriptor.getElementTypeDescriptor();
		Collection<Object> target = CollectionUtils.createCollection(requiredTypeDescriptor.getType(),
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
