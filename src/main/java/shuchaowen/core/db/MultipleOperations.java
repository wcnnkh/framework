package shuchaowen.core.db;

import java.util.Collection;

public interface MultipleOperations extends Collection<OperationBean>{
	MultipleOperations save(Object ...beans);
	
	MultipleOperations update(Object ...beans);
	
	MultipleOperations delete(Object ...beans);
	
	MultipleOperations saveOrUpdate(Object ...beans);
	
	void commit(DB db);
}
