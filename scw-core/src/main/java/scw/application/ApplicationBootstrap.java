package scw.application;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.concurrent.SettableListenableFuture;

public class ApplicationBootstrap<T extends Application> extends SettableListenableFuture<T> implements Runnable {
	private static Logger logger = LoggerUtils.getLogger(ApplicationBootstrap.class);
	private final T application;

	public ApplicationBootstrap(T application) {
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
			logger.error(e);
			setException(e);
		}
	}
}
