package io.basc.framework.core.convert.transform;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import lombok.NonNull;

public interface PropertyMapping<T extends Property> extends PropertyDescriptors<T>, Mapping<Object, T> {

	public static interface PropertyMappingWrapper<T extends Property, W extends PropertyMapping<T>>
			extends PropertyMapping<T>, PropertyDescriptorsWrapper<T, W>, MappingWrapper<Object, T, W> {

		@Override
		default Elements<T> getAccesses(Object key) {
			return getSource().getAccesses(key);
		}

		@Override
		default Elements<KeyValue<Object, T>> getMembers() {
			return getSource().getMembers();
		}
	}

	@Override
	default Elements<KeyValue<Object, T>> getMembers() {
		return keys().flatMap((key) -> getValues(key).map((value) -> KeyValue.of(key, value)));
	}

	@Override
	default Elements<T> getAccesses(@NonNull Object key) {
		if (key instanceof String) {
			return getValues((String) key);
		}
		return Elements.empty();
	}
}
