package io.basc.framework.util;

import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;

/**
 * @see #hasPrevious() 暂不支持原始迭代器的反向迭代
 * @author wcnnkh
 *
 * @param <E>
 */
public final class FilterableCursor<E> extends AbstractCursor<E, FilterableCursor<E>> {
	private final ReversibleIterator<? extends E> iterator;
	private final Predicate<? super E> startPredicate;
	private final Predicate<? super E> endPredicate;
	private final Predicate<? super E> predicate;
	private boolean hasStart = false;
	private boolean hasEnd = false;
	private Supplier<E> current;
	private BigInteger start;
	private BigInteger position;
	private final BigInteger count;

	public FilterableCursor(ReversibleIterator<? extends E> iterator, BigInteger start, @Nullable BigInteger count,
			@Nullable RunnableProcessor<? extends RuntimeException> closeProcessor) {
		this(iterator, start, count, null, null, null, closeProcessor);
	}

	public FilterableCursor(ReversibleIterator<? extends E> iterator, @Nullable Predicate<? super E> predicate,
			@Nullable RunnableProcessor<? extends RuntimeException> closeProcessor) {
		this(iterator, BigInteger.ZERO, null, predicate, null, null, closeProcessor);
	}

	public FilterableCursor(ReversibleIterator<? extends E> iterator, @Nullable Predicate<? super E> startPredicate,
			@Nullable Predicate<? super E> endPredicate,
			@Nullable RunnableProcessor<? extends RuntimeException> closeProcessor) {
		this(iterator, BigInteger.ZERO, null, null, startPredicate, endPredicate, closeProcessor);
	}

	public FilterableCursor(ReversibleIterator<? extends E> iterator, BigInteger start, @Nullable BigInteger count,
			@Nullable Predicate<? super E> predicate, @Nullable Predicate<? super E> startPredicate,
			@Nullable Predicate<? super E> endPredicate,
			@Nullable RunnableProcessor<? extends RuntimeException> closeProcessor) {
		super(closeProcessor);
		Assert.requiredArgument(iterator != null, "iterator");
		Assert.requiredArgument(start != null && start.compareTo(BigInteger.ZERO) >= 0, "start");
		Assert.requiredArgument(count == null || count.compareTo(BigInteger.ZERO) >= 0, "count");
		this.iterator = iterator;
		this.startPredicate = startPredicate;
		this.endPredicate = endPredicate;
		this.predicate = predicate;
		this.start = start;
		this.count = count;
	}

	private void initPosition() {
		if (position == null) {
			for (BigInteger i = BigInteger.ZERO; start.compareTo(i) > 0; i = i.add(BigInteger.ONE)) {
				if (!iterator.hasNext()) {
					break;
				}

				E e = iterator.next();
				if (!hasStart && hasEnd && predicate != null && !predicate.test(e)) {
					continue;
				}

				if (!hasStart && startPredicate != null && startPredicate.test(e)) {
					hasStart = true;
				}

				if (!hasEnd && endPredicate != null && endPredicate.test(e)) {
					hasEnd = true;
				}
			}
			position = BigInteger.ZERO;
		}
	}

	@Override
	public boolean hasNext() {
		if (current != null) {
			return true;
		}

		if (hasEnd) {
			return false;
		}

		initPosition();
		if (count != null && position.compareTo(count) >= 0) {
			return false;
		}

		if (startPredicate == null && endPredicate == null && predicate == null) {
			return iterator.hasNext();
		}

		while (iterator.hasNext()) {
			if (predicate == null && hasStart) {
				return true;
			}

			E e = iterator.next();
			if (predicate != null && !predicate.test(e)) {
				continue;
			}

			if (startPredicate != null && startPredicate.test(e)) {
				hasStart = true;
			}

			if (endPredicate != null && endPredicate.test(e)) {
				hasEnd = true;
				return false;
			}

			current = () -> e;
			return true;
		}
		return false;
	}

	@Override
	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		try {
			if (predicate == null && startPredicate == null && endPredicate == null) {
				return iterator.next();
			} else {
				try {
					return current.get();
				} finally {
					current = null;
				}
			}
		} finally {
			this.position = position.add(BigInteger.ZERO);
		}
	}

	@Override
	public boolean hasPrevious() {
		initPosition();
		if (position.compareTo(BigInteger.ZERO) <= 0) {
			return false;
		}
		return iterator.hasPrevious();
	}

	@Override
	public E previous() {
		if (!hasPrevious()) {
			throw new NoSuchElementException();
		}

		try {
			return iterator.previous();
		} finally {
			this.position = position.subtract(BigInteger.ZERO);
		}
	}

	@Override
	public BigInteger getPosition() {
		return position == null ? BigInteger.ZERO : position;
	}
}
