package io.basc.framework.boot.support;

import java.util.OptionalInt;
import java.util.concurrent.CountDownLatch;

import io.basc.framework.beans.factory.Scope;
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
	private final Class<?> mainClass;
	private final MainArgs mainArgs;

	public MainApplication(Scope scope, Class<?> mainClass, @Nullable String[] args) {
		super(scope);
		Assert.requiredArgument(mainClass != null, "mainClass");
		this.mainClass = mainClass;
		this.mainArgs = new MainArgs(args);
		OptionalInt port = mainArgs.getPort();
		if (port.isPresent()) {
			setPort(port.getAsInt());
		}

		// 设置最高优先级
		getProperties().registerFirst(mainArgs);
		setClassLoader(mainClass.getClassLoader());
		setLogger(LoggerFactory.getLogger(mainClass));
		getLogger().debug("args:{}", this.mainArgs);
	}

	public Class<?> getMainClass() {
		return mainClass;
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
	protected void _init() {
		source(mainClass);
		super._init();
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
		run.setContextClassLoader(mainClass.getClassLoader());
		run.setName(mainClass.getSimpleName());
		run.setDaemon(false);
		run.start();

		Thread shutdown = new Thread(() -> destroy());
		shutdown.setContextClassLoader(mainClass.getClassLoader());
		shutdown.setName(mainClass.getSimpleName() + "-shutdown");
		Runtime.getRuntime().addShutdownHook(shutdown);
		return start;
	}

	public static ListenableFuture<Application> run(Scope scope, Class<?> mainClass, @Nullable String[] args) {
		MainApplication application = new MainApplication(scope, mainClass, args);
		return application.start();
	}

	public static ListenableFuture<Application> run(Scope scope, Class<?> mainClass, @Nullable String[] args,
			Class<?>... sourceClasses) {
		MainApplication application = new MainApplication(scope, mainClass, args);
		for (Class<?> source : sourceClasses) {
			application.source(source);
		}
		return application.start();
	}

	public static ListenableFuture<Application> run(Scope scope, Class<?> mainClass) {
		MainApplication application = new MainApplication(scope, mainClass, null);
		return application.start();
	}

	public static ListenableFuture<Application> run(Scope scope, Class<?> mainClass, Class<?>... sourceClasses) {
		return run(scope, mainClass, null, sourceClasses);
	}

	public static ListenableFuture<Application> run(Class<?> mainClass, @Nullable String[] args) {
		return run(Scope.DEFAULT, mainClass, args);
	}

	public static ListenableFuture<Application> run(Class<?> mainClass) {
		return run(Scope.DEFAULT, mainClass);
	}
}
