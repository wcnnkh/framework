package run.soeasy.framework.core.streaming;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.streaming.ZipIterator.Rule;

@RequiredArgsConstructor
@Getter
public class ZipStreamable<L, R, E> implements Streamable<E> {
	@NonNull
	private final Streamable<? extends L> leftStreamable;
	@NonNull
	private final Streamable<? extends R> rightStreamable;
	@NonNull
	private final Rule rule;
	@NonNull
	private final BiFunction<? super L, ? super R, ? extends E> combiner;

	@Override
	public Stream<E> stream() {
		Stream<? extends L> leftStream = leftStreamable.stream();
		try {
			Stream<? extends R> rightStream = rightStreamable.stream();
			try {
				Iterator<? extends L> leftIter = leftStream.iterator();
				Iterator<? extends R> rightIter = rightStream.iterator();
				Iterator<E> zipIterator = new ZipIterator<>(leftIter, rightIter, rule, combiner);
				Stream<E> resultStream = StreamSupport
						.stream(Spliterators.spliteratorUnknownSize(zipIterator, Spliterator.ORDERED), false);
				resultStream = resultStream.onClose(() -> ObjectUtils.closeQuietly(leftStream));
				resultStream = resultStream.onClose(() -> ObjectUtils.closeQuietly(rightStream));
				return resultStream;
			} catch (Exception e) {
				ObjectUtils.closeQuietly(rightStream);
				throw e;
			}
		} catch (Throwable e) {
			ObjectUtils.closeQuietly(leftStream);
			throw e;
		}
	}

	@Override
	public long count() {
		long leftCount = leftStreamable.count();
		long rightCount = rightStreamable.count();

		switch (rule) {
		case ANY_HAS_NEXT:
			return Math.max(leftCount, rightCount);
		case BOTH_HAS_NEXT:
			return Math.min(leftCount, rightCount);
		case LEFT_FIRST:
			return leftCount + Math.max(0, rightCount - leftCount);
		case RIGHT_FIRST:
			return rightCount + Math.max(0, leftCount - rightCount);
		default:
			return 0;
		}
	}

	@Override
	public boolean isEmpty() {
		if (leftStreamable.isEmpty() && rightStreamable.isEmpty()) {
			return true;
		}

		switch (rule) {
		case ANY_HAS_NEXT:
			return false;
		case BOTH_HAS_NEXT:
			return leftStreamable.isEmpty() || rightStreamable.isEmpty();
		case LEFT_FIRST:
		case RIGHT_FIRST:
			return leftStreamable.isEmpty() && rightStreamable.isEmpty();
		default:
			return true;
		}
	}

	@Override
	public Streamable<E> reload() {
		return new ZipStreamable<>(leftStreamable.reload(), rightStreamable.reload(), rule, combiner);
	}
}