package shuchaowen.core.db;

public interface MultipleOperations {
	MultipleOperations save(Object ...beans);
	
	MultipleOperations update(Object ...beans);
	
	MultipleOperations delete(Object ...beans);
	
	MultipleOperations saveOrUpdate(Object ...beans);
	
	void commit(DB db);
}
