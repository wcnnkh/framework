package io.basc.framework.core.convert.transform.stereotype;

import java.io.Serializable;
import java.util.Map;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.support.IdentityConversionService;
import io.basc.framework.core.convert.transform.stereotype.collection.MapProperties;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.collections.Lookup;
import lombok.NonNull;

public interface Properties extends PropertyTemplate<Property>, Lookup<String, Property>, PropertyFactory {
	public static class EmptyProperties implements Properties, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public Elements<Property> getElements() {
			return Elements.empty();
		}
	}

	public static interface PropertiesWrapper<W extends Properties>
			extends Properties, PropertyMappingWrapper<Property, W>, LookupWrapper<String, Property, W> {

		@Override
		default Property get(String key) {
			return getSource().get(key);
		}

		@Override
		default Source getProperty(@NonNull PropertyDescriptor propertyDescriptor) {
			return getSource().getProperty(propertyDescriptor);
		}

		@Override
		default boolean hasProperty(@NonNull PropertyDescriptor propertyDescriptor) {
			return getSource().hasProperty(propertyDescriptor);
		}
	}

	public static final Properties EMPTY_PROPERTIES = new EmptyProperties();

	public static Properties forMap(Map<? extends String, ?> map) {
		return new MapProperties(map, TypeDescriptor.map(map.getClass(), String.class, Object.class),
				new IdentityConversionService());
	}

	@Override
	default Property get(String key) {
		Elements<Property> values = getValues(key);
		if (values == null) {
			return null;
		}

		return values.isUnique() ? null : values.getUnique();
	}

	@Override
	default Source getProperty(@NonNull PropertyDescriptor propertyDescriptor) {
		Property property = get(propertyDescriptor.getName());
		if (property == null) {
			return null;
		}

		return property.getTypeDescriptor().isAssignableTo(propertyDescriptor.getRequiredTypeDescriptor()) ? property
				: null;
	}

	@Override
	default boolean hasProperty(@NonNull PropertyDescriptor propertyDescriptor) {
		Property property = get(propertyDescriptor.getName());
		if (property == null) {
			return false;
		}

		return property.getTypeDescriptor().isAssignableTo(propertyDescriptor.getRequiredTypeDescriptor());
	}
}
