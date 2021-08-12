package scw.util.page;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import scw.core.utils.CollectionUtils;
import scw.lang.Nullable;

public interface Pageable<K, T> extends Iterable<T>{
	/**
	 * 获取当前页的使用的开始游标
	 * 
	 * @return
	 */
	@Nullable
	K getCursorId();

	/**
	 * 分页的限制数据(limit)
	 * 
	 * @return
	 */
	long getCount();

	/**
	 * 获取下一页的开始游标id
	 * 
	 * @return
	 */
	@Nullable
	K getNextCursorId();
	
	List<T> rows();

	/**
	 * 是否还有更多数据
	 * 
	 * @return
	 */
	boolean hasNext();
	
	/**
	 * 获取当前分页的第一条数据
	 * @return
	 */
	default T first(){
		List<T> rows = rows();
		if(CollectionUtils.isEmpty(rows)){
			return null;
		}
		return rows.get(0);
	}
	
	/**
	 * 获取当前分页的最后一条数据
	 * @return
	 */
	default T last(){
		List<T> rows = rows();
		if(CollectionUtils.isEmpty(rows)){
			return null;
		}
		return rows.get(rows.size() - 1);
	}
	
	@Override
	default Iterator<T> iterator() {
		return rows().iterator();
	}
	
	default Stream<T> stream(){
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), 0), false);
	}
}