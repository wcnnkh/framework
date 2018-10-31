package shuchaowen.core.db.id;

import shuchaowen.core.db.DBManager;
import shuchaowen.core.util.id.IdGenerator;
import shuchaowen.redis.Redis;

public class RedisTableIdGenerator implements IdGenerator<Long>{
	private final Redis redis;
	private final Class<?> tableClass;
	private final String fieldName;
	private volatile RedisIdGernerator idGenerator;
	
	public RedisTableIdGenerator(Redis redis, Class<?> tableClass, String fieldName){
		this.redis = redis;
		this.tableClass = tableClass;
		this.fieldName = fieldName;
	}
	
	public Long next() {
		if(idGenerator == null){
			synchronized (this) {
				if(idGenerator == null){
					Long maxId = DBManager.getDB(tableClass).getMaxLongValue(tableClass, fieldName);
					maxId = maxId == null? 0:maxId;
					idGenerator = new RedisIdGernerator(redis, "IdGenerator#" + tableClass.getName(), maxId);
				}
			}
		}
		return idGenerator.next();
	}

}
