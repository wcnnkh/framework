package scw.boot.support;

import scw.boot.Application;
import scw.boot.ApplicationBootstrap;
import scw.boot.Main;
import scw.context.ClassesLoader;
import scw.env.support.MainArgs;
import scw.logger.LoggerUtils;
import scw.util.concurrent.ListenableFuture;

public class MainApplication extends CommonApplication implements Application {
	private final Class<?> mainClass;
	private final MainArgs mainArgs;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MainApplication(Class<?> mainClass, String[] args) {
		this.mainClass = mainClass;
		this.mainArgs = new MainArgs(args);
		setClassLoader(mainClass.getClassLoader());
		getContextClassesLoader().add((ClassesLoader)getClassesLoader(ApplicationUtils.getBasePackage(mainClass)));
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
	public void beforeInit() throws Throwable {
		Thread shutdown = new Thread(new Runnable() {

			public void run() {
				if (MainApplication.this.isInitialized()) {
					MainApplication.this.destroy();
				}
			}
		}, mainClass.getSimpleName() + "-shutdown");
		Runtime.getRuntime().addShutdownHook(shutdown);
		super.beforeInit();
	}

	@Override
	public void destroy() {
		try {
			super.destroy();
		} catch (Throwable e) {
			getLogger().error(e, "desroy error");
		}
	}
	
	@Override
	public void afterInit() throws Throwable {
		super.afterInit();
		if (getBeanFactory().isInstance(Main.class)) {
			getBeanFactory().getInstance(Main.class).main(this, mainClass, mainArgs);
		}
	}

	public static ListenableFuture<MainApplication> run(Class<?> mainClass, String[] args) {
		MainApplication application = new MainApplication(mainClass, args);
		ApplicationBootstrap<MainApplication> runnable = new ApplicationBootstrap<MainApplication>(application);
		Thread run = new Thread(runnable);
		run.setContextClassLoader(mainClass.getClassLoader());
		run.setName(mainClass.getName());
		run.setDaemon(false);
		run.start();
		return runnable;
	}

	public static final ListenableFuture<MainApplication> run(Class<?> mainClass) {
		return run(mainClass, null);
	}
}
