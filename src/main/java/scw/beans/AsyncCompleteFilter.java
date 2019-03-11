package scw.beans;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import scw.beans.annotaion.AsyncComplete;
import scw.beans.annotaion.Autowrite;
import scw.beans.annotaion.Destroy;
import scw.beans.annotaion.InitMethod;
import scw.beans.proxy.Filter;
import scw.beans.proxy.FilterChain;
import scw.beans.proxy.Invoker;
import scw.common.Base64;
import scw.common.FileManager;
import scw.common.MethodConfig;
import scw.common.utils.ConfigUtils;
import scw.common.utils.FileUtils;

public final class AsyncCompleteFilter implements Filter {
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
						new InvokeRunnable(info, f.getPath()).start();
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
		private ScheduledFuture<?> scheduledFuture;

		public InvokeRunnable(AsyncInvokeInfo info, String logPath) {
			this.info = info;
			this.logPath = logPath;
		}

		public void run() {
			try {
				info.invoke();
				File file = new File(logPath);
				file.deleteOnExit();
				scheduledFuture.cancel(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void start() {
			scheduledFuture = executorService.scheduleAtFixedRate(this, 0, info.getDelayMillis(), TimeUnit.SECONDS);
		}
	}

	final class AsyncInvokeInfo implements Serializable {
		private static final long serialVersionUID = 1L;
		private MethodConfig methodConfig;
		private long delayMillis;
		private TimeUnit timeUnit;
		private Object[] args;

		public AsyncInvokeInfo() {
		};

		public AsyncInvokeInfo(AsyncComplete asyncComplete, Class<?> clz, Method method, Object[] args) {
			this.delayMillis = asyncComplete.delayMillis();
			this.methodConfig = new MethodConfig(clz, method);
			this.timeUnit = asyncComplete.timeUnit();
			this.args = args;
		}

		public long getDelayMillis() {
			return delayMillis;
		}

		public TimeUnit getTimeUnit() {
			return timeUnit;
		}

		public Object invoke() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
				NoSuchMethodException, SecurityException {
			Object bean = beanFactory.get(methodConfig.getClz());
			return methodConfig.getMethod().invoke(bean, args);
		}
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain chain)
			throws Throwable {
		AsyncComplete asyncComplete = method.getAnnotation(AsyncComplete.class);
		if (asyncComplete == null) {
			return chain.doFilter(invoker, proxy, method, args, chain);
		}

		AsyncInvokeInfo info = new AsyncInvokeInfo(asyncComplete, method.getDeclaringClass(), method, args);
		File file = fileManager.createRandomFileWriteObject(info);
		InvokeRunnable runnable = new InvokeRunnable(info, file.getPath());
		runnable.start();
		return null;
	}
}