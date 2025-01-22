package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.util.collections.Elements;
import lombok.NonNull;

public interface PropertyTemplate<T extends Property> extends PropertyDescriptors<T>, Template<Object, T> {

	public static interface PropertyMappingWrapper<T extends Property, W extends PropertyTemplate<T>>
			extends PropertyTemplate<T>, PropertyDescriptorsWrapper<T, W>, TemplateWrapper<Object, T, W> {

		@Override
		default Elements<Object> getAccessorIndexes() {
			return getSource().getAccessorIndexes();
		}

		@Override
		default Elements<T> getAccessors(@NonNull Object index) {
			return getSource().getAccessors(index);
		}
	}

	@Override
	default Elements<Object> getAccessorIndexes() {
		return getElements().map((e) -> e);
	}

	@Override
	default Elements<T> getAccessors(@NonNull Object index) {
		if (index instanceof String) {
			return getValues((String) index);
		}
		if (index instanceof PropertyDescriptor) {
			PropertyDescriptor propertyDescriptor = (PropertyDescriptor) index;
			Elements<T> values = getValues(propertyDescriptor.getName());
			return values.filter(
					(e) -> e.getTypeDescriptor().isAssignableTo(propertyDescriptor.getRequiredTypeDescriptor()));
		}
		return Elements.empty();
	}
}
