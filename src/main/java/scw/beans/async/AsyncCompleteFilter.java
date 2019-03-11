package scw.beans.async;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import scw.beans.BeanFactory;
import scw.beans.annotaion.AsyncComplete;
import scw.beans.annotaion.Autowrite;
import scw.beans.annotaion.Destroy;
import scw.beans.annotaion.InitMethod;
import scw.beans.proxy.Filter;
import scw.beans.proxy.FilterChain;
import scw.beans.proxy.Invoker;
import scw.common.Base64;
import scw.common.FileManager;
import scw.common.utils.ConfigUtils;
import scw.common.utils.FileUtils;

public final class AsyncCompleteFilter implements Filter {
	private static ThreadLocal<Boolean> ENABLE_TAG = new ThreadLocal<Boolean>();

	public static boolean isEnable() {
		Boolean b = ENABLE_TAG.get();
		return b == null ? true : b;
	}

	public static void setEnable(boolean enable) {
		ENABLE_TAG.set(enable);
	}

	private FileManager fileManager;
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

	@Autowrite
	private BeanFactory beanFactory;

	@InitMethod
	private void init() throws UnsupportedEncodingException {
		String logPath = System.getProperty("java.io.tmpdir");
		String classPath = ConfigUtils.getClassPath();
		logPath += File.separator + "AsyncComplate_" + Base64.encode(classPath.getBytes("UTF-8"));
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
						new InvokeRunnable(info, f.getPath()).run();
						f.deleteOnExit();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Destroy
	private void destory() {
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
			ENABLE_TAG.set(false);
			try {
				info.invoke(beanFactory);
				File file = new File(logPath);
				if(file.exists()){
					file.delete();
				}
			} catch (Exception e) {
				executorService.schedule(this, info.getDelayMillis(), info.getTimeUnit());
				e.printStackTrace();
			}
		}
	}

	private void realFilter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		AsyncComplete asyncComplete = method.getAnnotation(AsyncComplete.class);
		if (asyncComplete == null) {
			filterChain.doFilter(invoker, proxy, method, args);
			return;
		}

		AsyncInvokeInfo info = new AsyncInvokeInfo(asyncComplete, method.getDeclaringClass(), method, args);
		File file = fileManager.createRandomFileWriteObject(info);
		InvokeRunnable runnable = new InvokeRunnable(info, file.getPath());
		runnable.run();
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		if (!isEnable()) {
			return filterChain.doFilter(invoker, proxy, method, args);
		}

		try {
			realFilter(invoker, proxy, method, args, filterChain);
		} finally {
			ENABLE_TAG.remove();
		}
		return null;
	}
}