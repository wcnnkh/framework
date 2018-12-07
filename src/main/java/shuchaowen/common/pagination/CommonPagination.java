package shuchaowen.common.pagination;

import java.util.List;

public class CommonPagination<T> extends AbstractPagination<T>{
	private static final long serialVersionUID = 1L;
	private long totalCount;
	private List<T> list;
	
	public CommonPagination(){};//为了序列化
	
	public CommonPagination(long total, int limit, List<T> list){
		super(limit);
		this.totalCount = total;
		this.list = list;
	}
	
	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	
	public void setList(List<T> list) {
		this.list = list;
	}

	public List<T> getList() {
		return list;
	}
}
