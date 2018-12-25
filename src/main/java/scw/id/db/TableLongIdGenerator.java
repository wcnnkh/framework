package scw.id.db;

import scw.db.DB;
import scw.db.DBManager;
import scw.id.IdGenerator;
import scw.id.LongIdGenerator;

public class TableLongIdGenerator implements IdGenerator<Long> {
	private Class<?> tableClass;
	private String fieldName;
	private int serverId = 0;
	private int maxServerId = 1;
	private DB db;
	private volatile LongIdGenerator idGenerator;

	public TableLongIdGenerator(Class<?> tableClass, String fieldName) {
		this(tableClass, fieldName, 0, 1);
	}

	public TableLongIdGenerator(Class<?> tableClass, DB db, String fieldName) {
		this(tableClass, db, fieldName, 0, 1);
	}

	public TableLongIdGenerator(Class<?> tableClass, String fieldName,
			int serverId, int maxServerId) {
		this.tableClass = tableClass;
		this.fieldName = fieldName;
		this.serverId = serverId;
		this.maxServerId = maxServerId;
	}

	public TableLongIdGenerator(Class<?> tableClass, DB db, String fieldName,
			int serverId, int maxServerId) {
		this.tableClass = tableClass;
		this.fieldName = fieldName;
		this.serverId = serverId;
		this.maxServerId = maxServerId;
		this.db = db;
	}

	public Long next() {
		if (idGenerator == null) {
			synchronized (this) {
				if (idGenerator == null) {
					if (db == null) {
						db = DBManager.getDB(tableClass);
					}
					Long maxId = db.getMaxLongValue(tableClass, fieldName);
					maxId = maxId == null ? 0 : maxId;
					idGenerator = new LongIdGenerator(serverId, maxServerId,
							maxId);
				}
			}
		}
		return idGenerator.next();
	}
}
