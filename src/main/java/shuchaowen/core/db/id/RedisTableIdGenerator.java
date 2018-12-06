package shuchaowen.core.db.id;

import shuchaowen.common.IdGenerator;
import shuchaowen.core.db.DBManager;
import shuchaowen.redis.Redis;

public class RedisTableIdGenerator implements IdGenerator<Long>{
	private final Redis redis;
	private final Class<?> tableClass;
	private final String fieldName;
	private volatile RedisIdGernerator idGenerator;
	private final String key;
	private final boolean checkKey;
	
	public RedisTableIdGenerator(Redis redis, Class<?> tableClass, String fieldName){
		this(redis, tableClass, fieldName, true);
	}
	
	/**
	 * @param redis
	 * @param tableClass
	 * @param fieldName
	 * @param checkKey 是否每次都检查key是否存在
	 */
	public RedisTableIdGenerator(Redis redis, Class<?> tableClass, String fieldName, boolean checkKey){
		this.redis = redis;
		this.tableClass = tableClass;
		this.fieldName = fieldName;
		this.key = "IdGenerator_" + tableClass.getName() + "_" +fieldName;
		this.checkKey = checkKey;
	}
	
	private boolean isInit(){
		if(idGenerator == null){
			return false;
		}
		
		if(checkKey){
			return redis.exists(key);
		}
		return true;
	}
	
	public Long next() {
		if(!isInit()){
			synchronized (this) {
				if(!isInit()){
					Long maxId = DBManager.getDB(tableClass).getMaxLongValue(tableClass, fieldName);
					maxId = maxId == null? 0:maxId;
					idGenerator = new RedisIdGernerator(redis, key, maxId);
				}
			}
		}
		return idGenerator.next();
	}

}
