package run.soeasy.framework.core.convert.support;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

import run.soeasy.framework.core.convert.ConditionalConversionService;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.type.TypeMapping;

class ArrayToArrayConversionService extends AbstractConversionService implements ConditionalConversionService {

	public ArrayToArrayConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<TypeMapping> getConvertibleTypeMappings() {
		return Collections.singleton(new TypeMapping(Object[].class, Object[].class));
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
