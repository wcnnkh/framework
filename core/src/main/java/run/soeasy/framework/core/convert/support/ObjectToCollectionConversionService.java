package run.soeasy.framework.core.convert.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.convert.ConditionalConversionService;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.ConvertiblePair;
import run.soeasy.framework.core.convert.TargetDescriptor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypedValue;

class ObjectToCollectionConversionService extends AbstractConversionService implements ConditionalConversionService {
	public ObjectToCollectionConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Collection.class));
	}

	@Override
	public Object apply(@NonNull TypedValue value, @NonNull TargetDescriptor targetDescriptor) {
		Object source = value.get();
		if (source == null) {
			return null;
		}

		TypeDescriptor elementDesc = targetDescriptor.getRequiredTypeDescriptor().getElementTypeDescriptor();
		Collection<Object> target = CollectionUtils.createCollection(
				targetDescriptor.getRequiredTypeDescriptor().getType(),
				(elementDesc != null ? elementDesc.getType() : null), 1);

		if (elementDesc == null || elementDesc.isCollection()) {
			target.add(source);
		} else {
			TypeDescriptor sourceType = value.getReturnTypeDescriptor();
			Object singleElement = getConversionService().convert(source, sourceType, elementDesc);
			target.add(singleElement);
		}
		return target;
	}
}
