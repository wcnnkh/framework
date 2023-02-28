package io.basc.framework.util;

import java.util.LinkedHashMap;

public final class InheriterBackup<A, B> extends LinkedHashMap<Inheriter<A, B>, B> {
	private static final long serialVersionUID = 1L;

	public InheriterBackup() {
		super();
	}

	public InheriterBackup(int initialCapacity) {
		super(initialCapacity);
	}

	public void restore() {
		for (java.util.Map.Entry<Inheriter<A, B>, B> entry : entrySet()) {
			entry.getKey().restore(entry.getValue());
		}
	}
}
