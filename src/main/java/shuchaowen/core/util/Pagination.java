package shuchaowen.core.util;

import java.io.Serializable;

/**
 * 可用于分页
 * @author shuchaowen
 * @param <T>
 */
public class Pagination<T> implements Serializable{
	private static final long serialVersionUID = 1511962074546668955L;
	private long limit;
	private long totalCount;
	private T data;
	
	public Pagination(){};
	
	public Pagination(long total, long limit, T data){
		this.totalCount = total;
		this.limit = limit;
		this.data = data;
	}
	
	public long getMaxPage(){
		if(totalCount <= limit){
			return 1;
		}
		return (long) Math.ceil(((double)totalCount)/limit);
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
