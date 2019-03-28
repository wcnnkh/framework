package scw.beans.async;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.beans.BeanFactory;
import scw.beans.annotaion.AsyncComplete;
import scw.beans.annotaion.Autowrite;
import scw.beans.annotaion.Destroy;
import scw.beans.annotaion.InitMethod;
import scw.common.Base64;
import scw.common.FileManager;
import scw.common.MethodDefinition;
import scw.common.utils.ClassUtils;
import scw.common.utils.ConfigUtils;
import scw.common.utils.FileUtils;

/**
 * 只能受BeanFactory管理
 * @author shuchaowen
 *
 */
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

	protected AsyncCompleteFilter() {
	}

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
						executorService.submit(new InvokeRunnable(info, f.getPath()));
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
			Object rtn;
			try {
				rtn = info.invoke(beanFactory);
				if (ClassUtils.isBooleanType(info.getMethodConfig().getReturnType())) {
					if (rtn != null && (Boolean) rtn == false) {
						retry();
						return;
					}
				}
				deleteLog();
			} catch (Exception e) {
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
			executorService.schedule(this, info.getDelayMillis(), info.getTimeUnit());
		}
	}

	private Object realFilter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		if (!isEnable()) {
			return filterChain.doFilter(invoker, proxy, method, args);
		}

		AsyncComplete asyncComplete = method.getAnnotation(AsyncComplete.class);
		if (asyncComplete == null) {
			return filterChain.doFilter(invoker, proxy, method, args);
		}

		AsyncInvokeInfo info = new AsyncInvokeInfo(asyncComplete, method.getDeclaringClass(), method, args);
		File file = fileManager.createRandomFileWriteObject(info);
		executorService.submit(new InvokeRunnable(info, file.getPath()));
		return null;
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		try {
			return realFilter(invoker, proxy, method, args, filterChain);
		} finally {
			ENABLE_TAG.remove();
		}
	}
}

class AsyncInvokeInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private MethodDefinition methodConfig;
	private long delayMillis;
	private TimeUnit timeUnit;
	private Object[] args;

	public AsyncInvokeInfo() {
	};

	public AsyncInvokeInfo(AsyncComplete asyncComplete, Class<?> clz, Method method, Object[] args) {
		this.delayMillis = asyncComplete.delayMillis();
		this.methodConfig = new MethodDefinition(clz, method);
		this.timeUnit = asyncComplete.timeUnit();
		this.args = args;
	}

	public MethodDefinition getMethodConfig() {
		return methodConfig;
	}

	public long getDelayMillis() {
		return delayMillis;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public Object invoke(BeanFactory beanFactory) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		Object bean = beanFactory.get(methodConfig.getBelongClass());
		return methodConfig.getMethod().invoke(bean, args);
	}
}