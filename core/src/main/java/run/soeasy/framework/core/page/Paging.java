package run.soeasy.framework.core.page;

import java.util.NoSuchElementException;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.LinkedIterator;

/**
 * 分页
 * 
 * @author soeasy.run
 *
 * @param <K>
 * @param <V>
 */
public interface Paging<K, V> extends Pageable<K, V>, PagingQuery<K, Paging<K, V>> {
	/**
	 * 是否已知总数,未知数量的分页在调用getTotal使用的是代价最大的循环计数
	 * 
	 * @return
	 */
	boolean isKnowTotal();

	/**
	 * 总数量
	 * 
	 * @return
	 */
	long getTotal();

	/**
	 * 在已知total的情况下几乎无代价，
	 * 
	 * @return 总页数, 如果pageSize未知会抛出分母为0异常
	 */
	default long getPages() {
		return (long) Math.ceil((double) getTotal() / getPageSize());
	}

	/**
	 * 每页数量
	 * 
	 * @return 如果为0说明每页数量不确定
	 */
	int getPageSize();

	/**
	 * 下一页
	 * 
	 * @return
	 */
	default Paging<K, V> nextPage() {
		if (!hasNextPage()) {
			throw new NoSuchElementException("There is no next page");
		}
		return jumpTo(getNextCursorId());
	}

	/**
	 * 所有页
	 * 
	 * @return
	 */
	default Elements<Paging<K, V>> pages() {
		return Elements.of(() -> new LinkedIterator<>(this, Paging::hasNextPage, Paging::nextPage));
	}

	/**
	 * 跳转
	 * 
	 * @param cursorId
	 * @return
	 */
	default Paging<K, V> jumpTo(K cursorId) {
		return query(cursorId, getPageSize());
	}
}
