package scw.complete;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import scw.aop.ProxyUtils;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.core.Destroy;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.ClassUtils;
import scw.io.serialzer.ObjectFileManager;
import scw.io.serialzer.ObjectFileManager.ObjectInfo;
import scw.lang.NestedExceptionUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

/**
 * 依赖本地文件系统实现的确认机制
 * 
 * @author shuchaowen
 *
 */

@Configuration(order = Integer.MIN_VALUE, value = CompleteService.class)
public class LocalCompleteService implements CompleteService, Destroy {
	private static Logger logger = LoggerUtils.getLogger(LocalCompleteService.class);
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
	private final ObjectFileManager objectFileManager;
	private final long delayMillis;
	private final TimeUnit delayTimeUnit;
	private final BeanFactory beanFactory;

	public LocalCompleteService(BeanFactory beanFactory) {
		this(beanFactory, "complete", 1, TimeUnit.MINUTES);
	}

	public LocalCompleteService(BeanFactory beanFactory, String suffix, long delayMillis, TimeUnit delayTimeUnit) {
		this(beanFactory, new ObjectFileManager(suffix), delayMillis, delayTimeUnit);
	}

	public LocalCompleteService(BeanFactory beanFactory, ObjectFileManager objectFileManager, long delayMillis,
			TimeUnit delayTimeUnit) {
		this.beanFactory = beanFactory;
		this.objectFileManager = objectFileManager;
		this.delayMillis = delayMillis;
		this.delayTimeUnit = delayTimeUnit;
	}

	public void init() throws Exception {
		// 对意外结束的任务重新执行
		for (ObjectInfo objectInfo : objectFileManager.getObjectList()) {
			CompleteTask completeTask = (CompleteTask) objectInfo.getInstance();
			Complete complete = new InternalComplete(completeTask, objectInfo.getIndex());
			logger.info("add internal runnable: " + completeTask);
			getExecutorService().execute(complete);
		}
	}

	public ScheduledExecutorService getExecutorService() {
		return executorService;
	}

	public Complete register(CompleteTask completeTask) throws IOException {
		long index = objectFileManager.writeObject(completeTask);
		return new InternalComplete(completeTask, index);
	}

	public Object processTask(CompleteTask completeTask) throws Throwable {
		BeanDefinition beanDefinition = beanFactory
				.getDefinition(ProxyUtils.getProxyFactory().getUserClass(completeTask.getClass()));
		if (beanDefinition != null) {
			beanDefinition.init(completeTask);
		}
		try {
			return completeTask.process();
		} finally {
			beanDefinition.destroy(completeTask);
		}
	}

	public ObjectFileManager getObjectFileManager() {
		return objectFileManager;
	}

	final class InternalComplete implements Complete {
		private final CompleteTask completeTask;
		private final long index;
		private boolean cancel = false;

		public InternalComplete(CompleteTask completeTask, long index) {
			this.completeTask = completeTask;
			this.index = index;
		}

		public void cancel() {
			if (cancel) {
				return;
			}

			objectFileManager.delete(index);
			cancel = true;
		}

		public boolean isCancel() {
			return cancel;
		}

		public void run() {
			if (isCancel()) {
				return;
			}

			Object rtn;
			try {
				rtn = processTask(completeTask);
				if (rtn != null && ClassUtils.isAssignableValue(boolean.class, rtn)) {
					if (!((Boolean) rtn).booleanValue()) {
						retry();
						return;
					}
				}
				cancel();
			} catch (Throwable e) {
				retry();
				logger.error(NestedExceptionUtils.getRootCause(e), "execute error retry soon [{}]", completeTask);
			}
		}

		private void retry() {
			executorService.schedule(this, delayMillis, delayTimeUnit);
		}
	}

	public void destroy() throws Exception {
		executorService.shutdownNow();
	}
}
