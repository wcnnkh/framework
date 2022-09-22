package io.basc.framework.boot.support;

import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.util.concurrent.ListenableFuture;
import io.basc.framework.util.concurrent.SettableListenableFuture;

import java.util.function.Consumer;

public class ApplicationRunner<T extends ConfigurableApplication> {
	private final T application;
	private final String threadName;

	public ApplicationRunner(T application, String threadName) {
		this.application = application;
		this.threadName = threadName;
	}

	public ApplicationRunner<T> config(Consumer<T> consumer) {
		consumer.accept(application);
		return this;
	}

	public ListenableFuture<T> start() {
		Runtime.getRuntime().addShutdownHook(new ApplicationShutdown());
		ApplicationStart start = new ApplicationStart();
		Thread run = new Thread(start);
		run.setContextClassLoader(application.getClassLoader());
		run.setName(threadName);
		run.setDaemon(false);
		run.start();
		return start;
	}

	private class ApplicationStart extends SettableListenableFuture<T> implements Runnable {

		public void run() {
			try {
				application.init();
				set(application);
				while (true) {
					try {
						Thread.sleep(Long.MAX_VALUE);
					} catch (InterruptedException e) {
						break;
					}
				}
			} catch (Throwable e) {
				application.getLogger().error(e, "Initialization exception");
				setException(e);
			}
		}
	}

	private class ApplicationShutdown extends Thread {
		public ApplicationShutdown() {
			setName("shutdown-" + threadName);
			setContextClassLoader(application.getClassLoader());
		}

		@Override
		public void run() {
			try {
				application.destroy();
			} catch (Throwable e) {
				application.getLogger().error(e, "destroy error");
			}
			super.run();
		}
	}
}
