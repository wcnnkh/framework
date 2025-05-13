package run.soeasy.framework.core.convert.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import run.soeasy.framework.core.collection.CollectionFactory;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConditionalConversionService;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.service.ConvertiblePair;

class ObjectToCollectionConversionService extends AbstractConversionService implements ConditionalConversionService {
	public ObjectToCollectionConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Collection.class));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (source == null) {
			return null;
		}

		TypeDescriptor elementDesc = targetType.getElementTypeDescriptor();
		Collection<Object> target = CollectionFactory.createCollection(targetType.getType(),
				(elementDesc != null ? elementDesc.getType() : null), 1);

		if (elementDesc == null || elementDesc.isCollection()) {
			target.add(source);
		} else {
			Object singleElement = getConversionService().convert(source, sourceType, elementDesc);
			target.add(singleElement);
		}
		return target;
	}
}
