package shuchaowen.core.pagination;

import java.util.List;

public class CommonPagination<T> extends AbstractPagination<T>{
	private static final long serialVersionUID = 1L;
	private long totalCount;
	private List<T> list;
	
	public CommonPagination(){};//为了序列化
	
	public CommonPagination(long total, long limit, List<T> list){
		super(limit);
		this.totalCount = total;
		this.list = list;
	}
	
	public long getMaxPage(){
		if(getTotalCount() <= getLimit()){
			return 1;
		}
		return (long) Math.ceil(((double)getTotalCount())/getLimit());
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
