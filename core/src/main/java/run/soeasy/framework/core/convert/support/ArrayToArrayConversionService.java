package run.soeasy.framework.core.convert.support;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConditionalConversionService;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.service.ConvertiblePair;

class ArrayToArrayConversionService extends AbstractConversionService implements ConditionalConversionService {

	public ArrayToArrayConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object[].class, Object[].class));
	}

	@Override
	public Object convert(@NonNull Source value, @NonNull TypeDescriptor requiredTypeDescriptor)
			throws ConversionException {
		Object source = value.get();
		TypeDescriptor sourceTypeDescriptor = value.getTypeDescriptor();
		int len = Array.getLength(source);
		TypeDescriptor targetElementType = requiredTypeDescriptor.getElementTypeDescriptor();
		Object targetArray = Array.newInstance(targetElementType.getType(), len);
		for (int i = 0; i < len; i++) {
			Object sourceElement = Array.get(source, i);
			Object targetElement = getConversionService().convert(sourceElement,
					sourceTypeDescriptor.elementTypeDescriptor(sourceElement), targetElementType);
			Array.set(targetArray, i, targetElement);
		}
		return targetArray;
	}
}
