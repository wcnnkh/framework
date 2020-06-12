package scw.complete;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import scw.aop.ProxyUtils;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.core.Destroy;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.ClassUtils;
import scw.io.support.LocalLogger.Record;
import scw.io.support.SystemLocalLogger;
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
public final class LocalCompleteService implements CompleteService, Destroy {
	private static Logger logger = LoggerUtils.getLogger(LocalCompleteService.class);
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
	private final SystemLocalLogger<CompleteTask> systemLocalLogger = new SystemLocalLogger<CompleteTask>(
			GlobalPropertyFactory.getInstance().getValue("scw.local.complete.name", String.class, "complete_service"));
	private final long delayMillis;
	private final TimeUnit delayTimeUnit;
	private final BeanFactory beanFactory;

	public LocalCompleteService(BeanFactory beanFactory) {
		this(beanFactory, 1, TimeUnit.MINUTES);
	}

	public LocalCompleteService(BeanFactory beanFactory, long delayMillis, TimeUnit delayTimeUnit) {
		this.beanFactory = beanFactory;
		this.delayMillis = delayMillis;
		this.delayTimeUnit = delayTimeUnit;
	}

	public void init() throws Exception {
		// 对意外结束的任务重新执行
		Enumeration<Record<CompleteTask>> enumeration = systemLocalLogger.enumeration();
		while (enumeration.hasMoreElements()) {
			Record<CompleteTask> record = enumeration.nextElement();
			Complete complete = new InternalComplete(record);
			logger.info("add internal runnable: " + record.getData());
			getExecutorService().execute(complete);
		}
	}

	public ScheduledExecutorService getExecutorService() {
		return executorService;
	}

	public Complete register(CompleteTask completeTask) throws IOException {
		Record<CompleteTask> record = systemLocalLogger.create(completeTask);
		return new InternalComplete(record);
	}

	public Object processTask(CompleteTask completeTask) throws Throwable {
		BeanDefinition beanDefinition = beanFactory == null ? null
				: beanFactory.getDefinition(ProxyUtils.getProxyFactory().getUserClass(completeTask.getClass()));
		if (beanDefinition != null) {
			beanDefinition.init(completeTask);
		}
		try {
			return completeTask.process();
		} finally {
			if (beanDefinition != null) {
				beanDefinition.destroy(completeTask);
			}
		}
	}

	final class InternalComplete implements Complete {
		private final Record<CompleteTask> record;
		private boolean cancel = false;

		public InternalComplete(Record<CompleteTask> record) {
			this.record = record;
		}

		public void cancel() {
			if (cancel) {
				return;
			}

			systemLocalLogger.getLocalLogger().delete(record.getId());
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
				rtn = processTask(record.getData());
				if (rtn != null && ClassUtils.isAssignableValue(boolean.class, rtn)) {
					if (!((Boolean) rtn).booleanValue()) {
						retry();
						return;
					}
				}
				cancel();
			} catch (Throwable e) {
				retry();
				logger.error(NestedExceptionUtils.getRootCause(e), "execute error retry soon: {}", record.getId());
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
