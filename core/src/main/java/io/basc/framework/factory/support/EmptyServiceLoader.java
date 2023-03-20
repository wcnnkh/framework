package io.basc.framework.factory.support;

import io.basc.framework.util.Cursor;
import io.basc.framework.util.ServiceLoader;

public class EmptyServiceLoader<S> implements ServiceLoader<S> {
	public void reload() {
	}

	public Cursor<S> iterator() {
		return Cursor.empty();
	};
}
