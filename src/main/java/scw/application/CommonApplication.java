package scw.application;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

import com.alibaba.dubbo.config.ProtocolConfig;

import scw.application.crontab.Crontab;
import scw.application.crontab.CrontabContext;
import scw.application.crontab.CrontabContextFactory;
import scw.application.mq.AnnotationMQUtils;
import scw.beans.BeanUtils;
import scw.beans.CommonFilter;
import scw.beans.XmlBeanFactory;
import scw.beans.property.XmlPropertiesFactory;
import scw.beans.rpc.dubbo.XmlDubboUtils;
import scw.core.PropertiesFactory;
import scw.core.aop.Invoker;
import scw.core.exception.AlreadyExistsException;
import scw.core.logger.LoggerFactory;
import scw.core.logger.LoggerUtils;
import scw.core.utils.AnnotationUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.sql.orm.ORMUtils;

public class CommonApplication implements Application {
	private final XmlBeanFactory beanFactory;
	private volatile boolean start = false;
	private final PropertiesFactory propertiesFactory;
	private final String configPath;

	public CommonApplication(String configPath, boolean initStatic, PropertiesFactory propertiesFactory) {
		this.configPath = configPath;
		this.propertiesFactory = propertiesFactory == null ? new XmlPropertiesFactory(configPath) : propertiesFactory;
		try {
			this.beanFactory = new XmlBeanFactory(this.propertiesFactory, configPath, initStatic);
			this.beanFactory.addFirstFilters(CommonFilter.class.getName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getConfigPath() {
		return configPath;
	}

	public CommonApplication(String configXml, boolean initStatic) {
		this(configXml, initStatic, new XmlPropertiesFactory(configXml));
	}

	public Collection<Class<?>> getClasses() {
		return ClassUtils.getClasses(beanFactory.getPackages());
	}

	public XmlBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public PropertiesFactory getPropertiesFactory() {
		return beanFactory.getPropertiesFactory();
	}

	public void init() {
		if (start) {
			throw new RuntimeException("已经启动了");
		}

		synchronized (this) {
			if (start) {
				throw new RuntimeException("已经启动了");
			}

			start = true;
		}

		String ormScanPackageName = propertiesFactory.getValue("orm.scan");
		if (!StringUtils.isEmpty(ormScanPackageName)) {
			ORMUtils.registerCglibProxyTableBean(ormScanPackageName);
		}

		beanFactory.init();

		if (!StringUtils.isNull(configPath)) {
			XmlDubboUtils.serviceExport(propertiesFactory, beanFactory, configPath);
		}

		crontabService();
		AnnotationMQUtils.scanningAMQPParamsConsumer(getBeanFactory(), getClasses(), getBeanFactory().getFilterNames());
	}

	public void destroy() {
		if (!start) {
			throw new RuntimeException("还未启动，无法销毁");
		}

		synchronized (this) {
			if (!start) {
				throw new RuntimeException("还未启动，无法销毁");
			}

			start = false;
		}

		ProtocolConfig.destroyAll();
		beanFactory.destroy();
		LoggerFactory.destroy();
	}

	protected final void crontabService() {
		HashSet<String> taskNameSet = new HashSet<String>();
		for (Class<?> clz : getClasses()) {
			for (Method method : AnnotationUtils.getAnnoationMethods(clz, true, true, Crontab.class)) {
				Crontab c = method.getAnnotation(Crontab.class);
				if (taskNameSet.contains(c.name())) {
					throw new AlreadyExistsException("任务：" + c.name() + "已经存在");
				}
				scw.core.Crontab crontab = getBeanFactory().get(scw.core.Crontab.class);

				CrontabRun crontabRun = new CrontabRun(c.name(), getBeanFactory().get(c.factory()),
						BeanUtils.getInvoker(getBeanFactory(), clz, method));
				crontab.crontab(c.dayOfWeek(), c.month(), c.dayOfMonth(), c.hour(), c.minute(), crontabRun);
				LoggerUtils.info(CommonApplication.class,
						"添加计划任务：{},dayOfWeek={},month={},dayOfMonth={},hour={},minute={}", c.name(), c.dayOfWeek(),
						c.month(), c.dayOfMonth(), c.hour(), c.minute());
			}
		}
	}

	final class CrontabRun implements Runnable {
		private final String name;
		private final Invoker invoker;
		private final CrontabContextFactory crontabContextFactory;

		public CrontabRun(String name, CrontabContextFactory crontabContextFactory, Invoker invoker) {
			this.name = name;
			this.crontabContextFactory = crontabContextFactory;
			this.invoker = invoker;
		}

		public void run() {
			CrontabContext context = crontabContextFactory.getContext(name);
			if (context == null) {
				execute();
			} else {
				if (!context.begin()) {
					context.completet();
					return;
				}

				try {
					invoker.invoke();
					context.end();
				} catch (Throwable e) {
					context.error(e);
				} finally {
					context.completet();
				}
			}
		}

		private void execute() {
			LoggerUtils.info(CrontabRun.class, "开始执行Crontab：{}" + name);
			try {
				invoker.invoke();
				LoggerUtils.info(CrontabRun.class, "执行Crontab结束：{}" + name);
			} catch (Throwable e) {
				LoggerUtils.warn(CrontabRun.class, name);
				e.printStackTrace();
			}
		}
	}
}
