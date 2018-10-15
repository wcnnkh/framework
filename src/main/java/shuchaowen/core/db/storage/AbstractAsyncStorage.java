package shuchaowen.core.db.storage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.sql.SQL;

/**
 * 缓存热点数据
 * @author shuchaowen
 *
 */
public abstract class AbstractAsyncStorage implements Storage{
	private final AbstractDB db;
	
	public AbstractAsyncStorage(AbstractDB db){
		this.db = db;
	}
	
	public AbstractDB getDb() {
		return db;
	}

	public void save(Collection<?> beans) {
		execute(new ExecuteInfo(EOperationType.SAVE, beans));
	}

	public void update(Collection<?> beans) {
		execute(new ExecuteInfo(EOperationType.UPDATE, beans));
	}

	public void delete(Collection<?> beans) {
		execute(new ExecuteInfo(EOperationType.DELETE, beans));
	}

	public void saveOrUpdate(Collection<?> beans) {
		execute(new ExecuteInfo(EOperationType.SAVE_OR_UPDATE, beans));
	}
	
	protected Collection<SQL> getSqlList(ExecuteInfo executeInfo){
		switch (executeInfo.getOperationType()) {
		case SAVE:
			return getDb().getSaveSqlList(executeInfo.getBeanList());
		case DELETE:
			return getDb().getDeleteSqlList(executeInfo.getBeanList());
		case UPDATE:
			return getDb().getUpdateSqlList(executeInfo.getBeanList());
		case SAVE_OR_UPDATE:
			return getDb().getSaveOrUpdateSqlList(executeInfo.getBeanList());
		default:
			break;
		}
		return null;
	}

	public <T> T getById(Class<T> type, Object... params) {
		return db.getByIdFromDB(type, null, params);
	}

	public <T> Map<PrimaryKeyParameter, T> getById(Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters) {
		return db.getByIdFromDB(type, null, primaryKeyParameters);
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		return db.getByIdListFromDB(type, null, params);
	}
	
	public abstract void execute(ExecuteInfo executeInfo);
}
