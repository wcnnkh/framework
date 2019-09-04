package scw.core;

import java.io.Serializable;

/**
 * 可用于分页
 * 
 * @author shuchaowen
 * @param <T>
 */
public class Pagination<T> implements Serializable {
	private static final long serialVersionUID = 1511962074546668955L;
	private int limit;
	private long totalCount;
	private T data;

	public Pagination() {
	};

	public Pagination(long total, int limit, T data) {
		this.totalCount = total;
		this.limit = limit;
		this.data = data;
	}

	public int getMaxPage() {
		return (int) getLongMaxPage();
	}

	public long getLongMaxPage() {
		if (totalCount <= limit) {
			return 1;
		}
		return (long) Math.ceil(((double) totalCount) / limit);
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getTotalCount() {
		return (int) getLongTotalCount();
	}

	public long getLongTotalCount() {
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

	public static int getBegin(long page, int limit) {
		return (int) getLongBegin(page, limit);
	}

	public static long getLongBegin(long page, int limit) {
		return Math.max(1, (page - 1)) * limit;
	}
}
