package scw.sql.async;

import scw.sql.SqlOperations;
import scw.sql.Sqls;
import scw.sql.orm.SqlFormat;
import scw.utils.queue.MemoryQueue;

public final class MemoryAsyncService extends QueueAsyncService {

	public MemoryAsyncService(SqlOperations sqlOperations, SqlFormat sqlFormat) {
		super(new MemoryQueue<Sqls>(), sqlOperations, sqlFormat);
	}

}
