package io.basc.framework.util.element;

import java.util.Iterator;
import java.util.NoSuchElementException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParallelElementIterator<L, R> implements Iterator<ParallelElement<L, R>> {
	@NonNull
	private final Iterator<? extends L> leftIterator;
	@NonNull
	private final Iterator<? extends R> rightIterator;

	@Override
	public boolean hasNext() {
		return leftIterator.hasNext() || rightIterator.hasNext();
	}

	@Override
	public ParallelElement<L, R> next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		IterativeElement<L> left = leftIterator.hasNext()
				? new IterativeElement<>(leftIterator.next(), leftIterator.hasNext())
				: null;
		IterativeElement<R> right = rightIterator.hasNext()
				? new IterativeElement<>(rightIterator.next(), rightIterator.hasNext())
				: null;
		return new ParallelElement<>(left, right);
	}
}
