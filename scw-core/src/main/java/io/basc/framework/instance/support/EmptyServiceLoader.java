package io.basc.framework.instance.support;

import io.basc.framework.instance.ServiceLoader;

import java.util.Collections;

public class EmptyServiceLoader<S> implements ServiceLoader<S> {
	public void reload() {
	}

	public java.util.Iterator<S> iterator() {
		return Collections.emptyIterator();
	};
}
