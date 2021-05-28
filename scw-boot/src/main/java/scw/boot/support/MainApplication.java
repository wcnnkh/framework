package scw.boot.support;

import scw.boot.Application;
import scw.boot.ApplicationPostProcessor;
import scw.boot.ConfigurableApplication;
import scw.boot.Main;
import scw.env.MainArgs;
import scw.logger.LoggerFactory;
import scw.util.concurrent.ListenableFuture;

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
			getInstance(Main.class).main(this, mainClass,
					mainArgs);
		}
	}

	public static ApplicationRunner<MainApplication> main(Class<?> mainClass, String[] args){
		return new ApplicationRunner<MainApplication>(new MainApplication(mainClass, args), mainClass.getSimpleName());
	}

	public static ListenableFuture<MainApplication> run(Class<?> mainClass,
			String[] args) {
		ApplicationRunner<MainApplication> runner = new ApplicationRunner<MainApplication>(new MainApplication(mainClass, args), mainClass.getSimpleName());
		return runner.start();
	}

	public static final ListenableFuture<MainApplication> run(Class<?> mainClass) {
		return run(mainClass, null);
	}
	
	@Override
	public String toString() {
		return mainClass.toString();
	}
}
