package shuchaowen.core.db.storage;

import java.util.Collection;
import java.util.List;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.OperationBean;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.PrimaryKeyValue;

public class CommonStorage implements Storage{
	private final AbstractDB db;
	private final Storage getStorage;
	private final Storage opStorage;
	
	public CommonStorage(AbstractDB db, Storage getStorage, Storage opStorage){
		this.db = db;
		this.getStorage = getStorage;
		this.opStorage = opStorage;
	}
	
	public AbstractDB getDb() {
		return db;
	}

	public Storage getGetStorage() {
		return getStorage;
	}

	public Storage getOpStorage() {
		return opStorage;
	}

	public <T> T getById(Class<T> type, Object... params) {
		if(getStorage == null){
			return db.getByIdFromDB(type, null, params);
		}else{
			return getStorage.getById(type, params);
		}
	}

	public <T> PrimaryKeyValue<T> getById(Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters) {
		if(getStorage == null){
			return db.getByIdFromDB(type, null, primaryKeyParameters);
		}else{
			return getStorage.getById(type, primaryKeyParameters);
		}
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		if(getStorage == null){
			return db.getByIdListFromDB(type, null, params);
		}else{
			return getStorage.getByIdList(type, params);
		}
	}

	public void op(Collection<OperationBean> operationBeans) {
		if(opStorage == null){
			db.execute(operationBeans);
		}else{
			opStorage.op(operationBeans); 
		}
	}
}
