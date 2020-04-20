package scw.async.local;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import scw.async.AsyncException;
import scw.async.AsyncRunnable;
import scw.core.utils.ClassUtils;
import scw.io.FileUtils;
import scw.io.JavaSerializer;
import scw.io.ObjectFileManager;

public class FileLocalAsyncExecutor extends ExecutorServiceAsyncExecutor {
	private final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(4);
	private final ObjectFileManager objectFileManager;
	private final long delayMillis;
	private final TimeUnit delayTimeUnit;

	public FileLocalAsyncExecutor(String suffix, long delayMillis,
			TimeUnit delayTimeUnit) {
		this(new ObjectFileManager(FileUtils.getTempDirectory(), suffix,
				JavaSerializer.SERIALIZER), delayMillis, delayTimeUnit);
	}

	public FileLocalAsyncExecutor(ObjectFileManager objectFileManager,
			long delayMillis, TimeUnit delayTimeUnit) {
		super(true);
		this.objectFileManager = objectFileManager;
		this.delayMillis = delayMillis;
		this.delayTimeUnit = delayTimeUnit;
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
				rtn = asyncRunnable.call();
				if (rtn != null
						&& ClassUtils.isAssignableValue(boolean.class, rtn)) {
					if (!((Boolean) rtn).booleanValue()) {
						retry();
						return;
					}
				}
				objectFileManager.delete(index);
			} catch (Throwable e) {
				retry();
				logger.error(e, "execute error retry soon [{}]", asyncRunnable);
				e.printStackTrace();
			}
		}

		private void retry() {
			executorService.schedule(this, delayMillis, delayTimeUnit);
		}
	}
}
