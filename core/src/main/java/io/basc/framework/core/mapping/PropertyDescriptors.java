package io.basc.framework.core.mapping;

import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.util.KeyValue;
import io.basc.framework.util.collections.KeyValues;
import io.basc.framework.util.collections.Listable;

@FunctionalInterface
public interface PropertyDescriptors<T extends PropertyDescriptor> extends KeyValues<String, T>, Listable<T> {
	@FunctionalInterface
	public static interface PropertyDescriptorsWrapper<T extends PropertyDescriptor, W extends PropertyDescriptors<T>>
			extends PropertyDescriptors<T>, KeyValuesWrapper<String, T, W>, ListableWrapper<T, W> {
		@Override
		default boolean isEmpty() {
			return getSource().isEmpty();
		}

		@Override
		default Iterator<KeyValue<String, T>> iterator() {
			return getSource().iterator();
		}

		@Override
		default Stream<KeyValue<String, T>> stream() {
			return getSource().stream();
		}
	}

	@Override
	default boolean isEmpty() {
		return Listable.super.isEmpty();
	}

	@Override
	default Iterator<KeyValue<String, T>> iterator() {
		return getElements().map((e) -> KeyValue.of(e.getName(), e)).iterator();
	}

	@Override
	default Stream<KeyValue<String, T>> stream() {
		return getElements().map((e) -> KeyValue.of(e.getName(), e)).stream();
	}

}
