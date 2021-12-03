package io.basc.framework.util.page;

public interface Pagination<T> extends Page<Long, T>{
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
	
	default boolean hasPrevious() {
		return getPageNumber() > 1;
	}
	
	@Override
	default Long getNextCursorId() {
		Long start = getCursorId();
		if(start == null){
			return null;
		}
		
		if(!PageSupport.hasMore(getTotal(), start, getCount())) {
			return null;
		}
		
		return PageSupport.getNextStart(start, getCount());
	}
}
