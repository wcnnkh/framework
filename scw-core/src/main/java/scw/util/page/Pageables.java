package scw.util.page;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public interface Pageables<K, T> extends Pageable<K, T>, Iterator<Pageable<K, T>> {
	PageableProcessor<K, T> getProcessor();

	@Override
	default <R> Pageables<K, R> map(Function<? super T, ? extends R> mapper) {
		return new JumpPageables<>(getProcessor(), this, mapper);
	}

	@Override
	default Pageables<K, T> jumpTo(PageableProcessor<K, T> processor, K cursorId) {
		Pageable<K, T> pageable = Pageable.super.jumpTo(processor, cursorId);
		return new JumpPageables<>(processor, pageable, (p) -> p);
	}

	@Override
	default Pageables<K, T> next(PageableProcessor<K, T> processor) {
		Pageable<K, T> pageable = Pageable.super.next(processor);
		return new JumpPageables<>(processor, pageable, (p) -> p);
	}

	default Pageables<K, T> next() {
		if (!hasNext()) {
			throw new NoSuchElementException(
					"cursorId=" + getCursorId() + ", nextCursorId=" + getNextCursorId() + ", count=" + getCount());
		}
		return jumpTo(getNextCursorId());
	}

	default Pageables<K, T> jumpTo(K cursorId) {
		return jumpTo(getProcessor(), cursorId);
	}
}
