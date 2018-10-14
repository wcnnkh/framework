package shuchaowen.core.db.storage.async;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.DBUtils;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.storage.AbstractAsyncStorage;
import shuchaowen.core.db.storage.ExecuteInfo;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.Logger;

/**
 * 此方式只能在单机环境一使用，因为在集群下无法保证执行顺序
 * 此方案只能用于单服务器架构，集群架构请不要使用此构造方法
 * @author shuchaowen
 *
 */
public class MemoryAsyncStorage extends AbstractAsyncStorage {
	private final static long LOGGER_QUEUE_SIZE_TIMEOUT = 10000L;
	private LinkedBlockingQueue<ExecuteInfo> queue = new LinkedBlockingQueue<ExecuteInfo>();
	private volatile boolean service = true;
	private volatile boolean logger = false;
	private volatile boolean loggerQueueSize = true;
	
	public MemoryAsyncStorage(AbstractDB db) {
		super(db);
		start();
	}
	
	private void loggerSize(){
		Logger.debug("MemoryAsyncStorage", "当前队列剩余数量：" + queue.size());
	}
	
	protected void start() {
		new Thread(new Runnable() {
			
			public void run() {
				while(service){
					ExecuteInfo executeInfo = queue.poll();
					if(executeInfo == null){
						continue;
					}

					Collection<SQL> sqls = getSqlList(executeInfo);
					if(logger){
						for(SQL sql : sqls){
							Logger.debug("MemoryAsyncStorage", sql.getSql());
						}
					}
					
					try {
						DBUtils.execute(getDb(), sqls);
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
						Thread.sleep(LOGGER_QUEUE_SIZE_TIMEOUT);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
					
					if(loggerQueueSize){
						loggerSize();
					}
				}
			}
		}).start();
	}
	
	public boolean isLogger() {
		return logger;
	}

	public void setLogger(boolean logger) {
		this.logger = logger;
	}

	public boolean isLoggerQueueSize() {
		return loggerQueueSize;
	}

	public void setLoggerQueueSize(boolean loggerQueueSize) {
		this.loggerQueueSize = loggerQueueSize;
	}

	public void shutdown(){
		service = false;
		if(queue.size() > 0){
			while(true){
				ExecuteInfo executeInfo = queue.poll();
				try {
					getDb().execute(getSqlList(executeInfo));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void execute(ExecuteInfo executeInfo) {
		if(!service){
			throw new ShuChaoWenRuntimeException("service is " + service);//停止服务了
		}
		queue.offer(executeInfo);
	}
}
