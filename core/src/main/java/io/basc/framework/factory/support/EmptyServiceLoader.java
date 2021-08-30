package io.basc.framework.factory.support;

import java.util.Collections;

import io.basc.framework.factory.ServiceLoader;

public class EmptyServiceLoader<S> implements ServiceLoader<S> {
	public void reload() {
	}

	public java.util.Iterator<S> iterator() {
		return Collections.emptyIterator();
	};
}
