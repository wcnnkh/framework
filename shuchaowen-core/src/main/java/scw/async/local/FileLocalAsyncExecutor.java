package scw.async.local;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import scw.async.AsyncException;
import scw.async.AsyncRunnable;
import scw.beans.BeanFactory;
import scw.core.utils.ClassUtils;
import scw.io.ObjectFileManager;
import scw.io.ObjectFileManager.ObjectInfo;
import scw.lang.NestedExceptionUtils;

public class FileLocalAsyncExecutor extends ExecutorServiceAsyncExecutor {
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
	private final ObjectFileManager objectFileManager;
	private final long delayMillis;
	private final TimeUnit delayTimeUnit;

	public FileLocalAsyncExecutor(BeanFactory beanFactory, String suffix, long delayMillis, TimeUnit delayTimeUnit)
			throws IOException, ClassNotFoundException {
		this(beanFactory, new ObjectFileManager(suffix), delayMillis, delayTimeUnit);
	}

	public FileLocalAsyncExecutor(BeanFactory beanFactory, ObjectFileManager objectFileManager, long delayMillis,
			TimeUnit delayTimeUnit) throws IOException, ClassNotFoundException {
		super(beanFactory, true);
		this.objectFileManager = objectFileManager;
		this.delayMillis = delayMillis;
		this.delayTimeUnit = delayTimeUnit;

		// 对意外结束的任务重新执行
		for (ObjectInfo objectInfo : objectFileManager.getObjectList()) {
			AsyncRunnable asyncRunnable = (AsyncRunnable) objectInfo.getInstance();
			InternalRunnable internalRunnable = new InternalRunnable(asyncRunnable,
					objectInfo.getIndex());
			logger.info("add internal runnable: " + asyncRunnable);
			getExecutorService().execute(internalRunnable);
		}
	}

	public ScheduledExecutorService getExecutorService() {
		return executorService;
	}

	@Override
	protected Runnable createRunnable(AsyncRunnable asyncRunnable) {
		try {
			long index = objectFileManager.writeObject(asyncRunnable);
			return new InternalRunnable(asyncRunnable, index);
		} catch (IOException e) {
			throw new AsyncException(e);
		}
	}

	public ObjectFileManager getObjectFileManager() {
		return objectFileManager;
	}

	final class InternalRunnable implements Runnable {
		private final AsyncRunnable asyncRunnable;
		private final long index;

		public InternalRunnable(AsyncRunnable asyncRunnable, long index) {
			this.asyncRunnable = asyncRunnable;
			this.index = index;
		}

		public void run() {
			Object rtn;
			try {
				rtn = call(asyncRunnable);
				if (rtn != null && ClassUtils.isAssignableValue(boolean.class, rtn)) {
					if (!((Boolean) rtn).booleanValue()) {
						retry();
						return;
					}
				}
				objectFileManager.delete(index);
			} catch (Throwable e) {
				retry();
				logger.error(NestedExceptionUtils.getRootCause(e), "execute error retry soon [{}]", asyncRunnable);
			}
		}

		private void retry() {
			executorService.schedule(this, delayMillis, delayTimeUnit);
		}
	}
}
