package shuchaowen.core.db.id;

import shuchaowen.common.IdGenerator;
import shuchaowen.common.IntegerIdGenerator;
import shuchaowen.core.db.DBManager;

public class TableIntegerIdGenerator implements IdGenerator<Integer>{
	private Class<?> tableClass;
	private String fieldName;
	private int serverId;
	private int maxServerId = 1;
	private volatile IntegerIdGenerator idGenerator;
	
	public TableIntegerIdGenerator(Class<?> tableClass, String fieldName) {
		this(tableClass, fieldName, 0, 1);
	}
	
	public TableIntegerIdGenerator(Class<?> tableClass, String fieldName, int serverId, int maxServerId) {
		this.tableClass = tableClass;
		this.fieldName = fieldName;
		this.serverId = serverId;
		this.maxServerId = maxServerId;
	}
	
	public Integer next() {
		if(idGenerator == null){
			synchronized (this) {
				if(idGenerator == null){
					Integer maxId = DBManager.getDB(tableClass).getMaxIntValue(tableClass, fieldName);
					maxId = maxId == null? 0:maxId;
					idGenerator = new IntegerIdGenerator(serverId, maxServerId, maxId);
				}
			}
		}
		return idGenerator.next();
	}
}
