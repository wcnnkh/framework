package scw.common;

import java.io.Serializable;

/**
 * 可用于分页
 * @author shuchaowen
 * @param <T>
 */
public class Pagination<T> implements Serializable{
	private static final long serialVersionUID = 1511962074546668955L;
	private int limit;
	private long totalCount;
	private T data;
	
	public Pagination(){};
	
	public Pagination(long total, int limit, T data){
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

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
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
