package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

import lombok.NonNull;
import run.soeasy.framework.core.domain.CustomizePair;
import run.soeasy.framework.core.domain.Pair;

class PairIterator<L, R> implements Iterator<Pair<Sequential<L>, Sequential<R>>> {
	private final Iterator<Sequential<L>> leftIterator;
	private final Iterator<Sequential<R>> rightIterator;

	public PairIterator(@NonNull Iterator<? extends L> leftIterator,
			@NonNull Iterator<? extends R> rightIterator) {
		this.leftIterator = new SequentialIterator<>(leftIterator);
		this.rightIterator = new SequentialIterator<>(rightIterator);
	}

	@Override
	public boolean hasNext() {
		return leftIterator.hasNext() || rightIterator.hasNext();
	}

	@Override
	public Pair<Sequential<L>, Sequential<R>> next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		return new CustomizePair<>(leftIterator.hasNext() ? leftIterator.next() : null,
				rightIterator.hasNext() ? rightIterator.next() : null);
	}
}