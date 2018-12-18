package scw.db.storage;

import java.util.Collection;
import java.util.List;

import scw.db.OperationBean;

public interface Storage {
	<T> T getById(Class<T> type, Object... params);

	<T> List<T> getByIdList(Class<T> type, Object... params);
	
	void op(Collection<OperationBean> operationBeans);
}
