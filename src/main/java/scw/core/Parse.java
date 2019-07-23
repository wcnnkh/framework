package scw.core;

public interface Parse<E> {
	Object parse(E e, Class<?> type);
}