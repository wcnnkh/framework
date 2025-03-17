package run.soeasy.framework.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadPerTaskExecutor implements Executor {
	private ThreadFactory threadFactory;

	public ThreadPerTaskExecutor() {
		this(Executors.defaultThreadFactory());
	}

	public ThreadPerTaskExecutor(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}

	@Override
	public void execute(Runnable command) {
		Thread thread = threadFactory.newThread(command);
		thread.start();
	}

}
