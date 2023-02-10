package io.basc.framework.util;

final class InvertedSuccessive<E> implements ReversibleIterator<E> {
	private final ReversibleIterator<E> successive;

	public InvertedSuccessive(ReversibleIterator<E> successive) {
		this.successive = successive;
	}

	@Override
	public boolean hasNext() {
		return successive.hasPrevious();
	}

	@Override
	public E next() {
		return successive.previous();
	}

	@Override
	public boolean hasPrevious() {
		return successive.hasNext();
	}

	@Override
	public E previous() {
		return successive.next();
	}

	@Override
	public ReversibleIterator<E> invert() {
		return successive;
	}
}
