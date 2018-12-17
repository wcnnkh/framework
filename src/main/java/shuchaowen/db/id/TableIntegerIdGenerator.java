package shuchaowen.db.id;

import shuchaowen.common.IdGenerator;
import shuchaowen.common.IntegerIdGenerator;
import shuchaowen.db.DB;
import shuchaowen.db.DBManager;

public class TableIntegerIdGenerator implements IdGenerator<Integer> {
	private Class<?> tableClass;
	private String fieldName;
	private int serverId;
	private int maxServerId = 1;
	private DB db;
	private volatile IntegerIdGenerator idGenerator;

	public TableIntegerIdGenerator(Class<?> tableClass, String fieldName) {
		this(tableClass, fieldName, 0, 1);
	}

	public TableIntegerIdGenerator(Class<?> tableClass, DB db, String fieldName) {
		this(tableClass, db, fieldName, 0, 1);
	}

	public TableIntegerIdGenerator(Class<?> tableClass, String fieldName,
			int serverId, int maxServerId) {
		this.tableClass = tableClass;
		this.fieldName = fieldName;
		this.serverId = serverId;
		this.maxServerId = maxServerId;
	}

	public TableIntegerIdGenerator(Class<?> tableClass, DB db,
			String fieldName, int serverId, int maxServerId) {
		this.tableClass = tableClass;
		this.fieldName = fieldName;
		this.serverId = serverId;
		this.maxServerId = maxServerId;
		this.db = db;
	}

	public Integer next() {
		if (idGenerator == null) {
			synchronized (this) {
				if (idGenerator == null) {
					if (db == null) {
						db = DBManager.getDB(tableClass);
					}

					Integer maxId = db.getMaxIntValue(tableClass, fieldName);
					maxId = maxId == null ? 0 : maxId;
					idGenerator = new IntegerIdGenerator(serverId, maxServerId,
							maxId);
				}
			}
		}
		return idGenerator.next();
	}
}
