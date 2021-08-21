package scw.util.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

import scw.core.Assert;
import scw.lang.Nullable;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.XUtils;

/**
 * 任务队列 默认是一个守护线程自动退出
 * 
 * @author shuchaowen
 *
 */
public class TaskQueue extends Thread implements AsyncExecutor {
	private static Logger logger = LoggerFactory.getLogger(TaskQueue.class);
	private final BlockingQueue<Runnable> queue;
	private boolean tryGet = true;

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
		synchronized (queue) {
			return queue.poll();
		}
	}

	/**
	 * 执行完后是否尝试调用{@see Future#get()}
	 * 
	 * @return
	 */
	public boolean isTryGet() {
		return tryGet;
	}

	public void setTryGet(boolean tryGet) {
		this.tryGet = tryGet;
	}

	protected void run(Runnable task) throws Throwable {
		task.run();
		
		if (isTryGet()) {
			//其中一个目的是为了方便输出异常日志
			if (task instanceof Future) {
				((Future<?>) task).get();
			}
		}
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			Runnable task;
			synchronized (queue) {
				try {
					task = queue.take();
				} catch (InterruptedException e) {
					continue;
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Thread[{}] execute task: {}", getName(), task);
			}
			try {
				run(task);
			} catch (Throwable e) {
				logger.error(e, "Thread[{}] execute task fail: {}", getName(), task);
			}
		}
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		Assert.requiredArgument(task != null, "task");
		final String taskId = XUtils.getUUID();
		FutureTask<T> future = new FutureTask<T>(task) {
			@Override
			public String toString() {
				return taskId + "[" + task.toString() + "]";
			}
		};

		if (isInterrupted()) {
			throw new RejectedExecutionException(String.valueOf(future));
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Thread[{}] submit task: {}", Thread.currentThread().getName(), future);
		}

		if (!queue.offer(future)) {
			throw new RejectedExecutionException(String.valueOf(future));
		}
		return future;
	}
}
