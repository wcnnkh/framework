package shuchaowen.core.db.id;

import shuchaowen.common.IdGenerator;
import shuchaowen.common.LongIdGenerator;
import shuchaowen.core.db.DBManager;

public class TableLongIdGenerator implements IdGenerator<Long>{
	private Class<?> tableClass;
	private String fieldName;
	private int serverId = 0;
	private int maxServerId = 1;
	private volatile LongIdGenerator idGenerator;
	
	public TableLongIdGenerator(Class<?> tableClass, String fieldName) {
		this(tableClass, fieldName, 0, 1);
	}
	
	public TableLongIdGenerator(Class<?> tableClass, String fieldName, int serverId, int maxServerId) {
		this.tableClass = tableClass;
		this.fieldName = fieldName;
		this.serverId = serverId;
		this.maxServerId = maxServerId;

	}
	
	public Long next() {
		if(idGenerator == null){
			synchronized (this) {
				if(idGenerator == null){
					Long maxId = DBManager.getDB(tableClass).getMaxLongValue(tableClass, fieldName);
					maxId = maxId == null? 0:maxId;
					idGenerator = new LongIdGenerator(serverId, maxServerId, maxId);
				}
			}
		}
		return idGenerator.next();
	}
}
