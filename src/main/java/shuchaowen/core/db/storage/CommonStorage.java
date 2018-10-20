package shuchaowen.core.db.storage;

import java.util.Collection;
import java.util.List;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.PrimaryKeyValue;

public class CommonStorage implements Storage{
	private final AbstractDB db;
	private final Storage getStorage;
	private final Storage executeStorage;
	
	public CommonStorage(AbstractDB db, Storage getStorage, Storage executeStorage){
		this.db = db;
		this.getStorage = getStorage;
		this.executeStorage = executeStorage;
	}
	
	public AbstractDB getDb() {
		return db;
	}

	public Storage getGetStorage() {
		return getStorage;
	}

	public Storage getExecuteStorage() {
		return executeStorage;
	}

	public void save(Collection<?> beans) {
		if(executeStorage == null){
			db.saveToDB(beans);
		}else{
			executeStorage.save(beans);
		}
	}

	public void update(Collection<?> beans) {
		if(executeStorage == null){
			db.updateToDB(beans);
		}else{
			executeStorage.update(beans);
		}
	}

	public void delete(Collection<?> beans) {
		if(executeStorage == null){
			db.deleteToDB(beans);
		}else{
			executeStorage.delete(beans);
		}
	}

	public void saveOrUpdate(Collection<?> beans) {
		if(executeStorage == null){
			db.saveOrUpdateToDB(beans);
		}else{
			executeStorage.saveOrUpdate(beans);
		}
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
}
