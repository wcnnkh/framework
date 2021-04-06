package scw.util;


@FunctionalInterface
public interface Supplier<T> extends java.util.function.Supplier<T>{
	T get();
}
