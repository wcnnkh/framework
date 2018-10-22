package shuchaowen.core.db.storage.async;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.DBUtils;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.storage.ExecuteInfo;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

/**
 * 此方式只能在单机环境一使用，因为在集群下无法保证执行顺序 此方案只能用于单服务器架构，集群架构请不要使用此构造方法
 * 
 * @author shuchaowen
 *
 */
public final class MemoryAsyncStorage extends AbstractAsyncStorage {
	private LinkedBlockingQueue<ExecuteInfo> queue = new LinkedBlockingQueue<ExecuteInfo>();
	private boolean service = true;
	private volatile boolean logger = true;
	private Thread thread;

	public MemoryAsyncStorage(AbstractDB db) {
		super(db);
		this.thread = new Thread(new Runnable() {

			public void run() {
				while (!Thread.interrupted()) {
					try {
						next();
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
			}
		});
		this.thread.start();
	}

	private void next() {
		ExecuteInfo executeInfo = queue.poll();
		if (executeInfo == null) {
			return;
		}

		Collection<SQL> sqls = getSqlList(executeInfo);
		if (logger) {
			for (SQL sql : sqls) {
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

	@Override
	public void execute(ExecuteInfo executeInfo) {
		if (!service) {
			throw new ShuChaoWenRuntimeException("service is " + service);// 停止服务了
		}
		queue.offer(executeInfo);
	}
}
