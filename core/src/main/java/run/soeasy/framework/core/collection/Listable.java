package run.soeasy.framework.core.collection;

public interface Listable<E> {
	Elements<E> getElements();

	default boolean hasElements() {
		return getElements().isEmpty();
	}
}
