package io.basc.framework.util.page;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import io.basc.framework.util.XUtils;

public interface Pageables<K, T> extends Pageable<K, T>{
	Pageables<K, T> jumpTo(K cursorId);
	
	default Pageables<K, T> next() {
		if (!hasNext()) {
			throw new NoSuchElementException(
					"cursorId=" + getCursorId() + ", nextCursorId=" + getNextCursorId() + ", count=" + getCount());
		}
		return jumpTo(getNextCursorId());
	}

	default Stream<Pageables<K, T>> pageables(){
		Iterator<Pageables<K, T>> iterator = new PageablesIterator<>(this);
		return XUtils.stream(iterator);
	}

	/**
	 * 获取所有的数据
	 * 
	 * @return
	 */
	default Stream<T> streamAll() {
		if(hasNext()) {
			Iterator<T> iterator = new IteratorAll<>(this);
			return XUtils.stream(iterator);
		}
		return stream();
	}
}
