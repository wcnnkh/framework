package scw.application;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.CollectionUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class MainApplication extends CommonApplication implements Application, Runnable {
	private final Logger logger;
	private final Class<?> mainClass;
	private final MainArgs args;

	public MainApplication(Class<?> mainClass, MainArgs args) {
		super(DEFAULT_BEANS_PATH);
		this.mainClass = mainClass;
		this.args = args;
		
		configuration(mainClass, args);
		for(Entry<String, String> entry : args.getParameterMap().entrySet()){
			getPropertyFactory().put(entry.getKey(), entry.getValue());
		}
		
		this.logger = LoggerUtils.getLogger(mainClass);
		if (args != null) {
			logger.debug("args: {}", args);
			addInternalSingleton(MainArgs.class, args);
		}
	}

	public Class<?> getMainClass() {
		return mainClass;
	}

	public MainArgs getArgs() {
		return args;
	}

	public void run() {
		init();
	}

	public final Logger getLogger() {
		return logger;
	}

	public static void configuration(Class<?> mainClass, MainArgs args) {
		Thread.currentThread().setContextClassLoader(mainClass.getClassLoader());
		BasePackage basePackage = mainClass.getAnnotation(BasePackage.class);
		if (basePackage == null) {
			GlobalPropertyFactory.getInstance().setBasePackageName(mainClass.getPackage().getName());
		} else {
			GlobalPropertyFactory.getInstance().setBasePackageName(basePackage.value());
		}
	}

	public static void run(MainApplication application) {
		Thread run = new Thread(application);
		run.setContextClassLoader(application.getMainClass().getClassLoader());
		run.setName(application.getMainClass().getName());
		run.setDaemon(false);
		run.start();
	}

	public static MainApplication getAutoMainApplicationImpl(Class<?> mainClass, MainArgs args)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Collection<Class<MainApplication>> impls = InstanceUtils.getConfigurationClassList(MainApplication.class,
				GlobalPropertyFactory.getInstance());
		if (!CollectionUtils.isEmpty(impls)) {
			Iterator<Class<MainApplication>> iterator = impls.iterator();
			while (iterator.hasNext()) {
				Constructor<MainApplication> constructor = ReflectionUtils.findConstructor(iterator.next(), false,
						Class.class, MainArgs.class);
				if (constructor != null) {
					ReflectionUtils.makeAccessible(constructor);
					return constructor.newInstance(mainClass, args);
				}
			}
		}
		return null;
	}

	public static void run(Class<?> mainClass, String[] args) {
		MainArgs mainArgs = new MainArgs(args);
		configuration(mainClass, mainArgs);
		MainApplication application;
		try {
			application = getAutoMainApplicationImpl(mainClass, mainArgs);
		} catch (Exception e) {
			throw new ApplicationException("获取MainApplication实现异常", e);
		}

		if (application == null) {
			application = new MainApplication(mainClass, mainArgs);
		}

		application.getLogger().info("use application: {}", application.getClass().getName());
		run(application);
	}

	public static void run(Class<?> mainClass) {
		run(mainClass, null);
	}
}
