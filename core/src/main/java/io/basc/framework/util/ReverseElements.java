package io.basc.framework.util;

import java.util.ArrayList;
import java.util.Collections;

public final class ReverseElements<E> extends SerializableElements<E> {
	private static final long serialVersionUID = 1L;
	private transient Elements<E> source;

	public ReverseElements(Elements<E> source) {
		Assert.requiredArgument(source != null, "source");
		this.source = source;
	}

	@Override
	public Elements<E> reverse() {
		return source == null ? super.reverse() : source;
	}

	@Override
	protected ArrayList<E> create() {
		ArrayList<E> list = new ArrayList<>(source.toList());
		Collections.reverse(list);
		return list;
	}
}
