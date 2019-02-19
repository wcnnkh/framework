package scw.utils.id.db;

import scw.db.DB;
import scw.db.DBManager;
import scw.memcached.Memcached;
import scw.utils.id.IdGenerator;
import scw.utils.id.MemcachedIdGenerator;

public class MemcachedTableIdGenerator implements IdGenerator<Long> {
	private final Memcached memcached;
	private final Class<?> tableClass;
	private final String fieldName;
	private volatile MemcachedIdGenerator idGenerator;
	private final String key;
	private final boolean checkKey;
	private DB db;

	public MemcachedTableIdGenerator(Class<?> tableClass, Memcached memcached,
			String fieldName) {
		this(tableClass, memcached, fieldName, true);
	}

	public MemcachedTableIdGenerator(DB db, Class<?> tableClass,
			Memcached memcached, String fieldName) {
		this(tableClass, db, memcached, fieldName, true);
	}

	/**
	 * @param tableClass
	 * @param memcached
	 * @param fieldName
	 * @param checkKey
	 *            是否每次都检查key是否存在
	 */
	public MemcachedTableIdGenerator(Class<?> tableClass, Memcached memcached,
			String fieldName, boolean checkKey) {
		this.memcached = memcached;
		this.fieldName = fieldName;
		this.tableClass = tableClass;
		this.key = "IdGenerator_" + tableClass.getName() + "_" + fieldName;
		this.checkKey = checkKey;
	}

	public MemcachedTableIdGenerator(Class<?> tableClass, DB db,
			Memcached memcached, String fieldName, boolean checkKey) {
		this.memcached = memcached;
		this.fieldName = fieldName;
		this.tableClass = tableClass;
		this.key = "IdGenerator_" + tableClass.getName() + "_" + fieldName;
		this.checkKey = checkKey;
		this.db = db;
	}

	private boolean isInit() {
		if (idGenerator == null) {
			return false;
		}

		if (checkKey) {
			return memcached.get(key) != null;
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
					idGenerator = new MemcachedIdGenerator(memcached, key,
							maxId);
				}
			}
		}
		return idGenerator.next();
	}
}
