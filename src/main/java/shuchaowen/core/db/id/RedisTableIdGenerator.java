package shuchaowen.core.db.id;

import shuchaowen.core.db.DBManager;
import shuchaowen.core.util.id.IdGenerator;
import shuchaowen.redis.Redis;

public class RedisTableIdGenerator implements IdGenerator<Long>{
	private final Redis redis;
	private final Class<?> tableClass;
	private final String fieldName;
	private volatile RedisIdGernerator idGenerator;
	private final String key;
	
	public RedisTableIdGenerator(Redis redis, Class<?> tableClass, String fieldName){
		this.redis = redis;
		this.tableClass = tableClass;
		this.fieldName = fieldName;
		this.key = "IdGenerator_" + tableClass.getName() + "_" +fieldName;
	}
	
	private boolean isInit(){
		return idGenerator != null && redis.exists(key);
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
