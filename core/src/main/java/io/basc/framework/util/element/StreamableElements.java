package io.basc.framework.util.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.util.Assert;

public class StreamableElements<E> extends SerializableElements<E> implements Elements<E> {
	private static final long serialVersionUID = 1L;
	private final transient Streamable<E> streamable;

	public StreamableElements(Streamable<E> streamable) {
		Assert.requiredArgument(streamable != null, "streamble");
		this.streamable = streamable;
	}

	@Override
	protected ArrayList<E> create() {
		return streamable.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public Iterator<E> iterator() {
		return super.iterator();
	}

	@Override
	public Stream<E> stream() {
		if (streamable != null) {
			return streamable.stream();
		}
		return super.stream();
	}
}
