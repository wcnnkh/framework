package shuchaowen.core.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ProcessorQueue implements Runnable{
	private BlockingQueue<Runnable> queue;
	
	public ProcessorQueue(){
		this(Integer.MAX_VALUE);
	}
	
	public ProcessorQueue(int maxProcessSize) {
		queue = new LinkedBlockingQueue<Runnable>(Math.abs(maxProcessSize)); 
	}
	
	public void run() {
		Runnable runnable;
		while(true){
			try {
				runnable = queue.take();
				if(runnable == null){
					continue;
				}
				
				runnable.run();
			} catch (InterruptedException e) {
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean submit(Runnable runnable) {
		return queue.offer(runnable);
	}
}
