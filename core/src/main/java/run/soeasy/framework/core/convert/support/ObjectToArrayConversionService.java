package run.soeasy.framework.core.convert.support;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.convert.ConditionalConversionService;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.type.TypeMapping;

class ObjectToArrayConversionService extends AbstractConversionService implements ConditionalConversionService {
	public ObjectToArrayConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<TypeMapping> getConvertibleTypeMappings() {
		return Collections.singleton(new TypeMapping(Object.class, Object[].class));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (source == null) {
			return null;
		}

		TypeDescriptor targetElementType = targetType.getElementTypeDescriptor();
		Assert.state(targetElementType != null, "No target element type");
		Object target = Array.newInstance(targetElementType.getType(), 1);
		Object targetElement = getConversionService().convert(source, sourceType, targetElementType);
		Array.set(target, 0, targetElement);
		return target;
	}
}
