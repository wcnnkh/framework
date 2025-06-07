package run.soeasy.framework.core.collection;

import java.io.Serializable;
import java.util.Collection;

@FunctionalInterface
public interface Listable<E> {
	@SuppressWarnings("unchecked")
	public static <E> Listable<E> empty() {
		return (Listable<E>) EmptyElements.EMPTY_ELEMENTS;
	}

	@SuppressWarnings("unchecked")
	public static <E> Listable<E> forElements(Elements<E> elements) {
		return (Listable<E> & Serializable) () -> elements;
	}

	public static <E> Listable<E> forCollection(Collection<E> collection) {
		return forElements(Elements.of(collection));
	}

	Elements<E> getElements();

	default boolean hasElements() {
		return getElements().isEmpty();
	}
}
