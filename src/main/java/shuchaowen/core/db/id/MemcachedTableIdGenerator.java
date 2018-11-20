package shuchaowen.core.db.id;

import shuchaowen.core.db.DBManager;
import shuchaowen.core.util.id.IdGenerator;
import shuchaowen.memcached.Memcached;

public class MemcachedTableIdGenerator implements IdGenerator<Long>{
	private final Memcached memcached;
	private final Class<?> tableClass;
	private final String fieldName;
	private volatile MemcachedIdGenerator idGenerator;
	private final String key;
	
	public MemcachedTableIdGenerator(Class<?> tableClass, Memcached memcached, String fieldName){
		this.memcached = memcached;
		this.fieldName = fieldName;
		this.tableClass = tableClass;
		this.key = "IdGenerator_" + tableClass.getName() + "_" +fieldName;
	}
	
	private boolean isInit(){
		return idGenerator != null && memcached.get(key) != null;
	}
	
	public Long next() {
		if(!isInit()){
			synchronized (this) {
				if(!isInit()){
					Long maxId = DBManager.getDB(tableClass).getMaxLongValue(tableClass, fieldName);
					maxId = maxId == null? 0:maxId;
					idGenerator = new MemcachedIdGenerator(memcached, key, maxId);
				}
			}
		}
		return idGenerator.next();
	}
}
