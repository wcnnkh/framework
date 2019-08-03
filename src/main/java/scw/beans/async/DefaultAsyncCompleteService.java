package scw.beans.async;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import scw.beans.BeanFactory;
import scw.beans.annotation.Autowired;
import scw.beans.annotation.InitMethod;
import scw.core.Destroy;
import scw.core.utils.ClassUtils;
import scw.core.utils.FileManager;
import scw.core.utils.SystemPropertyUtils;
import scw.io.FileUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class DefaultAsyncCompleteService implements AsyncCompleteService,
		Destroy {
	private static Logger logger = LoggerFactory
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

	@Autowired
	private BeanFactory beanFactory;

	@InitMethod
	private void init() throws UnsupportedEncodingException {
		String logPath = System.getProperty("java.io.tmpdir");
		logPath += File.separator + "AsyncComplate_"
				+ SystemPropertyUtils.getSystemOnlyId();
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
						AsyncInvokeInfo info = FileUtils.readObject(f);
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
				rtn = info.invoke(beanFactory);
				if (ClassUtils.isBooleanType(info.getMethodConfig().getMethod()
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
