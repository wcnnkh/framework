package shuchaowen.core.db.storage.async;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.storage.ExecuteInfo;

public interface AsyncConsumer {
	void consumer(AbstractDB db, ExecuteInfo executeInfo);
}
