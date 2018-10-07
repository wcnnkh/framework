package shuchaowen.core.db.storage.memory;

import java.util.concurrent.LinkedBlockingQueue;

import shuchaowen.core.db.storage.AbstractAsyncStorage;
import shuchaowen.core.db.storage.ExecuteInfo;

/**
 * 不推荐在集群环境下使用，因为在多队列下无法保证执行顺序
 * @author asus1
 *
 */
public class MemoryAsyncStorage extends AbstractAsyncStorage{
	private LinkedBlockingQueue<ExecuteInfo> executeQueue = new LinkedBlockingQueue<ExecuteInfo>();
	
	public MemoryAsyncStorage(){
		new Thread(new Runnable() {
			
			public void run() {
				while(!Thread.currentThread().isInterrupted()){
					consumer(executeQueue.poll());
				}
			}
		}, this.getClass().getName()).start();
	}

	public void producer(ExecuteInfo executeInfo) {
		executeQueue.offer(executeInfo);
	}
}
