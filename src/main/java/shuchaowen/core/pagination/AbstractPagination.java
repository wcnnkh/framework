package shuchaowen.core.pagination;

import java.io.Serializable;

public abstract class AbstractPagination<T> implements Pagination<T>, Serializable{
	private static final long serialVersionUID = 1L;
	private long limit;
	
	public AbstractPagination(){};//为了序列化
	
	public AbstractPagination(long limit){
		this.limit = limit;
	}
	
	public long getMaxPage(){
		if(getTotalCount() <= getLimit()){
			return 1;
		}
		return (long) Math.ceil(((double)getTotalCount())/getLimit());
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}
}
