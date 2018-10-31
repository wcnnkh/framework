package shuchaowen.core.db.id;

import shuchaowen.core.db.DBManager;
import shuchaowen.core.util.id.IdGenerator;
import shuchaowen.memcached.Memcached;

public class MemcachedTableIdGenerator implements IdGenerator<Long>{
	private final Memcached memcached;
	private final Class<?> tableClass;
	private final String fieldName;
	private volatile MemcachedIdGenerator idGenerator;
	
	public MemcachedTableIdGenerator(Class<?> tableClass, Memcached memcached, String fieldName){
		this.memcached = memcached;
		this.fieldName = fieldName;
		this.tableClass = tableClass;
	}
	
	
	public Long next() {
		if(idGenerator == null){
			synchronized (this) {
				if(idGenerator == null){
					Long maxId = DBManager.getDB(tableClass).getMaxLongValue(tableClass, fieldName);
					maxId = maxId == null? 0:maxId;
					idGenerator = new MemcachedIdGenerator(memcached, "IdGenerator_" + tableClass.getName(), maxId);
				}
			}
		}
		return idGenerator.next();
	}
}
