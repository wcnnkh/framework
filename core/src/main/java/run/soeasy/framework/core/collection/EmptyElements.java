package run.soeasy.framework.core.collection;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

class EmptyElements<E> extends EmptyStreamable<E> implements Elements<E>, Listable<E> {
	private static final long serialVersionUID = 1L;
	static final EmptyElements<Object> EMPTY_ELEMENTS = new EmptyElements<>();

	@Override
	public Provider<E> cacheable() {
		return Provider.empty();
	}

	@Override
	public Elements<E> clone() {
		return this;
	}

	@Override
	public Elements<E> filter(Predicate<? super E> predicate) {
		return this;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public Iterator<E> iterator() {
		return Collections.emptyIterator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> Elements<U> map(Function<? super E, ? extends U> mapper) {
		return (Elements<U>) this;
	}

	@Override
	public Elements<E> reverse() {
		return this;
	}

	@Override
	public Stream<E> stream() {
		return Stream.empty();
	}

	@Override
	public ListElementsWrapper<E, ?> toList() {
		return new StandardListElements<>(Collections.emptyList());
	}

	@Override
	public SetElementsWrapper<E, ?> toSet() {
		return new StandardSetElements<>(Collections.emptySet());
	}

	@Override
	public final boolean hasElements() {
		return false;
	}

	@Override
	public final Elements<E> getElements() {
		return this;
	}
}