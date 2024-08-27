package io.basc.framework.util;

import java.util.Iterator;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class StandardStreamableElements<E, W extends Streamable<E>> implements StreamableElementsWrapper<E, W> {
	private final W source;

	@Override
	public Stream<E> stream() {
		return source.stream();
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
