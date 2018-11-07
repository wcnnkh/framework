package shuchaowen.core.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProcessorQueue{
	private final BlockingQueue<Runnable> queue;
	private final Thread thread;
	
	public ProcessorQueue(int maxProcessSize, String threadName) {
		queue = new LinkedBlockingQueue<Runnable>(Math.abs(maxProcessSize));
		this.thread = new Thread(new Runnable() {
			
			public void run() {
				Runnable runnable;
				try {
					while(!Thread.interrupted()){
						runnable = queue.take();
						if(runnable == null){
							continue;
						}
						
						try {
							runnable.run();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (InterruptedException e) {
				}
			}
		}, threadName);
	}
	
	public void submit(Runnable runnable) throws InterruptedException {
		queue.put(runnable);
	}
	
	public void start(){
		thread.start();
	}
	
	public void destroy(){
		thread.interrupt();
	}
}
