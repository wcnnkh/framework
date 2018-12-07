package shuchaowen.db.storage;

import java.util.Collection;
import java.util.List;

import shuchaowen.db.AbstractDB;
import shuchaowen.db.OperationBean;
import shuchaowen.db.PrimaryKeyParameter;
import shuchaowen.db.PrimaryKeyValue;

public class CommonStorage implements Storage {
	private final AbstractDB db;

	public CommonStorage(AbstractDB db) {
		this.db = db;
	}

	public AbstractDB getDb() {
		return db;
	}

	public <T> T getById(Class<T> type, Object... params) {
		return db.getByIdFromDB(type, null, params);
	}

	public <T> PrimaryKeyValue<T> getById(Class<T> type, Collection<PrimaryKeyParameter> primaryKeyParameters) {
		return db.getByIdFromDB(type, null, primaryKeyParameters);
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		return db.getByIdListFromDB(type, null, params);
	}

	public void op(Collection<OperationBean> operationBeans) {
		db.execute(operationBeans);
	}
}
