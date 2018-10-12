package shuchaowen.core.db.storage.async;

import java.util.concurrent.LinkedBlockingQueue;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.sql.format.SQLFormat;
import shuchaowen.core.db.storage.AbstractExecuteStorage;
import shuchaowen.core.db.storage.ExecuteInfo;
import shuchaowen.core.util.Logger;
import shuchaowen.core.util.XTime;

/**
 * 此方式只能在单机环境一使用，因为在集群下无法保证执行顺序
 * 
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
				while(true){
					ExecuteInfo executeInfo = queue.poll();
					if(executeInfo == null){
						continue;
					}
					
					try {
						getDb().execute(getSqlList(executeInfo));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		new Thread(new Runnable() {
			
			public void run() {
				while(true){
					try {
						Thread.sleep(10 * XTime.ONE_SECOND);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if(queue.size() == 0){
						continue;
					}
					
					Logger.info("MemoryAsyncExecuteStorage", "剩余等待异步存储数量：" + queue.size());
				} 
			}
		}).start();
	}

	@Override
	public void execute(ExecuteInfo executeInfo) {
		queue.offer(executeInfo);
	}
}
