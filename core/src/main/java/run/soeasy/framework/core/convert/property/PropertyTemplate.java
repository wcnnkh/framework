package run.soeasy.framework.core.convert.property;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.KeyValueListable;
import run.soeasy.framework.core.collection.Streams;

@FunctionalInterface
public interface PropertyTemplate<T extends PropertyDescriptor>
		extends KeyValueListable<Object, T, KeyValue<Object, T>>, Elements<T> {

	public static class EmptyPropertyTemplate<T extends PropertyDescriptor>
			implements PropertyTemplate<T>, Serializable {
		private static final long serialVersionUID = 1L;
		private static final PropertyTemplate<?> EMPTY_PROPERTY_TEMPLATE = new EmptyPropertyTemplate<>();

		@Override
		public Iterator<T> iterator() {
			return Collections.emptyIterator();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends PropertyDescriptor> PropertyTemplate<T> empty() {
		return (PropertyTemplate<T>) EmptyPropertyTemplate.EMPTY_PROPERTY_TEMPLATE;
	}

	@FunctionalInterface
	public static interface PropertyTemplateWrapper<T extends PropertyDescriptor, W extends PropertyTemplate<T>> extends
			PropertyTemplate<T>, KeyValueListableWrapper<Object, T, KeyValue<Object, T>, W>, ElementsWrapper<T, W> {
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
