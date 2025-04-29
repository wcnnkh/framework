package run.soeasy.framework.core.convert.mapping;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.KeyValueListable;
import run.soeasy.framework.core.collection.Streams;

@FunctionalInterface
public interface PropertyDescriptors<T extends PropertyDescriptor>
		extends KeyValueListable<Object, T, KeyValue<Object, T>>, Elements<T> {

	public static class EmptyPropertyDescriptors<T extends PropertyDescriptor>
			implements PropertyDescriptors<T>, Serializable {
		private static final long serialVersionUID = 1L;
		private static final PropertyDescriptors<?> EMPTY_PROPERTY_DESCRIPTORS = new EmptyPropertyDescriptors<>();

		@Override
		public Iterator<T> iterator() {
			return Collections.emptyIterator();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends PropertyDescriptor> PropertyDescriptors<T> empty() {
		return (PropertyDescriptors<T>) EmptyPropertyDescriptors.EMPTY_PROPERTY_DESCRIPTORS;
	}

	@FunctionalInterface
	public static interface PropertyDescriptorsWrapper<T extends PropertyDescriptor, W extends PropertyDescriptors<T>>
			extends PropertyDescriptors<T>, KeyValueListableWrapper<Object, T, KeyValue<Object, T>, W>,
			ElementsWrapper<T, W> {
		@Override
		default Elements<KeyValue<Object, T>> getElements() {
			return getSource().getElements();
		}

		@Override
		default Stream<T> stream() {
			return getSource().stream();
		}
	}

	@Override
	default Elements<KeyValue<Object, T>> getElements() {
		return map((e) -> KeyValue.of(e.getName(), e));
	}

	@Override
	default Stream<T> stream() {
		return Streams.stream(iterator());
	}
}
