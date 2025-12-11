package run.soeasy.framework.core.convert.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.convert.AbstractConditionalConverter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;

class ObjectToCollectionConverter extends AbstractConditionalConverter {
	public Set<TypeMapping> getConvertibleTypeMappings() {
		return Collections.singleton(new TypeMapping(Object.class, Collection.class));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (source == null) {
			return null;
		}

		TypeDescriptor elementDesc = targetType.getElementTypeDescriptor();
		Collection<Object> target = CollectionUtils.createCollection(targetType.getType(),
				(elementDesc != null ? elementDesc.getType() : null));

		if (elementDesc == null || elementDesc.isCollection()) {
			target.add(source);
		} else {
			Object singleElement = getConverter().convert(source, sourceType, elementDesc);
			target.add(singleElement);
		}
		return target;
	}
}
