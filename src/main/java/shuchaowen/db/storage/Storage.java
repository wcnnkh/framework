package shuchaowen.db.storage;

import java.util.Collection;
import java.util.List;

import shuchaowen.db.OperationBean;

public interface Storage {
	<T> T getById(Class<T> type, Object... params);

	<T> List<T> getByIdList(Class<T> type, Object... params);
	
	void op(Collection<OperationBean> operationBeans);
}
