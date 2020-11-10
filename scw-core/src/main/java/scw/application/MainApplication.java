package scw.application;

import java.util.Map.Entry;

import scw.core.GlobalPropertyFactory;
import scw.logger.LoggerUtils;
import scw.util.concurrent.ListenableFuture;

public class MainApplication extends CommonApplication implements Application {
	private final Class<?> mainClass;
	private final MainArgs args;

	public MainApplication(Class<?> mainClass, String[] args) {
		this.mainClass = mainClass;
		this.args = new MainArgs(args);
		setClassLoader(mainClass.getClassLoader());
		BasePackage basePackage = mainClass.getAnnotation(BasePackage.class);
		if (basePackage == null) {
			Package p = mainClass.getPackage();
			if (p != null) {
				GlobalPropertyFactory.getInstance().setBasePackageName(p.getName());
			}
		} else {
			GlobalPropertyFactory.getInstance().setBasePackageName(basePackage.value());
		}

		for (Entry<String, String> entry : this.args.getParameterMap().entrySet()) {
			getPropertyFactory().put(entry.getKey(), entry.getValue());
		}

		setLogger(LoggerUtils.getLogger(mainClass));
		if (args != null) {
			getLogger().debug("args: {}", this.args);
		}
	}

	public Class<?> getMainClass() {
		return mainClass;
	}

	public MainArgs getArgs() {
		return args;
	}

	public static final ListenableFuture<Application> run(MainApplication application) {
		ApplicationBootstrap bootstrap = application.getBeanFactory().getInstance(ApplicationBootstrap.class);
		application.getLogger().info("using bootstrap: {}", bootstrap.getClass().getName());
		bootstrap.setMainArgs(application.getArgs());
		Thread run = new Thread(bootstrap);
		run.setContextClassLoader(application.getClassLoader());
		run.setName(application.getMainClass().getName());
		run.setDaemon(false);
		run.start();
		return bootstrap;
	}

	public static final ListenableFuture<Application> run(Class<?> mainClass, String[] args) {
		MainApplication application = new MainApplication(mainClass, args);
		application.init();
		return run(application);
	}

	public static final ListenableFuture<Application> run(Class<?> mainClass) {
		return run(mainClass, null);
	}
}
