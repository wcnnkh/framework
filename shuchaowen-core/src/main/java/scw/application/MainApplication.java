package scw.application;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;

import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.CollectionUtils;
import scw.util.FormatUtils;

public class MainApplication extends CommonApplication implements Application {
	private final Class<?> mainClass;
	private final String[] args;

	public MainApplication(Class<?> mainClass, String[] args) {
		super(DEFAULT_BEANS_PATH);
		this.mainClass = mainClass;
		this.args = args;
		configuration(mainClass, args);
	}

	public Class<?> getMainClass() {
		return mainClass;
	}

	public String[] getArgs() {
		return args;
	}

	private static class Run extends Thread {
		private MainApplication mainApplication;

		public Run(MainApplication mainApplication) {
			this.mainApplication = mainApplication;
		}

		public void run() {
			mainApplication.init();
		}
	}

	public static void configuration(Class<?> mainClass, String[] args) {
		Thread.currentThread()
				.setContextClassLoader(mainClass.getClassLoader());
		GlobalPropertyFactory.getInstance().setBasePackageName(
				mainClass.getPackage().getName());
	}

	public static void run(MainApplication application) {
		Run run = new Run(application);
		run.setContextClassLoader(application.getMainClass().getClassLoader());
		run.setName(application.getMainClass().getName());
		run.setDaemon(false);
		run.start();
	}

	public static MainApplication getAutoMainApplicationImpl(
			Class<?> mainClass, String[] args) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Collection<Class<MainApplication>> impls = InstanceUtils
				.getConfigurationClassList(MainApplication.class,
						GlobalPropertyFactory.getInstance());
		if (!CollectionUtils.isEmpty(impls)) {
			Iterator<Class<MainApplication>> iterator = impls.iterator();
			while (iterator.hasNext()) {
				Constructor<MainApplication> constructor = ReflectionUtils
						.findConstructor(iterator.next(), false, Class.class,
								String[].class);
				if (constructor != null) {
					ReflectionUtils.setAccessibleConstructor(constructor);
					return constructor.newInstance(mainClass, args);
				}

				constructor = ReflectionUtils.findConstructor(iterator.next(),
						false, Class.class);
				if (constructor != null) {
					ReflectionUtils.setAccessibleConstructor(constructor);
					return constructor.newInstance(mainClass);
				}
			}
		}
		return null;
	}

	public static void run(Class<?> mainClass, String[] args) {
		configuration(mainClass, args);
		MainApplication application;
		try {
			application = getAutoMainApplicationImpl(mainClass, args);
		} catch (Exception e) {
			throw new ApplicationException("获取MainApplication实现异常", e);
		}

		if (application == null) {
			application = new MainApplication(mainClass, args);
		}

		FormatUtils.info(mainClass, "use application: {}", application.getClass()
				.getName());
		run(application);
	}

	public static void run(Class<?> mainClass) {
		run(mainClass, null);
	}
}
