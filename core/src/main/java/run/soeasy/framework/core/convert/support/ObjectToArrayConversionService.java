package run.soeasy.framework.core.convert.support;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.convert.ConditionalConversionService;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.ConvertiblePair;
import run.soeasy.framework.core.convert.TargetDescriptor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypedValue;

class ObjectToArrayConversionService extends AbstractConversionService implements ConditionalConversionService {
	public ObjectToArrayConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Object[].class));
	}

	@Override
	public Object apply(@NonNull TypedValue value, @NonNull TargetDescriptor targetDescriptor) {
		Object source = value.get();
		if (source == null) {
			return null;
		}

		TypeDescriptor sourceType = value.getReturnTypeDescriptor();
		TypeDescriptor targetElementType = targetDescriptor.getRequiredTypeDescriptor().getElementTypeDescriptor();
		Assert.state(targetElementType != null, "No target element type");
		Object target = Array.newInstance(targetElementType.getType(), 1);
		Object targetElement = getConversionService().convert(source, sourceType, targetElementType);
		Array.set(target, 0, targetElement);
		return target;
	}
}
