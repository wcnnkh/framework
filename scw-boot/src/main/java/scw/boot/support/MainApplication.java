package scw.boot.support;

import scw.boot.Application;
import scw.boot.Main;
import scw.env.MainArgs;
import scw.logger.LoggerFactory;
import scw.util.concurrent.ListenableFuture;

public class MainApplication extends DefaultApplication implements Application {
	private final Class<?> mainClass;
	private final MainArgs mainArgs;

	public MainApplication(Class<?> mainClass, String[] args) {
		this.mainClass = mainClass;
		this.mainArgs = new MainArgs(args);
		setClassLoader(mainClass.getClassLoader());
		getEnvironment().source(mainClass);
		getEnvironment().addFactory(mainArgs);
		setLogger(LoggerFactory.getLogger(mainClass));
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
	
	public static ApplicationRunner<MainApplication> main(Class<?> mainClass, String[] args){
		return new ApplicationRunner<MainApplication>(new MainApplication(mainClass, args), mainClass.getSimpleName());
	}

	public static ListenableFuture<MainApplication> run(Class<?> mainClass,
			String[] args) {
		return main(mainClass, args).start();
	}

	public static final ListenableFuture<MainApplication> run(Class<?> mainClass) {
		return run(mainClass, null);
	}
	
	@Override
	public String toString() {
		return mainClass.toString();
	}
}
