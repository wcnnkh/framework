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
		while(true){
			try {
				console(handlerQueue.take());
			} catch (InterruptedException e) {
				break;
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void log(LogMsg msg){
		handlerQueue.offer(msg);
	}
	
	public abstract void console(LogMsg msg) throws Exception;
}