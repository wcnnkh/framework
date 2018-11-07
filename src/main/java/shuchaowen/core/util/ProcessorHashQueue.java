package shuchaowen.core.util;

public class ProcessorHashQueue<K>{
	private final ProcessorQueue[] queueArr;
	
	public ProcessorHashQueue(int threadSize, int maxProcessSize) {
		queueArr = new ProcessorQueue[threadSize];
		for(int i=0; i<threadSize; i++){
			queueArr[i] = new ProcessorQueue(maxProcessSize, this.getClass().getName() + "#" + i);
		}
	}

	public void process(K index, Runnable runnable) throws InterruptedException {
		switch (queueArr.length) {
		case 0:
			try {
				runnable.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		case 1:
			queueArr[0].submit(runnable);
		default:
			queueArr[Math.abs(index.hashCode()%queueArr.length)].submit(runnable);
		}
	}

	public void start() {
		for(ProcessorQueue processorQueue : queueArr){
			processorQueue.start();
		}
	}

	public void destroy() {
		for(ProcessorQueue processorQueue : queueArr){
			processorQueue.destroy();
		}
	}
}
