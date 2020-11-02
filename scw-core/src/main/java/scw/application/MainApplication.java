package scw.application;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.CollectionUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.logger.SplitLineAppend;

public class MainApplication extends CommonApplication implements Application {
	private final Logger logger;
	private final Class<?> mainClass;
	private final MainArgs args;

	public MainApplication(Class<?> mainClass, String[] args) {
		super(DEFAULT_BEANS_PATH);
		this.mainClass = mainClass;
		this.args = new MainArgs(args);

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

		this.logger = LoggerUtils.getLogger(mainClass);
		if (args != null) {
			logger.debug("args: {}", this.args);
			addInternalSingleton(MainArgs.class, this.args);
		}
	}

	public Class<?> getMainClass() {
		return mainClass;
	}

	public MainArgs getArgs() {
		return args;
	}

	public final Logger getLogger() {
		return logger;
	}

	@Override
	protected void initInternal() throws Exception {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					MainApplication.this.destroy();
				} catch (Exception e) {
					logger.error(e, "destroy error");
				}
			}
		});
		super.initInternal();
	}

	@Override
	protected void destroyInternal() throws Exception {
		logger.info(new SplitLineAppend("destroy"));
		super.destroyInternal();
	}

	public void start() {
		Thread run = new MainApplicationThread();
		run.setContextClassLoader(mainClass.getClassLoader());
		run.setName(mainClass.getName());
		run.setDaemon(false);
		run.start();
	}

	private class MainApplicationThread extends Thread {
		@Override
		public void run() {
			init();
			while (true) {
				try {
					Thread.sleep(Long.MAX_VALUE);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

	protected static MainApplication getAutoMainApplicationImpl(Class<?> mainClass, String[] args) {
		Collection<Class<MainApplication>> impls = InstanceUtils.getConfigurationClassList(MainApplication.class,
				GlobalPropertyFactory.getInstance());
		if (!CollectionUtils.isEmpty(impls)) {
			Iterator<Class<MainApplication>> iterator = impls.iterator();
			while (iterator.hasNext()) {
				Constructor<MainApplication> constructor = ReflectionUtils.findConstructor(iterator.next(), false,
						Class.class, String[].class);
				if (constructor != null) {
					ReflectionUtils.makeAccessible(constructor);
					try {
						return constructor.newInstance(mainClass, args);
					} catch (Exception e) {
						ReflectionUtils.handleReflectionException(e);
					}
				}
			}
		}
		return new MainApplication(mainClass, args);
	}

	public static MainApplication getMainApplication(Class<?> mainClass, String[] args) {
		MainApplication application = getAutoMainApplicationImpl(mainClass, args);
		application.getLogger().info("using application: {}", application.getClass().getName());
		return application;
	}

	public final static MainApplication getMainApplication(Class<?> mainClass) {
		return getMainApplication(mainClass, null);
	}

	public static final MainApplication run(Class<?> mainClass, String[] args) {
		MainApplication application = getMainApplication(mainClass, args);
		application.start();
		return application;
	}

	public static final MainApplication run(Class<?> mainClass) {
		return run(mainClass, null);
	}
}
