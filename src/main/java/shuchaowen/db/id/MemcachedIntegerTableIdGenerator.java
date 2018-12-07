package shuchaowen.db.id;

import shuchaowen.common.IdGenerator;
import shuchaowen.db.DBManager;
import shuchaowen.memcached.Memcached;

public class MemcachedIntegerTableIdGenerator implements IdGenerator<Integer>{
	private final Memcached memcached;
	private final Class<?> tableClass;
	private final String fieldName;
	private volatile IdGenerator<Integer> idGenerator;
	private final String key;
	private final boolean checkKey;
	
	public MemcachedIntegerTableIdGenerator(Class<?> tableClass, Memcached memcached, String fieldName){
		this(tableClass, memcached, fieldName, true);
	}
	
	/**
	 * @param tableClass
	 * @param memcached
	 * @param fieldName
	 * @param checkKey 是否每次都检查key是否存在
	 */
	public MemcachedIntegerTableIdGenerator(Class<?> tableClass, Memcached memcached, String fieldName, boolean checkKey){
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
	
	public Integer next() {
		if(!isInit()){
			synchronized (this) {
				if(!isInit()){
					Integer maxId = DBManager.getDB(tableClass).getMaxIntValue(tableClass, fieldName);
					maxId = maxId == null? 0:maxId;
					idGenerator = new MemcachedIntegerIdGenerator(memcached, key, maxId);
				}
			}
		}
		return idGenerator.next();
	}
}
