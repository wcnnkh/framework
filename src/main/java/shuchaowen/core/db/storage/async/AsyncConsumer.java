package shuchaowen.core.db.storage.async;

import java.util.Collection;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.OperationBean;

public interface AsyncConsumer {
	void consumer(AbstractDB db, Collection<OperationBean> operationBeans) throws Exception;
}
