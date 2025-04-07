package run.soeasy.framework.core.convert.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConditionalConversionService;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.service.ConvertiblePair;
import run.soeasy.framework.util.collection.CollectionUtils;

class ObjectToCollectionConversionService extends AbstractConversionService implements ConditionalConversionService {
	public ObjectToCollectionConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Collection.class));
	}

	@Override
	public Object convert(@NonNull Source value, @NonNull TypeDescriptor targetType) throws ConversionException {
		Object source = value.get();
		if (source == null) {
			return null;
		}

		TypeDescriptor elementDesc = targetType.getElementTypeDescriptor();
		Collection<Object> target = CollectionUtils.createCollection(targetType.getType(),
				(elementDesc != null ? elementDesc.getType() : null), 1);

		if (elementDesc == null || elementDesc.isCollection()) {
			target.add(source);
		} else {
			TypeDescriptor sourceType = value.getTypeDescriptor();
			Object singleElement = getConversionService().convert(source, sourceType, elementDesc);
			target.add(singleElement);
		}
		return target;
	}
}
