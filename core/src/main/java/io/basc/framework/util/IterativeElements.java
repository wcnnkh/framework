package io.basc.framework.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class IterativeElements<E> implements Elements<IterativeElement<E>>, Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
	private final Elements<E> source;

	@Override
	public Stream<IterativeElement<E>> stream() {
		return Streams.stream(iterator());
	}

	@Override
	public Iterator<IterativeElement<E>> iterator() {
		return new IterativeElementIterator<>(source.iterator());
	}
}
