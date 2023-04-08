package io.basc.framework.util;

import java.util.Collections;
import java.util.Iterator;

public class EmptyElements<E> extends EmptyStreamable<E> implements Elements<E> {
	private static final long serialVersionUID = 1L;
	public static final EmptyElements<Object> INSTANCE = new EmptyElements<>();

	@Override
	public Iterator<E> iterator() {
		return Collections.emptyIterator();
	}

}
