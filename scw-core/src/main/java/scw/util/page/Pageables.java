package scw.util.page;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface Pageables<K, T> extends Pageable<K, T>, PageableProcessor<K, T>,
		Iterator<Pageable<K, T>> {
	
	/**
	 * 返回的结果是可以被序列化的
	 * @return
	 */
	default Pageable<K, T> shared(){
		return new SharedPageable<K, T>(getCursorId(), rows(), getNextCursorId(), getCount(), hasNext());
	}
	
	@Override
	default <R> Pageables<K, R> jumpTo(PageableProcessor<K, R> processor, K cursorId) {
		Pageable<K, R> pageable = Pageable.super.jumpTo(processor, cursorId);
		return new JumpPageables<>(pageable, processor);
	}

	@Override
	default <R> Pageables<K, R> next(PageableProcessor<K, R> processor) {
		Pageable<K, R> pageable = Pageable.super.next(processor);
		return new JumpPageables<>(pageable, processor);
	}

	default Pageables<K, T> next() {
		if (!hasNext()) {
			throw new NoSuchElementException("cursorId=" + getCursorId()
					+ ", nextCursorId=" + getNextCursorId() + ", count="
					+ getCount());
		}
		return jumpTo(getNextCursorId());
	}

	default Pageables<K, T> jumpTo(K cursorId) {
		return jumpTo(this, cursorId);
	}
}
