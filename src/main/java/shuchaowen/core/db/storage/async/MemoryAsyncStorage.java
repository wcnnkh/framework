package shuchaowen.core.db.storage.async;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.OperationBean;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

/**
 * 此方式只能在单机环境一使用，因为在集群下无法保证执行顺序 此方案只能用于单服务器架构，集群架构请不要使用此构造方法
 * 
 * @author shuchaowen
 *
 */
public final class MemoryAsyncStorage extends AbstractAsyncStorage {
	private LinkedBlockingQueue<Collection<OperationBean>> queue = new LinkedBlockingQueue<Collection<OperationBean>>();
	private boolean service = true;
	private volatile boolean logger = true;
	private Thread thread;

	public MemoryAsyncStorage(AbstractDB db, AsyncConsumer asyncConsumer) {
		super(db, asyncConsumer);
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
		Collection<OperationBean> operationBeans = queue.poll();
		if (operationBeans == null) {
			return;
		}
		
		try {
			getAsyncConsumer().consumer(getDb(), operationBeans);
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

	public void op(Collection<OperationBean> operationBean) {
		if (!service) {
			throw new ShuChaoWenRuntimeException("service is " + service);// 停止服务了
		}
		queue.offer(operationBean);
	}
}
