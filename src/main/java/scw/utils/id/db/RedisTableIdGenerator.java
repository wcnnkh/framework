package scw.utils.id.db;

import scw.db.DB;
import scw.db.DBManager;
import scw.redis.Redis;
import scw.utils.id.IdGenerator;
import scw.utils.id.RedisIdGenerator;

public final class RedisTableIdGenerator implements IdGenerator<Long> {
	private final Redis redis;
	private final Class<?> tableClass;
	private final String fieldName;
	private volatile RedisIdGenerator idGenerator;
	private final String key;
	private final boolean checkKey;
	private DB db;

	public RedisTableIdGenerator(Redis redis, Class<?> tableClass,
			String fieldName) {
		this(redis, tableClass, fieldName, true);
	}

	public RedisTableIdGenerator(Redis redis, DB db, Class<?> tableClass,
			String fieldName) {
		this(redis, tableClass, db, fieldName, true);
	}

	/**
	 * @param redis
	 * @param tableClass
	 * @param fieldName
	 * @param checkKey
	 *            是否每次都检查key是否存在
	 */
	public RedisTableIdGenerator(Redis redis, Class<?> tableClass,
			String fieldName, boolean checkKey) {
		this.redis = redis;
		this.tableClass = tableClass;
		this.fieldName = fieldName;
		this.key = "IdGenerator_" + tableClass.getName() + "_" + fieldName;
		this.checkKey = checkKey;
	}

	public RedisTableIdGenerator(Redis redis, Class<?> tableClass, DB db,
			String fieldName, boolean checkKey) {
		this.redis = redis;
		this.tableClass = tableClass;
		this.fieldName = fieldName;
		this.key = "IdGenerator_" + tableClass.getName() + "_" + fieldName;
		this.checkKey = checkKey;
		this.db = db;
	}

	private boolean isInit() {
		if (idGenerator == null) {
			return false;
		}

		if (checkKey) {
			return redis.exists(key);
		}
		return true;
	}

	public Long next() {
		if (!isInit()) {
			synchronized (this) {
				if (!isInit()) {
					if (db == null) {
						db = DBManager.getDB(tableClass);
					}
					Long maxId = db.getMaxLongValue(tableClass, fieldName);
					maxId = maxId == null ? 0 : maxId;
					idGenerator = new RedisIdGenerator(redis, key, maxId);
				}
			}
		}
		return idGenerator.next();
	}

}
