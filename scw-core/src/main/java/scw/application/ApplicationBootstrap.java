package scw.application;

import scw.aop.annotation.AopEnable;
import scw.util.concurrent.SettableListenableFuture;

@AopEnable(false)
public class ApplicationBootstrap extends SettableListenableFuture<Application> implements Runnable, ApplicationAware {
	private Application application;
	private MainArgs mainArgs;
	private Class<?> mainClass;

	public void setApplication(Application application) {
		this.application = application;
	}

	public Application getApplication() {
		return application;
	}

	public MainArgs getMainArgs() {
		return mainArgs;
	}

	public void setMainArgs(MainArgs mainArgs) {
		this.mainArgs = mainArgs;
	}

	public Class<?> getMainClass() {
		return mainClass;
	}

	public void setMainClass(Class<?> mainClass) {
		this.mainClass = mainClass;
	}

	public void run() {
		try {
			if(!application.isInitialized()){
				application.init();
			}
			start();
			set(application);
			while (true) {
				try {
					Thread.sleep(Long.MAX_VALUE);
				} catch (InterruptedException e) {
					break;
				}
			}
		} catch (Throwable e) {
			application.getLogger().error(e, "Running error");
			setException(e);
		}
	}

	public void start() throws Throwable {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					ApplicationBootstrap.this.shutdown();
				} catch (Exception e) {
					application.getLogger().error(e, "destroy error");
				}
			}
		});
	}

	/**
	 * @see ApplicationBootstrap#start()
	 */
	protected void shutdown() {
	}
}
