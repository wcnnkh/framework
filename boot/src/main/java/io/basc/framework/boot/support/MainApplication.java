package io.basc.framework.boot.support;

import java.util.concurrent.CountDownLatch;

import io.basc.framework.boot.Application;
import io.basc.framework.env.MainArgs;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.concurrent.ListenableFuture;
import io.basc.framework.util.concurrent.SettableListenableFuture;

public class MainApplication extends DefaultApplication implements Runnable {
	private SettableListenableFuture<Application> start;
	private CountDownLatch startLatch;
	private final Class<?> sourceClass;
	private final MainArgs mainArgs;

	public MainApplication(Class<?> sourceClass, @Nullable String[] args) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		this.sourceClass = sourceClass;
		this.mainArgs = new MainArgs(args);
		Integer port = mainArgs.getPort();
		if (port != null) {
			setPort(port);
		}

		getProperties().getTandemFactories().addService(mainArgs);
		setClassLoader(sourceClass.getClassLoader());
		source(sourceClass);
		setLogger(LoggerFactory.getLogger(sourceClass));
		getLogger().debug("args:{}", this.mainArgs);
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	public MainArgs getMainArgs() {
		return mainArgs;
	}

	@Override
	public void destroy() {
		synchronized (this) {
			if (startLatch != null) {
				startLatch.countDown();
			}
		}
		super.destroy();
	}

	@Override
	public final void run() {
		synchronized (this) {
			if (startLatch != null) {
				return;
			}
			startLatch = new CountDownLatch(1);
		}

		try {
			init();
			start.set(this);
		} catch (Throwable e) {
			start.setException(e);
			getLogger().error(e, "Initialization exception");
		} finally {
			try {
				startLatch.await();
			} catch (InterruptedException e) {
			}
		}
	}

	public final synchronized ListenableFuture<Application> start() {
		if (start != null) {
			return start;
		}

		start = new SettableListenableFuture<>();
		Thread run = new Thread(this);
		run.setContextClassLoader(getClassLoader());
		run.setName(sourceClass.getSimpleName());
		run.setDaemon(false);
		run.start();

		Thread shutdown = new Thread(() -> destroy());
		shutdown.setContextClassLoader(getClassLoader());
		shutdown.setName(sourceClass.getSimpleName() + "-shutdown");
		Runtime.getRuntime().addShutdownHook(shutdown);
		return start;
	}

	public static ListenableFuture<Application> run(Class<?> mainClass, @Nullable String[] args) {
		MainApplication application = new MainApplication(mainClass, args);
		return application.start();
	}

	public static ListenableFuture<Application> run(Class<?> mainClass, @Nullable String[] args,
			Class<?>... sourceClasses) {
		MainApplication application = new MainApplication(mainClass, args);
		for (Class<?> source : sourceClasses) {
			application.source(source);
		}
		return application.start();
	}

	public static ListenableFuture<Application> run(Class<?> mainClass) {
		MainApplication application = new MainApplication(mainClass, null);
		return application.start();
	}

	public static ListenableFuture<Application> run(Class<?> mainClass, Class<?>... sourceClasses) {
		return run(mainClass, null, sourceClasses);
	}
}
