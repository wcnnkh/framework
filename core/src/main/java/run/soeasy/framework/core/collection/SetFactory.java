package run.soeasy.framework.core.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public interface SetFactory<E, T extends Set<E>> extends CollectionFactory<E, T> {
	@Override
	default Set<E> display(Collection<E> source) {
		return (source instanceof Set) ? Collections.unmodifiableSet((Set<E>) source) : clone(source);
	}

	@Override
	default T clone(Collection<E> source) {
		int newCapacity = Math.max(source.size(), Math.round((source.size() + 1) * (DEFAULT_LOAD_FACTOR * 2 - 1)));
		T target = createCollection(newCapacity, DEFAULT_LOAD_FACTOR);
		target.addAll(source);
		return target;
	}
}