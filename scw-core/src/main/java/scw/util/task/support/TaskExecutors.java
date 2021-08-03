package scw.util.task.support;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import scw.util.task.AsyncListenableTaskExecutor;
import scw.util.task.DefaultAsyncTaskExecutor;

public class TaskExecutors {
	private TaskExecutors() {
	}

	private static volatile AsyncListenableTaskExecutor executor;

	/**
	 * 这是一个会自动关闭的执行器
	 * 
	 * @return
	 */
	public static AsyncListenableTaskExecutor getGlobalExecutor() {
		if (executor == null) {
			synchronized (TaskExecutors.class) {
				if (executor == null) {
					ExecutorService executorService = Executors.newWorkStealingPool();
					Thread thread = new Thread(() -> {
						if (!executorService.isShutdown()) {
							executorService.shutdownNow();
						}
					}, "WorkStealingPool sutdown(Executors)");
					Runtime.getRuntime().addShutdownHook(thread);
					executor = new DefaultAsyncTaskExecutor(executorService);
				}
			}
		}
		return executor;
	}
}
