package scw.mvc.action.logger.db;

import java.util.Map.Entry;

import scw.core.utils.CollectionUtils;
import scw.db.AsyncExecute;
import scw.db.DB;
import scw.mapper.Copy;
import scw.mvc.action.logger.ActionLog;

public class LogAsyncSave implements AsyncExecute {
	private static final long serialVersionUID = 1L;
	private ActionLog log;

	public LogAsyncSave(ActionLog log) {
		this.log = log;
	}

	public void execute(DB db) {
		LogTable logTable = Copy.copy(LogTable.class, log);
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
