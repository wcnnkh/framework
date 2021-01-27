package scw.instance.support;

import java.util.Collections;

import scw.instance.ServiceLoader;

public class EmptyServiceLoader<S> implements ServiceLoader<S> {
	public void reload() {
	}

	public java.util.Iterator<S> iterator() {
		return Collections.emptyIterator();
	};
}
