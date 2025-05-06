package run.soeasy.framework.core.convert.support;

import java.lang.reflect.Array;
import java.util.Collection;
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

class CollectionToArrayConversionService extends AbstractConversionService implements ConditionalConversionService {

	public CollectionToArrayConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Collection.class, Object[].class));
	}

	@Override
	public Object apply(@NonNull TypedValue value, @NonNull TargetDescriptor targetDescriptor) {
		Object source = value.get();
		if (source == null) {
			return null;
		}
		TypeDescriptor sourceType = value.getReturnTypeDescriptor();
		Collection<?> sourceCollection = (Collection<?>) source;
		TypeDescriptor targetElementType = targetDescriptor.getRequiredTypeDescriptor().getElementTypeDescriptor();
		Assert.state(targetElementType != null, "No target element type");
		Object array = Array.newInstance(targetElementType.getType(), sourceCollection.size());
		int i = 0;
		for (Object sourceElement : sourceCollection) {
			Object targetElement = this.getConversionService().convert(sourceElement,
					sourceType.elementTypeDescriptor(sourceElement), targetElementType);
			Array.set(array, i++, targetElement);
		}
		return array;
	}
}
