package scw.utils.crontab;

import java.lang.reflect.Method;
import java.util.HashSet;

import scw.aop.Invoker;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.annotation.Autowrite;
import scw.beans.annotation.InitMethod;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.utils.ClassUtils;
import scw.locks.Lock;
import scw.locks.LockFactory;

/**
 * 用来处理在分布试环境中任务重复执行的问题
 * 
 * @author shuchaowen
 *
 */
public final class CrontabService {
	private static Logger logger = LoggerFactory.getLogger(CrontabService.class);
	private scw.core.Crontab crontab;
	@Autowrite
	private BeanFactory beanFactory;
	private HashSet<String> taskNameSet = new HashSet<String>();
	private LockFactory lockFactory;

	public CrontabService(scw.core.Crontab crontab, LockFactory lockFactory, BeanFactory beanFactory) {
		this.crontab = crontab;
		this.beanFactory = beanFactory;
	}

	public CrontabService(LockFactory lockFactory) {
		this.lockFactory = lockFactory;
	}

	@InitMethod
	private void checkInit() {
		if (beanFactory == null) {
			throw new NullPointerException("beanFactory不可以为空");
		}

		if (crontab == null) {
			this.crontab = beanFactory.get(scw.core.Crontab.class);
		}
	}

	public synchronized void scan(String scanPackage) {
		for (Class<?> clz : ClassUtils.getClasses(scanPackage)) {
			for (Method method : ClassUtils.getAnnoationMethods(clz, true, true, Crontab.class)) {
				Crontab c = method.getAnnotation(Crontab.class);
				if (taskNameSet.contains(c.name())) {
					logger.warn("任务：" + c.name() + "已经存在");
					continue;
				}

				CrontabRun crontabRun = new CrontabRun(c.name(), lockFactory,
						BeanUtils.getInvoker(beanFactory, clz, method));
				crontab.crontab(c.dayOfWeek(), c.month(), c.dayOfMonth(), c.hour(), c.minute(), crontabRun);
			}
		}
	}

	final class CrontabRun implements Runnable {
		private final String name;
		private final LockFactory lockFactory;
		private final Invoker invoker;

		public CrontabRun(String name, LockFactory lockFactory, Invoker invoker) {
			this.name = name;
			this.lockFactory = lockFactory;
			this.invoker = invoker;
		}

		public void run() {
			if (lockFactory == null) {
				execute();
				return;
			}

			Lock lock = lockFactory.getLock(name);
			try {
				if (!lock.lock()) {
					return;
				}

				execute();
			} finally {
				lock.unlock();
			}
		}

		private void execute() {
			logger.trace("开始执行Crontab：{}" + name);
			try {
				invoker.invoke();
				logger.trace("执行Crontab结束：{}" + name);
			} catch (Throwable e) {
				logger.error(name, e);
			}
		}
	}
}
