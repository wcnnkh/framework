package io.basc.framework.factory.support;

import java.util.Collections;
import java.util.Iterator;

import io.basc.framework.util.ServiceLoader;

public class EmptyServiceLoader<S> implements ServiceLoader<S> {
	public void reload() {
	}

	public Iterator<S> iterator() {
		return Collections.emptyIterator();
	};
}
