package run.soeasy.framework.core.streaming;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CachedStreamable<E, W extends Streamable<? extends E>> implements Streamable<E>, Serializable {
	private static final long serialVersionUID = 1L;
	protected final transient W source;
	protected final transient Supplier<? extends Collection<E>> collectionFactory;
	protected volatile Collection<E> collection;

	@SuppressWarnings("unchecked")
	@Override
	public <C extends Collection<E>> C toCollection(Class<?> collectionType) {
		Collection<E> collection = getCollection();
		return collectionType.isInstance(collection) ? (C) collection : Streamable.super.toCollection(collectionType);
	}

	@Override
	public Collection<E> toCollection() {
		return getCollection();
	}

	@Override
	public boolean contains(Object element) {
		return getCollection().contains(element);
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof CachedStreamable) {
			CachedStreamable other = (CachedStreamable) obj;
			return getCollection().equals(other.getCollection());
		}
		return getCollection().equals(obj);
	}

	protected boolean isReloadable() {
		if (source == null || collectionFactory == null) {
			return true;
		}
		return false;
	}

	@Override
	public Streamable<E> reload() {
		return isReloadable() ? new CachedStreamable<>(source.reload(), collectionFactory) : this;
	}

	@Override
	public int hashCode() {
		return getCollection().hashCode();
	}

	@Override
	public String toString() {
		return getCollection().toString();
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		getCollection().forEach(action);
	}

	public Collection<E> getCollection() {
		if (!isReloadable()) {
			return collection == null ? Collections.emptyList() : collection;
		}

		if (collection == null) {
			synchronized (this) {
				if (collection == null) {
					collection = source.collect(Collectors.toCollection(collectionFactory));
				}
			}
		}
		return collection;
	}

	@Override
	public boolean isEmpty() {
		return getCollection().isEmpty();
	}

	@Override
	public long count() {
		return getCollection().size();
	}

	@Override
	public Stream<E> stream() {
		return getCollection().stream();
	}

	@Override
	public Object[] toArray() {
		return getCollection().toArray();
	}

	private void writeObject(ObjectOutputStream output) throws IOException {
		output.writeObject(getCollection());
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
		this.collection = (Collection<E>) input.readObject();
	}

	@Override
	public E getAt(int index) throws IndexOutOfBoundsException {
		if (index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + " (negative index is not allowed)");
		}

		Collection<E> collection = getCollection();
		if (collection instanceof List) {
			return ((List<E>) collection).get(index);
		}
		return Streamable.super.getAt(index);
	}

	@Override
	public Optional<E> at(int index) {
		if (index < 0) {
			return Optional.empty();
		}

		Collection<E> collection = getCollection();
		if (index >= collection.size()) {
			return Optional.empty();
		}

		if (collection instanceof List) {
			return Optional.ofNullable(((List<E>) collection).get(index));
		}
		return Streamable.super.at(index);
	}

	@Override
	public boolean isUnique() {
		return getCollection().size() == 1;
	}

	@Override
	public boolean isRandomAccess() {
		return getCollection() instanceof RandomAccess;
	}
}
