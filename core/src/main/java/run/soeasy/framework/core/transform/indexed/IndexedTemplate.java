package run.soeasy.framework.core.transform.indexed;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.KeyValueListable;
import run.soeasy.framework.core.collection.Streams;

@FunctionalInterface
public interface IndexedTemplate<T extends IndexedDescriptor>
		extends KeyValueListable<Object, T, KeyValue<Object, T>>, Elements<T> {
	public static class EmptyIndexedTemplate<T extends IndexedDescriptor> implements IndexedTemplate<T>, Serializable {
		private static final long serialVersionUID = 1L;
		private static final IndexedTemplate<?> EMPTY_INDEXED_TEMPLATE = new EmptyIndexedTemplate<>();

		@Override
		public Iterator<T> iterator() {
			return Collections.emptyIterator();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends IndexedDescriptor> IndexedTemplate<T> empty() {
		return (IndexedTemplate<T>) EmptyIndexedTemplate.EMPTY_INDEXED_TEMPLATE;
	}

	@FunctionalInterface
	public static interface IndexedTemplateWrapper<T extends IndexedDescriptor, W extends IndexedTemplate<T>> extends
			IndexedTemplate<T>, KeyValueListableWrapper<Object, T, KeyValue<Object, T>, W>, ElementsWrapper<T, W> {

		@Override
		default Stream<T> stream() {
			return getSource().stream();
		}

		@Override
		default Elements<KeyValue<Object, T>> getElements() {
			return getSource().getElements();
		}

		@Override
		default IndexedTemplate<T> randomAccess() {
			return getSource().randomAccess();
		}

		@Override
		default int size() {
			return getSource().size();
		}

	}

	@Override
	default Elements<KeyValue<Object, T>> getElements() {
		return Elements.of(() -> stream().map((e) -> KeyValue.of(e.getIndex(), e)));
	}

	@Override
	default Stream<T> stream() {
		return Streams.stream(iterator());
	}

	default int size() {
		return getElements().count().intValue();
	}

	@Override
	default IndexedTemplate<T> randomAccess() {
		return new RandomAccessIndexedTemplate<>(this);
	}
}
