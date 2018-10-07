package shuchaowen.core.db.storage;

import java.util.concurrent.LinkedBlockingQueue;

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

	public void consumer(ExecuteInfo executeInfo) {
		execute(executeInfo.getAbstractDB(), getSqlList(executeInfo));
	}
}
