package scw.core;

import scw.lang.Ignore;

@Ignore
public interface Callable<V> extends java.util.concurrent.Callable<V> {
	V call();
}
