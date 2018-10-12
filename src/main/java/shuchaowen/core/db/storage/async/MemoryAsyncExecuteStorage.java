package shuchaowen.core.db.storage.async;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.sql.format.SQLFormat;
import shuchaowen.core.db.storage.AbstractExecuteStorage;
import shuchaowen.core.db.storage.ExecuteInfo;

/**
 * 此方式只能在单机环境一使用，因为在集群下无法保证执行顺序
 * @author shuchaowen
 *
 */
public class MemoryAsyncExecuteStorage extends AbstractExecuteStorage {
	private LinkedBlockingQueue<ExecuteInfo> queue = new LinkedBlockingQueue<ExecuteInfo>();

	public MemoryAsyncExecuteStorage(AbstractDB db) {
		super(db, DEFAULT_SQL_FORMAT);
	}
	
	public MemoryAsyncExecuteStorage(AbstractDB db, SQLFormat sqlFormat) {
		super(db, sqlFormat);
	}

	protected void start() {
		new Thread(new Runnable() {

			public void run() {
				while (true) {
					try {
						ExecuteInfo executeInfo = queue.poll();
						Collection<SQL> sqls = getSqlList(executeInfo);
						getDb().execute(sqls);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	@Override
	public void execute(ExecuteInfo executeInfo) {
		queue.offer(executeInfo);
	}
}
