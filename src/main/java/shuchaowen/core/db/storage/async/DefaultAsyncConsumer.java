package shuchaowen.core.db.storage.async;

import java.util.Collection;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.OperationBean;

public class DefaultAsyncConsumer implements AsyncConsumer{

	public void consumer(AbstractDB db, Collection<OperationBean> operationBeans) {
		db.opToDB(operationBeans);
	}
}
