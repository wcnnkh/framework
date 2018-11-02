package shuchaowen.core.db;

import java.util.Collection;

public interface MultipleOperations {
	MultipleOperations save(Object ...beans);
	
	MultipleOperations update(Object ...beans);
	
	MultipleOperations delete(Object ...beans);
	
	MultipleOperations saveOrUpdate(Object ...beans);
	
	Collection<OperationBean> getOperationBeans();
}
