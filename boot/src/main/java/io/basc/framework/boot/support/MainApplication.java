package io.basc.framework.boot.support;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.boot.Main;
import io.basc.framework.env.MainArgs;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.concurrent.ListenableFuture;

public class MainApplication extends DefaultApplication implements Application, ApplicationPostProcessor {
	private final Class<?> mainClass;
	private final MainArgs mainArgs;

	public MainApplication(Class<?> mainClass, String[] args) {
		this.mainClass = mainClass;
		this.mainArgs = new MainArgs(args);
		setClassLoader(mainClass.getClassLoader());
		source(mainClass);
		getEnvironment().addFactory(mainArgs);
		setLogger(LoggerFactory.getLogger(mainClass));
		if (args != null) {
			getLogger().debug("args: {}", this.mainArgs);
		}
		addPostProcessor(this);
	}

	public Class<?> getMainClass() {
		return mainClass;
	}

	public MainArgs getMainArgs() {
		return mainArgs;
	}

	@Override
	public void postProcessApplication(ConfigurableApplication application) throws Throwable {
		if (isInstance(Main.class)) {
			getInstance(Main.class).main(this, mainClass, mainArgs);
		}
	}

	public static ApplicationRunner<MainApplication> main(Class<?> mainClass, String[] args) {
		return new ApplicationRunner<MainApplication>(new MainApplication(mainClass, args), mainClass.getSimpleName());
	}

	public static ListenableFuture<MainApplication> run(Class<?> mainClass, String[] args) {
		return main(mainClass, args).start();
	}

	public static ListenableFuture<MainApplication> run(Class<?> mainClass, String[] args, Class<?>... sourceClasses) {
		return main(mainClass, args).config((a) -> {
			for (Class<?> source : sourceClasses) {
				a.source(source);
			}
		}).start();
	}

	public static ListenableFuture<MainApplication> run(Class<?> mainClass) {
		return main(mainClass, null).start();
	}

	public static ListenableFuture<MainApplication> run(Class<?> mainClass, Class<?>... sourceClasses) {
		return run(mainClass, null, sourceClasses);
	}

	@Override
	public String toString() {
		return mainClass.toString();
	}
}
