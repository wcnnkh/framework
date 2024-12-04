package io.basc.framework.core.convert.transform;

import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.KeyValues;
import io.basc.framework.util.Listable;
import lombok.NonNull;

public interface PropertyMapping<T extends Property> extends Mapping<Object, T>, KeyValues<String, T>, Listable<T> {

	public static interface PropertyMappingWrapper<T extends Property, W extends PropertyMapping<T>>
			extends PropertyMapping<T>, KeyValuesWrapper<String, T, W>, MappingWrapper<Object, T, W> {

		@Override
		default Iterator<KeyValue<String, T>> iterator() {
			return getSource().iterator();
		}

		@Override
		default boolean isEmpty() {
			return getSource().isEmpty();
		}

		@Override
		default Stream<KeyValue<String, T>> stream() {
			return getSource().stream();
		}

		@Override
		default Elements<T> getAccesses(Object key) {
			return getSource().getAccesses(key);
		}

		@Override
		default Elements<KeyValue<Object, T>> getMembers() {
			return getSource().getMembers();
		}

		@Override
		default Elements<String> keys() {
			return getSource().keys();
		}
	}

	@Override
	default Stream<KeyValue<String, T>> stream() {
		return getElements().map((e) -> KeyValue.of(e.getName(), e)).stream();
	}

	@Override
	default Iterator<KeyValue<String, T>> iterator() {
		return getElements().map((e) -> KeyValue.of(e.getName(), e)).iterator();
	}

	@Override
	default boolean isEmpty() {
		return Listable.super.isEmpty();
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
