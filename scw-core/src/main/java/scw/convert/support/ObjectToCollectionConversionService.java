package scw.convert.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.convert.lang.ConditionalConversionService;
import scw.convert.lang.ConvertiblePair;
import scw.lang.Nullable;
import scw.util.CollectionFactory;

class ObjectToCollectionConversionService extends ConditionalConversionService {

	private final ConversionService conversionService;

	public ObjectToCollectionConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class,
				Collection.class));
	}

	@Nullable
	public Object convert(@Nullable Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}

		TypeDescriptor elementDesc = targetType.getElementTypeDescriptor();
		Collection<Object> target = CollectionFactory.createCollection(
				targetType.getType(),
				(elementDesc != null ? elementDesc.getType() : null), 1);

		if (elementDesc == null || elementDesc.isCollection()) {
			target.add(source);
		} else {
			Object singleElement = this.conversionService.convert(source,
					sourceType, elementDesc);
			target.add(singleElement);
		}
		return target;
	}
}
