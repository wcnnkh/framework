package scw.util.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

import scw.lang.Nullable;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

/**
 * 任务队列 默认是一个守护线程自动退出
 * 
 * @author shuchaowen
 *
 */
public class TaskQueue extends Thread implements AsyncExecutor {
	private static Logger logger = LoggerFactory.getLogger(TaskQueue.class);
	private final BlockingQueue<Runnable> queue;

	public TaskQueue() {
		this(new LinkedBlockingQueue<>());
	}

	public TaskQueue(BlockingQueue<Runnable> queue) {
		this.queue = queue;
		// 守护线程自动退出
		setDaemon(true);
	}

	@Nullable
	public Runnable poll() {
		synchronized (this) {
			return queue.poll();
		}
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			Runnable task;
			synchronized (this) {
				try {
					task = queue.take();
				} catch (InterruptedException e) {
					continue;
				}
			}

			if(logger.isDebugEnabled()) {
				logger.debug("execute: {}", task);
			}
			try {
				task.run();
			} catch (Throwable e) {
				logger.error(e, "task:{}", task);
			}
		}
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		FutureTask<T> future = new FutureTask<T>(task);
		if (!queue.offer(future)) {
			throw new RejectedExecutionException(String.valueOf(task));
		}
		return future;
	}
}
