package scw.application;

import java.util.Collection;

import scw.application.consumer.AnnotationConsumerUtils;
import scw.beans.BeanUtils;
import scw.beans.XmlBeanFactory;
import scw.core.resource.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.logger.LoggerUtils;
import scw.sql.orm.ORMUtils;
import scw.timer.TimerUtils;

public class CommonApplication extends XmlBeanFactory implements Application {
	public static final String DEFAULT_BEANS_PATH = "beans.xml";
	private volatile boolean start = false;

	public CommonApplication(String xmlConfigPath) {
		super(StringUtils.isEmpty(xmlConfigPath) ? DEFAULT_BEANS_PATH : xmlConfigPath);
	}

	public final XmlBeanFactory getBeanFactory() {
		return this;
	}

	protected String getAnnotationPackage() {
		return BeanUtils.getAnnotationPackage(getPropertyFactory());
	}

	protected String getORMPackage() {
		String orm = BeanUtils.getORMPackage(getPropertyFactory());
		return orm == null ? getAnnotationPackage() : orm;
	}

	@Override
	protected String getServicePackage() {
		String service = BeanUtils.getServiceAnnotationPackage(getPropertyFactory());
		return service == null ? getAnnotationPackage() : service;
	}

	protected String getCrontabAnnotationPackage() {
		String crontab = BeanUtils.getCrontabAnnotationPackage(getPropertyFactory());
		return crontab == null ? getAnnotationPackage() : crontab;
	}

	protected String getConsumerAnnotationPackage() {
		String consumer = BeanUtils.getConsumerAnnotationPackage(getPropertyFactory());
		return consumer == null ? getAnnotationPackage() : consumer;
	}

	@Override
	protected String getInitStaticPackage() {
		String init = BeanUtils.getInitStaticPackage(getPropertyFactory());
		return init == null ? getAnnotationPackage() : init;
	}

	protected String getStaticAnnotationPackage() {
		String init = BeanUtils.getInitStaticPackage(getPropertyFactory());
		return init == null ? getAnnotationPackage() : init;
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

		/**
		 * 使用容器进行初始化时，如果未找到log4j配置文件使用默认配置
		 */
		if (LoggerUtils.defaultConfigEnable() == null) {
			LoggerUtils.setDefaultConfigenable(true);
		}

		LoggerUtils.init();

		String ormScanPackageName = getPropertyFactory().getProperty("orm.scan");
		if (StringUtils.isNotEmpty(ormScanPackageName)) {
			ORMUtils.registerCglibProxyTableBean(ormScanPackageName);
		} else {
			ORMUtils.registerCglibProxyTableBean(getORMPackage());
		}

		super.init();
		TimerUtils.scanningAnnotation(ResourceUtils.getClassList(getCrontabAnnotationPackage()), getBeanFactory());
		scanningConsumer();
	}

	private void scanningConsumer() {
		Collection<Class<?>> classes = ResourceUtils.getClassList(getConsumerAnnotationPackage());
		AnnotationConsumerUtils.scanningAMQPConsumer(getBeanFactory(), classes);
		AnnotationConsumerUtils.scanningConsumer(getBeanFactory(), classes);
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

		super.destroy();
		LoggerUtils.destroy();
	}

	public synchronized static void run(final Class<?> clazz, String beanXml) {
		if (!ResourceUtils.isExist(beanXml)) {
			LoggerUtils.warn(TomcatApplication.class, "not found " + beanXml);
		}

		CommonApplication application;
		if (clazz == null) {
			application = new CommonApplication(beanXml);
		} else {
			application = new CommonApplication(beanXml) {
				@Override
				protected String getAnnotationPackage() {
					String[] arr = StringUtils.split(clazz.getName(), '.');
					if (arr.length < 2) {
						return super.getAnnotationPackage();
					} else if (arr.length == 2) {
						String p = super.getAnnotationPackage();
						if (StringUtils.isEmpty(p)) {
							return arr[0];
						} else {
							return p + "," + arr[0];
						}
					} else {
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < 2; i++) {
							if (i != 0) {
								sb.append(".");
							}
							sb.append(arr[i]);
						}

						String p = super.getAnnotationPackage();
						if (StringUtils.isEmpty(p)) {
							return sb.toString();
						} else {
							return p + "," + sb.toString();
						}
					}
				}
			};
		}
		application.init();
	}

	public static void run(Class<?> clazz) {
		run(clazz, DEFAULT_BEANS_PATH);
	}

	public static void run() {
		run(null);
	}
}
