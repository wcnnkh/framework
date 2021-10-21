package io.basc.framework.util.page;


public interface Page<T> extends Pageable<Long, T> {
	/**
	 * 总数
	 * 
	 * @return
	 */
	long getTotal();
	
	/**
	 * 获取当前页码
	 * 
	 * @return
	 */
	default long getPageNumber() {
		return PageSupport.getPageNumber(getCursorId(), getCount());
	}

	/**
	 * 总页数
	 * 
	 * @return
	 */
	default long getPages(){
		return PageSupport.getPages(getTotal(), getCount());
	}

	@Override
	default boolean hasNext() {
		return getPageNumber() < getPages();
	}

	default boolean hasPrevious() {
		return getPageNumber() > 1;
	}

	@Override
	default Long getNextCursorId() {
		Long start = getCursorId();
		if (start == null) {
			start = 0L;
		}
		return PageSupport.getNextStart(start, getCount());
	}
	
	default Page<T> shared(){
		return new SharedPage<>(this);
	}
}
