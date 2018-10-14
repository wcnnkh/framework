package shuchaowen.core.db.storage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.PrimaryKeyParameter;

public class CommonStorage implements Storage{
	private final AbstractDB db;
	private final Storage get;
	private final Storage execute;
	
	public CommonStorage(AbstractDB db, Storage get, Storage execute){
		this.db = db;
		this.get = get;
		this.execute = execute;
	}
	
	public AbstractDB getDb() {
		return db;
	}

	public Storage getGet() {
		return get;
	}

	public Storage getExecute() {
		return execute;
	}

	public void save(Collection<?> beans) {
		if(execute == null){
			db.saveToDB(beans);
		}else{
			execute.save(beans);
		}
	}

	public void update(Collection<?> beans) {
		if(execute == null){
			db.updateToDB(beans);
		}else{
			execute.update(beans);
		}
	}

	public void delete(Collection<?> beans) {
		if(execute == null){
			db.deleteToDB(beans);
		}else{
			execute.delete(beans);
		}
	}

	public void saveOrUpdate(Collection<?> beans) {
		if(execute == null){
			db.saveOrUpdateToDB(beans);
		}else{
			execute.saveOrUpdate(beans);
		}
	}

	public <T> T getById(Class<T> type, Object... params) {
		if(get == null){
			return db.getByIdFromDB(type, null, params);
		}else{
			return get.getById(type, params);
		}
	}

	public <T> Map<PrimaryKeyParameter, T> getById(Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters) {
		if(get == null){
			return db.getByIdFromDB(type, null, primaryKeyParameters);
		}else{
			return get.getById(type, primaryKeyParameters);
		}
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		if(get == null){
			return db.getByIdListFromDB(type, null, params);
		}else{
			return get.getByIdList(type, params);
		}
	}
}
