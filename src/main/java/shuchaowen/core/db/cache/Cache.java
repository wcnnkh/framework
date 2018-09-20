package shuchaowen.core.db.cache;

public interface Cache {
	<T> T getById(Class<T> type, String tableName, Object ...params);
	
	void save(Object bean);
	
	void delete(Object bean);
	
	void update(Object bean);
	
	void saveOrUpdate(Object bean);
}
