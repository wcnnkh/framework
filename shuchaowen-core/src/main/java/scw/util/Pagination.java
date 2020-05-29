package scw.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页数据
 * 
 * @author shuchaowen
 * @param <T>
 */
public class Pagination<T> implements Serializable {
	private static final long serialVersionUID = 1511962074546668955L;
	private final int limit;
	private long totalCount;
	private List<T> data;

	public Pagination(int limit) {
		this.limit = limit;
	}

	public Pagination(long total, int limit, List<T> data) {
		this.totalCount = total;
		this.limit = limit;
		this.data = data;
	}

	public int getMaxPage() {
		return (int) getLongMaxPage();
	}

	public long getLongMaxPage() {
		if (getTotalCount() <= limit) {
			return 1;
		}
		return (long) Math.ceil(((double) getTotalCount()) / limit);
	}

	public final int getLimit() {
		return limit;
	}

	public int getTotalCount() {
		return (int) getLongTotalCount();
	}

	public long getLongTotalCount() {
		if(totalCount == 0 && data != null){
			return data.size();
		}
		return totalCount;
	}

	public Pagination<T> setTotalCount(long totalCount) {
		this.totalCount = totalCount;
		return this;
	}

	public List<T> getData() {
		return data;
	}

	/**
	 * 注意：此方法会改变泛型的实际类型，造成泛型不安全，当调用此方法后泛型的类型已经被改变为指定类型
	 * 
	 * @param data
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <D> Pagination<D> setData(List<D> data) {
		Pagination<D> pagination = new Pagination<D>(limit);
		pagination.setTotalCount(getTotalCount());
		pagination.data = data;
		if (getClass().isInstance(pagination)) {
			this.data = (List<T>) data;
		}
		return pagination;
	}
	
	public <D> Pagination<D> emptyData(){
		this.data = Collections.emptyList();
		Pagination<D> pagination = new Pagination<D>(limit);
		pagination.setTotalCount(getTotalCount());
		pagination.data = Collections.emptyList();
		return pagination;
	}

	public static int getBegin(long page, int limit) {
		return (int) getLongBegin(page, limit);
	}

	public static long getLongBegin(long page, int limit) {
		return Math.max(0, (page - 1)) * limit;
	}
}
