package scw.boot.support;

import scw.boot.Application;
import scw.boot.Main;
import scw.context.ClassesLoader;
import scw.env.support.MainArgs;
import scw.logger.LoggerUtils;
import scw.util.concurrent.ListenableFuture;

@SuppressWarnings("unchecked")
public class MainApplication extends DefaultApplication implements Application {
	private final Class<?> mainClass;
	private final MainArgs mainArgs;

	@SuppressWarnings({"rawtypes" })
	public MainApplication(Class<?> mainClass, String[] args) {
		this.mainClass = mainClass;
		this.mainArgs = new MainArgs(args);
		setClassLoader(mainClass.getClassLoader());
		getContextClassesLoader().add(
				(ClassesLoader) getClassesLoader(ApplicationUtils
						.getBasePackage(mainClass)));
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
		return ApplicationUtils.run(new MainApplication(mainClass, args),
				mainClass.getSimpleName(), mainClass.getClassLoader());
	}

	public static final ListenableFuture<MainApplication> run(Class<?> mainClass) {
		return run(mainClass, null);
	}
}
