package io.basc.framework.util;

import java.util.Iterator;
import java.util.stream.Stream;

public class MultiElements<E> implements Elements<E> {
	private final Elements<? extends Elements<? extends E>> elements;

	public MultiElements(Iterable<? extends Elements<? extends E>> elements) {
		Assert.requiredArgument(elements != null, "elements");
		this.elements = Elements.of(elements);
	}

	public MultiElements(Elements<? extends Elements<? extends E>> elements) {
		Assert.requiredArgument(elements != null, "elements");
		this.elements = elements;
	}

	@Override
	public Stream<E> stream() {
		return elements.stream().flatMap((e) -> e.stream());
	}

	@Override
	public Iterator<E> iterator() {
		return CollectionUtils.iterator(elements.iterator(), (e) -> e.iterator());
	}

}
