package io.basc.framework.util;

import java.util.LinkedHashMap;

public final class InheriterCapture<A, B> extends LinkedHashMap<Inheriter<A, B>, A> {
	private static final long serialVersionUID = 1L;

	public InheriterCapture() {
		super();
	}

	public InheriterCapture(int initialCapacity) {
		super(initialCapacity);
	}

	public InheriterBackup<A, B> replay() {
		InheriterBackup<A, B> backup = new InheriterBackup<>(size());
		for (java.util.Map.Entry<Inheriter<A, B>, A> entry : entrySet()) {
			B b = entry.getKey().replay(entry.getValue());
			backup.put(entry.getKey(), b);
		}
		return backup;
	}
}
