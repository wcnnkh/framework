package shuchaowen.core.db.storage.async;

import java.io.IOException;
import java.util.Collection;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.OperationBean;
import shuchaowen.core.util.IOUtils;
import shuchaowen.core.util.Logger;
import shuchaowen.core.util.XTime;
import shuchaowen.memcached.Memcached;
import shuchaowen.memcached.MemcachedLock;
import shuchaowen.memcached.MemcachedQueue;

public final class MemcachedAsyncStorage extends AbstractAsyncStorage {
	private static final String CONSUMER_LOCK_KEY = "_consumer_lock";
	private MemcachedQueue memcachedQueue;
	private boolean error = false;

	public MemcachedAsyncStorage(AbstractDB db, final Memcached memcached, final String queueKey,
			AsyncConsumer asyncConsumer) {
		super(db, asyncConsumer);
		memcachedQueue = new MemcachedQueue(queueKey, memcached);
		new Thread(new Runnable() {

			public void run() {
				try {
					while (!Thread.interrupted()) {
						if(error){
							Logger.error(queueKey, "memcached async error");
							Thread.sleep(5 * XTime.ONE_MINUTE);//休息5分钟再次警告
							continue;
						}
						
						MemcachedLock memcachedLock = new MemcachedLock(memcached, queueKey + CONSUMER_LOCK_KEY);
						memcachedLock.lockWait(10);
						try {
							byte[] data = memcachedQueue.lockRead();
							if (data == null) {
								continue;
							}

							Collection<OperationBean> operationBeans = IOUtils.byteToJavaObject(data);
							if (operationBeans == null) {
								continue;
							}

							try {
								getAsyncConsumer().consumer(getDb(), operationBeans);
							} catch (Exception e) {
								//发生异常了，此队列应该关闭
								error = true;
								e.printStackTrace();
							}
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							memcachedLock.unLock();
						}
						Thread.sleep(10L);// 休眠一下，给其他服务出口竞争的机会
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public Memcached getMemcached() {
		return memcachedQueue.getMemcached();
	}

	public void op(Collection<OperationBean> operationBean) {
		// 这里使用jdk的序列化方式 ，因为不会出现各种没有经历过的问题
		try {
			byte[] data = IOUtils.javaObjectToByte(operationBean);
			memcachedQueue.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
