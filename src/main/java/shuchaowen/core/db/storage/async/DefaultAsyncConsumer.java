package shuchaowen.core.db.storage.async;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.TransactionContext;
import shuchaowen.core.db.storage.ExecuteInfo;

public class DefaultAsyncConsumer implements AsyncConsumer{

	public void consumer(AbstractDB db, ExecuteInfo executeInfo) {
		TransactionContext.getInstance().execute(db, executeInfo.getSqlList(db));
	}
}
