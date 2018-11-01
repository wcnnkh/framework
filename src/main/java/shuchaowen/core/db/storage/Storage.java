package shuchaowen.core.db.storage;

import java.util.Collection;
import java.util.List;

import shuchaowen.core.db.OperationBean;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.PrimaryKeyValue;

public interface Storage {
	<T> T getById(Class<T> type, Object... params);

	<T> PrimaryKeyValue<T> getById(Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters);

	<T> List<T> getByIdList(Class<T> type, Object... params);
	
	void op(Collection<OperationBean> operationBeans);
}
