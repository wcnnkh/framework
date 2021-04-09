package scw.boot.support;

import scw.boot.Application;
import scw.boot.Main;
import scw.env.support.MainArgs;
import scw.logger.LoggerUtils;
import scw.util.concurrent.ListenableFuture;

public class MainApplication extends DefaultApplication implements Application {
	private final Class<?> mainClass;
	private final MainArgs mainArgs;

	public MainApplication(Class<?> mainClass, String[] args) {
		this.mainClass = mainClass;
		this.mainArgs = new MainArgs(args);
		setClassLoader(mainClass.getClassLoader());
		getEnvironment().source(mainClass);
		getEnvironment().addPropertyFactory(mainArgs);
		setLogger(LoggerUtils.getLogger(mainClass));
		if (args != null) {
			getLogger().debug("args: {}", this.mainArgs);
		}
	}

	public Class<?> getMainClass() {
		return mainClass;
	}

	public MainArgs getMainArgs() {
		return mainArgs;
	}

	@Override
	public void afterInit() throws Throwable {
		super.afterInit();
		if (getBeanFactory().isInstance(Main.class)) {
			getBeanFactory().getInstance(Main.class).main(this, mainClass,
					mainArgs);
		}
	}

	public static ListenableFuture<MainApplication> run(Class<?> mainClass,
			String[] args) {
		MainApplication application = new MainApplication(mainClass, args);
		application.addShutdownHook();
		return ApplicationUtils.run(application, mainClass.getSimpleName());
	}

	public static final ListenableFuture<MainApplication> run(Class<?> mainClass) {
		return run(mainClass, null);
	}
	
	@Override
	public String toString() {
		return mainClass.toString();
	}
}
