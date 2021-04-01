package scw.util;

@FunctionalInterface
public interface Accept<E> {
	boolean accept(E e);
}
