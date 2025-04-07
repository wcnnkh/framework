package run.soeasy.framework.core.transform.stereotype;

import java.util.Iterator;
import java.util.stream.Stream;

import run.soeasy.framework.util.KeyValue;
import run.soeasy.framework.util.collection.KeyValues;
import run.soeasy.framework.util.collection.Listable;

@FunctionalInterface
public interface PropertyDescriptors<T extends PropertyDescriptor> extends KeyValues<String, T>, Listable<T> {
	@FunctionalInterface
	public static interface PropertyDescriptorsWrapper<T extends PropertyDescriptor, W extends PropertyDescriptors<T>>
			extends PropertyDescriptors<T>, KeyValuesWrapper<String, T, W>, ListableWrapper<T, W> {
		@Override
		default boolean hasElements() {
			return getSource().hasElements();
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
	default boolean hasElements() {
		return Listable.super.hasElements();
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
