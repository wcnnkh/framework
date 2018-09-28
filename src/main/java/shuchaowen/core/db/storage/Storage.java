package shuchaowen.core.db.storage;

import java.util.Collection;
import java.util.List;

import shuchaowen.core.db.ConnectionOrigin;
import shuchaowen.core.db.sql.format.SQLFormat;

public interface Storage {
	<T> T getById(ConnectionOrigin connectionOrigin, SQLFormat sqlFormat, Class<T> type, Object ...params);
	
	<T> List<T> getByIdList(ConnectionOrigin connectionOrigin, SQLFormat sqlFormat, Class<T> type, Object ...params);
	
	void save(Collection<Object> beans, ConnectionOrigin connectionOrigin, SQLFormat sqlFormat);
	
	void update(Collection<Object> beans, ConnectionOrigin connectionOrigin, SQLFormat sqlFormat);
	
	void delete(Collection<Object> beans, ConnectionOrigin connectionOrigin, SQLFormat sqlFormat);
	
	void saveOrUpdate(Collection<Object> beans, ConnectionOrigin connectionOrigin, SQLFormat sqlFormat);
	
	void incr(Object obj, String field, double limit, Double maxValue, ConnectionOrigin connectionOrigin, SQLFormat sqlFormat);
	
	void decr(Object obj, String field, double limit, Double minValue, ConnectionOrigin connectionOrigin, SQLFormat sqlFormat);
}
