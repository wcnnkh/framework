package scw.mvc.logger.db;

import java.util.Map.Entry;

import scw.core.reflect.CloneUtils;
import scw.core.utils.CollectionUtils;
import scw.db.AsyncExecute;
import scw.db.DB;
import scw.mvc.logger.Log;

public class LogAsyncSave implements AsyncExecute {
	private static final long serialVersionUID = 1L;
	private Log log;

	public LogAsyncSave(Log log) {
		this.log = log;
	}

	public void execute(DB db) {
		LogTable logTable = CloneUtils.copy(log, LogTable.class);
		db.save(logTable);
		
		if(!CollectionUtils.isEmpty(log.getAttributeMap())){
			for(Entry<String, String> entry : log.getAttributeMap().entrySet()){
				if(entry.getKey() == null || entry.getValue() == null){
					continue;
				}
				
				LogAttributeTable logAttributeTable = new LogAttributeTable();
				logAttributeTable.setLogId(logTable.getLogId());
				logAttributeTable.setName(entry.getKey());
				logAttributeTable.setValue(entry.getValue());
				db.save(logAttributeTable);
			}
		}
	}
}
