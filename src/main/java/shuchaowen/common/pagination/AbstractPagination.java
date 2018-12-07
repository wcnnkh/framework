package shuchaowen.common.pagination;

import java.io.Serializable;

public abstract class AbstractPagination<T> implements Pagination<T>, Serializable{
	private static final long serialVersionUID = 1L;
	private int limit;
	
	public AbstractPagination(){};//为了序列化
	
	public AbstractPagination(int limit){
		this.limit = limit;
	}
	
	public long getMaxPage(){
		if(getTotalCount() <= getLimit()){
			return 1;
		}
		return (long) Math.ceil(((double)getTotalCount())/getLimit());
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}
