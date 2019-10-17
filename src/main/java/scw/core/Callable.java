package scw.core;

import scw.core.annotation.Ignore;

@Ignore
public interface Callable<V> extends java.util.concurrent.Callable<V> {
	V call();
}
