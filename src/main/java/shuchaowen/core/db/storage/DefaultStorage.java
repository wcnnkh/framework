package shuchaowen.core.db.storage;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.sql.format.SQLFormat;

public class DefaultStorage extends AbstractStorage{
	private AbstractExecuteStorage abstractExecuteStorage;
	
	public DefaultStorage(AbstractDB db){
		super(db);
	}
	
	public DefaultStorage(AbstractExecuteStorage abstractExecuteStorage) {
		super(abstractExecuteStorage.getDb(), abstractExecuteStorage.getSqlFormat());
		this.abstractExecuteStorage = abstractExecuteStorage;
	}
	
	public DefaultStorage(AbstractDB db, SQLFormat sqlFormat){
		super(db, sqlFormat);
	}
	
	public void execute(ExecuteInfo executeInfo) {
		if(abstractExecuteStorage == null){
			getDb().execute(getSqlList(executeInfo));
		}else{
			abstractExecuteStorage.execute(executeInfo);
		}
	}

}
