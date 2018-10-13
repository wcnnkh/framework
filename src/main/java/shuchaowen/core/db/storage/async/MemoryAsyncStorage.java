package shuchaowen.core.db.storage.async;

import java.util.concurrent.LinkedBlockingQueue;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.storage.AbstractAsyncStorage;
import shuchaowen.core.db.storage.ExecuteInfo;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

/**
 * 此方式只能在单机环境一使用，因为在集群下无法保证执行顺序
 * 此方案只能用于单服务器架构，集群架构请不要使用此构造方法
 * @author shuchaowen
 *
 */
public class MemoryAsyncStorage extends AbstractAsyncStorage {
	private LinkedBlockingQueue<ExecuteInfo> queue = new LinkedBlockingQueue<ExecuteInfo>();
	private volatile boolean service = true;
	
	public MemoryAsyncStorage(AbstractDB db) {
		super(db);
	}
	
	protected void start() {
		new Thread(new Runnable() {
			
			public void run() {
				while(service){
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
