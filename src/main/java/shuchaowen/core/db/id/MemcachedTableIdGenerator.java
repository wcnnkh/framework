package shuchaowen.core.db.id;

import shuchaowen.common.IdGenerator;
import shuchaowen.core.db.DBManager;
import shuchaowen.memcached.Memcached;

public class MemcachedTableIdGenerator implements IdGenerator<Long>{
	private final Memcached memcached;
	private final Class<?> tableClass;
	private final String fieldName;
	private volatile MemcachedIdGenerator idGenerator;
	private final String key;
	private final boolean checkKey;
	
	public MemcachedTableIdGenerator(Class<?> tableClass, Memcached memcached, String fieldName){
		this(tableClass, memcached, fieldName, true);
	}
	
	/**
	 * @param tableClass
	 * @param memcached
	 * @param fieldName
	 * @param checkKey 是否每次都检查key是否存在
	 */
	public MemcachedTableIdGenerator(Class<?> tableClass, Memcached memcached, String fieldName, boolean checkKey){
		this.memcached = memcached;
		this.fieldName = fieldName;
		this.tableClass = tableClass;
		this.key = "IdGenerator_" + tableClass.getName() + "_" +fieldName;
		this.checkKey = checkKey;
	}
	
	private boolean isInit(){
		if(idGenerator == null){
			return false;
		}
		
		if(checkKey){
			return memcached.get(key) != null;
		}
		return true;
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
