package io.basc.framework.core.convert.transform;

import java.io.Serializable;

import io.basc.framework.core.convert.Value;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.collections.Lookup;
import lombok.NonNull;

public interface Properties extends PropertyTemplate<Property>, Lookup<String, Property>, PropertyFactory {
	public static class EmptyProperties implements Properties, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public Property get(String key) {
			return null;
		}

		@Override
		public Elements<String> keys() {
			return Elements.empty();
		}
	}

	public static final Properties EMPTY_PROPERTIES = new EmptyProperties();

	public static interface PropertiesWrapper<W extends Properties>
			extends Properties, PropertyMappingWrapper<Property, W>, LookupWrapper<String, Property, W> {

		@Override
		default Elements<String> keys() {
			return getSource().keys();
		}

		@Override
		default Elements<Property> getElements() {
			return getSource().getElements();
		}

		@Override
		default boolean containsKey(String key) {
			return getSource().containsKey(key);
		}

		@Override
		default Property get(String key) {
			return getSource().get(key);
		}

		@Override
		default Elements<Property> getValues(String key) {
			return getSource().getValues(key);
		}
	}

	default boolean containsKey(String key) {
		return get(key) != null;
	}

	@Override
	Property get(String key);

	@Override
	default Elements<Property> getElements() {
		return keys().map((key) -> get(key));
	}

	@Override
	default Elements<Property> getValues(String key) {
		Property property = get(key);
		if (property == null) {
			return Elements.empty();
		}
		return Elements.singleton(property);
	}

	@Override
	default boolean hasProperty(@NonNull PropertyDescriptor propertyDescriptor) {
		Property property = get(propertyDescriptor.getName());
		if (property == null) {
			return false;
		}

		return property.getTypeDescriptor().isAssignableTo(propertyDescriptor.getRequiredTypeDescriptor());
	}

	@Override
	default Value getProperty(@NonNull PropertyDescriptor propertyDescriptor) {
		Property property = get(propertyDescriptor.getName());
		if (property == null) {
			return null;
		}

		return property.getTypeDescriptor().isAssignableTo(propertyDescriptor.getRequiredTypeDescriptor()) ? property
				: null;
	}

	@Override
	Elements<String> keys();

}
