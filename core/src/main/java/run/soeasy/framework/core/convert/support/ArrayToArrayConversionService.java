package run.soeasy.framework.core.convert.support;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

import run.soeasy.framework.core.convert.ConversionException;
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
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		int len = Array.getLength(source);
		TypeDescriptor targetElementType = targetType.getElementTypeDescriptor();
		Object targetArray = Array.newInstance(targetElementType.getType(), len);
		for (int i = 0; i < len; i++) {
			Object sourceElement = Array.get(source, i);
			Object targetElement = getConversionService().convert(sourceElement,
					sourceType.elementTypeDescriptor(sourceElement), targetElementType);
			Array.set(targetArray, i, targetElement);
		}
		return targetArray;
	}
}
