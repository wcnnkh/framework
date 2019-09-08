package scw.core;

public interface Callable<V> extends java.util.concurrent.Callable<V> {
	V call();
}
