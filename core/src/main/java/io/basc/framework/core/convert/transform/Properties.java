package io.basc.framework.core.convert.transform;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Lookup;

public interface Properties extends PropertyMapping<Property>, Lookup<String, Property> {

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
	Elements<String> keys();
}
