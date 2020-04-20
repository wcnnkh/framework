package scw.async.beans;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import scw.core.Destroy;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceFactory;
import scw.core.utils.TypeUtils;
import scw.io.FileManager;
import scw.io.SerializerUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public final class DefaultAsyncCompleteService implements AsyncCompleteService,
		Destroy {
	private static Logger logger = LoggerUtils
			.getLogger(AsyncCompleteFilter.class);

	private static ThreadLocal<Boolean> ENABLE_TAG = new ThreadLocal<Boolean>();

	public static boolean isEnable() {
		Boolean b = ENABLE_TAG.get();
		return b == null ? true : b;
	}

	public static void setEnable(boolean enable) {
		ENABLE_TAG.set(enable);
	}

	private FileManager fileManager;
	private final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(4);

	private InstanceFactory instanceFactory;

	public DefaultAsyncCompleteService(InstanceFactory instanceFactory) {
		this(instanceFactory, GlobalPropertyFactory.getInstance().getTempDirectoryPath());
	}

	public DefaultAsyncCompleteService(InstanceFactory instanceFactory,
			String logPath) {
		this.instanceFactory = instanceFactory;
		init(logPath);
	}

	private void init(String logPath) {
		logPath += File.separator + "AsyncComplate_"
				+ GlobalPropertyFactory.getInstance().getSystemOnlyId();
		logger.info("异步确认日志目录 ：{}", logPath);
		fileManager = new FileManager(logPath);

		File file = new File(fileManager.getRootPath());
		if (!file.exists()) {
			file.mkdirs();
		} else {
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					try {
						AsyncInvokeInfo info = SerializerUtils.readObject(f);
						executorService.submit(new InvokeRunnable(info, f
								.getPath()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void destroy() {
		executorService.shutdownNow();
	}

	final class InvokeRunnable implements Runnable {
		private final AsyncInvokeInfo info;
		private final String logPath;

		public InvokeRunnable(AsyncInvokeInfo info, String logPath) {
			this.info = info;
			this.logPath = logPath;
		}

		public void run() {
			Object rtn;
			try {
				rtn = info.invoke(instanceFactory);
				if (TypeUtils.isBoolean(info.getMethodConfig().getMethod()
						.getReturnType())) {
					if (rtn != null && (Boolean) rtn == false) {
						retry();
						return;
					}
				}
				deleteLog();
			} catch (Throwable e) {
				retry();
				e.printStackTrace();
			}
		}

		private void deleteLog() {
			File file = new File(logPath);
			if (file.exists()) {
				file.delete();
			}
		}

		private void retry() {
			executorService.schedule(this, info.getDelayMillis(),
					info.getTimeUnit());
		}
	}

	public Object service(AsyncInvokeInfo info) throws Throwable {
		File file = fileManager.createRandomFileWriteObject(info);
		return executorService.submit(new InvokeRunnable(info, file.getPath()));
	}
}
