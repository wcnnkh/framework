package shuchaowen.core.logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class LoggerProcess implements AbstractLogger,Runnable{
	private BlockingQueue<LogMsg> handlerQueue = new LinkedBlockingQueue<LogMsg>();
	private Thread thread;
	
	public LoggerProcess() {
		System.out.println("Init shuchaowen-logger [" + this.getClass().getName() + "]");
		thread = new Thread(this, "shuchaowen-logger");
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
	
	public void start(){
		thread.start();
	}
	
	public void shutdown(){
		thread.interrupt();
	}
	
	public void log(LogMsg msg){
		handlerQueue.offer(msg);
	}
	
	public abstract void console(LogMsg msg) throws Exception;
}