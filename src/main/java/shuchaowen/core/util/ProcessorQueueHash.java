package shuchaowen.core.util;

import java.util.concurrent.ExecutorService;

public class ProcessorQueueHash<K>{
	private ProcessorQueue[] queueArr;
	private int count;
	
	public ProcessorQueueHash(){
		this(null, 0, 0);
	}
	
	public ProcessorQueueHash(ExecutorService threadPool, int thredSize, int queueSize) {
		this.count = Math.abs(thredSize);
		if(count > 0){
			queueArr = new ProcessorQueue[count];
			for(int i=0; i<count; i++){
				queueArr[i] = new ProcessorQueue(queueSize);
				threadPool.submit(queueArr[i]);
			}
		}
	}

	public boolean process(K index, Runnable runnable) {
		switch (count) {
		case 0:
			try {
				runnable.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		case 1:
			return queueArr[0].submit(runnable);
		default:
			return queueArr[Math.abs(index.hashCode()%count)].submit(runnable);
		}
	}
}
