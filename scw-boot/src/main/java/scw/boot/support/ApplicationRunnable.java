package scw.boot.support;

import scw.boot.Application;
import scw.util.concurrent.SettableListenableFuture;

public class ApplicationRunnable<T extends Application> extends SettableListenableFuture<T> implements Runnable{
	private final T application;
	
	public ApplicationRunnable(T application){
		this.application = application;
	}
	
	public void run() {
		try {
			application.init();
			set(application);
			while (true) {
				try {
					Thread.sleep(Long.MAX_VALUE);
				} catch (InterruptedException e) {
					break;
				}
			}
		} catch (Throwable e) {
			application.getLogger().error(e, "Initialization exception");
 			setException(e);
		}
	}
}
