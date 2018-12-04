package shuchaowen.core.pagination;

import java.util.List;

public interface Pagination<T>{
	long getMaxPage();
	
	long getTotalCount();
	
	long getLimit();
	
	List<T> getList();
}
