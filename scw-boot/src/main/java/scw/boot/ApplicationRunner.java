package scw.boot;

import scw.util.concurrent.ListenableFuture;
import scw.util.concurrent.SettableListenableFuture;

public class ApplicationRunner<T extends ConfigurableApplication> {
	private final T application;
	private final String threadName;

	public ApplicationRunner(T application, String threadName) {
		this.application = application;
		this.threadName = threadName;
	}
	
	public ListenableFuture<T> run() {
		ApplicationRunnable runnable = new ApplicationRunnable();
		Thread run = new Thread(runnable);
		run.setContextClassLoader(application.getClassLoader());
		run.setName(threadName);
		run.setDaemon(false);
		run.start();
		return runnable;
	}

	private class ApplicationRunnable extends SettableListenableFuture<T> implements Runnable {

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
}
