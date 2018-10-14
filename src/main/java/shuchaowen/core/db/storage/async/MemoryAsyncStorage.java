package shuchaowen.core.db.storage.async;

import java.util.Arrays;
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
	private LinkedBlockingQueue<ExecuteInfo> queue = new LinkedBlockingQueue<ExecuteInfo>();
	private volatile boolean service = true;
	private volatile boolean logger = true;
	
	public MemoryAsyncStorage(AbstractDB db) {
		super(db);
		start();
	}
	
	protected void start() {
		new Thread(new Runnable() {
			
			public void run() {
				while(service){
					next();
				}
			}
		}).start();
	}
	
	private static void logger(SQL sql){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(sql.getSql());
		sb.append("]");
		sb.append(" - ");
		sb.append(sql.getParams() == null? "[]":Arrays.toString(sql.getParams()));
		Logger.debug("MemoryAsyncStorage", sb.toString());
	}
	
	private void next(){
		ExecuteInfo executeInfo = queue.poll();
		if(executeInfo == null){
			return ;
		}

		Collection<SQL> sqls = getSqlList(executeInfo);
		if(logger){
			for(SQL sql : sqls){
				logger(sql);
			}
		}
		
		try {
			DBUtils.execute(getDb(), sqls);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isLogger() {
		return logger;
	}

	public void setLogger(boolean logger) {
		this.logger = logger;
	}

	public void shutdown(){
		service = false;
		if(queue.size() > 0){
			while(true){
				next();
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
