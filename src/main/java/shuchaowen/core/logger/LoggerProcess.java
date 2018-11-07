package shuchaowen.core.logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class LoggerProcess implements AbstractLogger,Runnable{
	private BlockingQueue<LogMsg> handlerQueue = new LinkedBlockingQueue<LogMsg>();
	
	public LoggerProcess() {
		System.out.println("Init shuchaowen-logger [" + this.getClass().getName() + "]");
		Thread thread = new Thread(this, "shuchaowen-logger");
		thread.setDaemon(true);
		thread.start();
	}
	
	public void run(){
		try {
			while(!Thread.interrupted()){
				LogMsg msg = handlerQueue.take();
				try {
					console(msg);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
		}
	}
	
	public void log(LogMsg msg){
		handlerQueue.offer(msg);
	}
	
	public abstract void console(LogMsg msg) throws Exception;
}