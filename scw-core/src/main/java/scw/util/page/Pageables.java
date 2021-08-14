package scw.util.page;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Pageables<K, T> extends Pageable<K, T>{
	Pageables<K, T> jumpTo(K cursorId);

	default Pageable<K, T> shared() {
		return new SharedPageable<K, T>(getCursorId(), rows(), getNextCursorId(), getCount(), hasNext());
	}
	
	default Pageables<K, T> next() {
		if (!hasNext()) {
			throw new NoSuchElementException(
					"cursorId=" + getCursorId() + ", nextCursorId=" + getNextCursorId() + ", count=" + getCount());
		}
		return jumpTo(getNextCursorId());
	}

	default Stream<Pageables<K, T>> pageables(){
		Iterator<Pageables<K, T>> iterator = new PageablesIterator<>(this);
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
	}

	/**
	 * 获取所有的数据
	 * 
	 * @return
	 */
	default Stream<T> streamAll() {
		if(hasNext()) {
			Iterator<T> iterator = new IteratorAll<>(this);
			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
		}
		return stream();
	}
}
