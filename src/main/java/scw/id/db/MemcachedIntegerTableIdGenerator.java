package scw.id.db;

import scw.db.DB;
import scw.db.DBManager;
import scw.id.IdGenerator;
import scw.memcached.Memcached;
import scw.memcached.MemcachedIntegerIdGenerator;

public class MemcachedIntegerTableIdGenerator implements IdGenerator<Integer> {
	private final Memcached memcached;
	private final Class<?> tableClass;
	private final String fieldName;
	private volatile IdGenerator<Integer> idGenerator;
	private final String key;
	private final boolean checkKey;
	private DB db;

	public MemcachedIntegerTableIdGenerator(Class<?> tableClass,
			Memcached memcached, String fieldName) {
		this(tableClass, memcached, fieldName, true);
	}

	public MemcachedIntegerTableIdGenerator(DB db, Class<?> tableClass,
			Memcached memcached, String fieldName) {
		this(db, tableClass, memcached, fieldName, true);
	}

	/**
	 * @param tableClass
	 * @param memcached
	 * @param fieldName
	 * @param checkKey
	 *            是否每次都检查key是否存在
	 */
	public MemcachedIntegerTableIdGenerator(Class<?> tableClass,
			Memcached memcached, String fieldName, boolean checkKey) {
		this.memcached = memcached;
		this.fieldName = fieldName;
		this.tableClass = tableClass;
		this.key = "IdGenerator_" + tableClass.getName() + "_" + fieldName;
		this.checkKey = checkKey;
	}

	public MemcachedIntegerTableIdGenerator(DB db, Class<?> tableClass,
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

	public Integer next() {
		if (!isInit()) {
			synchronized (this) {
				if (!isInit()) {
					if (db == null) {
						db = DBManager.getDB(tableClass);
					}

					Integer maxId = db.getMaxIntValue(tableClass, fieldName);
					maxId = maxId == null ? 0 : maxId;
					idGenerator = new MemcachedIntegerIdGenerator(memcached,
							key, maxId);
				}
			}
		}
		return idGenerator.next();
	}
}
