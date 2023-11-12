package io.basc.framework.util.element;

import java.io.Serializable;
import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.util.Streams;
import lombok.Data;

@Data
public class ParallelElements<L, R> implements Elements<ParallelElement<L, R>>, Serializable {
	private static final long serialVersionUID = 1L;
	private final Elements<? extends L> leftElements;
	private final Elements<? extends R> rightElements;

	@Override
	public Stream<ParallelElement<L, R>> stream() {
		return Streams.stream(iterator());
	}

	@Override
	public Iterator<ParallelElement<L, R>> iterator() {
		return new ParallelElementIterator<>(leftElements.iterator(), rightElements.iterator());
	}
}
