package shuchaowen.core.db.storage.async;

import java.io.IOException;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.storage.ExecuteInfo;
import shuchaowen.core.util.IOUtils;
import shuchaowen.memcached.Memcached;
import shuchaowen.memcached.MemcachedQueue;

public final class MemcachedAsyncStorage extends AbstractAsyncStorage {
	private MemcachedQueue memcachedQueue;

	public MemcachedAsyncStorage(AbstractDB db, final Memcached memcached, final String queueKey, AsyncConsumer asyncConsumer) {
		super(db, asyncConsumer);
		memcachedQueue = new MemcachedQueue(queueKey, memcached);
		new Thread(new Runnable() {

			public void run() {
				try {
					while (!Thread.interrupted()) {
						byte[] data = memcachedQueue.lockReadWait(100);
						try {
							ExecuteInfo executeInfo = IOUtils.byteToJavaObject(data);
							if(executeInfo == null){
								continue;
							}
							
							getAsyncConsumer().consumer(getDb(), executeInfo);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						Thread.sleep(10L);//休眠一下，给其他服务出口竞争的机会
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void execute(ExecuteInfo executeInfo) {
		// 这里使用jdk的序列化方式 ，因为不会出现各种没有经历过的问题
		try {
			byte[] data = IOUtils.javaObjectToByte(executeInfo);
			memcachedQueue.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Memcached getMemcached() {
		return memcachedQueue.getMemcached();
	}
}
