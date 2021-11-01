package io.basc.framework.util.stream;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

import io.basc.framework.util.ObjectUtils;

public class CursorSpliterator<T> implements Spliterator<T> {
	private final Spliterator<T> spliterator;
	private final CursorPosition cursorPosition;

	public CursorSpliterator(Spliterator<T> spliterator) {
		this(spliterator, 0);
	}

	public CursorSpliterator(Spliterator<T> spliterator, long position) {
		this(spliterator, new ParallelCursorPosition(position));
	}

	public CursorSpliterator(Spliterator<T> spliterator, CursorPosition cursorPosition) {
		this.spliterator = spliterator;
		this.cursorPosition = cursorPosition;
	}

	/**
	 * 不应该实现{@link #forEachRemaining(Consumer)},因为大部分的默念实现都是调用{@link #tryAdvance(Consumer)}方法，这样会导致{@link CursorPosition#increment()}执行两次
	 * 
	 * @see #forEachRemaining(Consumer)
	 */
	@Override
	public boolean tryAdvance(Consumer<? super T> action) {
		if (spliterator.tryAdvance(action)) {
			cursorPosition.increment();
			return true;
		}
		return false;
	}

	@Override
	public Spliterator<T> trySplit() {
		Spliterator<T> spliterator = this.spliterator.trySplit();
		if (spliterator == null) {
			return null;
		}
		return new CursorSpliterator<T>(spliterator, cursorPosition);
	}

	@Override
	public long estimateSize() {
		return spliterator.estimateSize();
	}

	@Override
	public int characteristics() {
		return spliterator.characteristics();
	}

	@Override
	public Comparator<? super T> getComparator() {
		return spliterator.getComparator();
	}

	@Override
	public long getExactSizeIfKnown() {
		return spliterator.getExactSizeIfKnown();
	}

	@Override
	public boolean hasCharacteristics(int characteristics) {
		return spliterator.hasCharacteristics(characteristics);
	}

	@Override
	public String toString() {
		return spliterator.toString();
	}

	@Override
	public int hashCode() {
		return spliterator.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof CursorSpliterator) {
			return ObjectUtils.nullSafeEquals(spliterator, ((CursorSpliterator<?>) obj).spliterator);
		}
		return false;
	}
}
