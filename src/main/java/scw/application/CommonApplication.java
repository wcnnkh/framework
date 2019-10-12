package scw.application;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import scw.application.consumer.AnnotationConsumerUtils;
import scw.application.consumer.XmlConsumerFactory;
import scw.application.crontab.CrontabAnnotationUtils;
import scw.beans.BeanUtils;
import scw.beans.XmlBeanFactory;
import scw.beans.dubbo.DubboUtils;
import scw.beans.property.ValueWiredManager;
import scw.beans.property.XmlPropertyFactory;
import scw.beans.xml.XmlBeanUtils;
import scw.core.MultiPropertyFactory;
import scw.core.PropertyFactory;
import scw.core.resource.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.logger.LoggerUtils;
import scw.sql.orm.ORMUtils;

public class CommonApplication implements Application {
	public static final String DEFAULT_BEANS_PATH = "classpath:/beans.xml";

	private final XmlBeanFactory beanFactory;
	private volatile boolean start = false;
	private final MultiPropertyFactory propertyFactory = new MultiPropertyFactory();
	private final String configPath;
	private final int valueRefreshPeriod;
	private final int propertyRefreshPeriod;

	public CommonApplication(String configXml) {
		this(configXml, getGlobalValueWiredRefreshPeriod(), getGlobalPropertyRefreshPeriod());
	}

	public CommonApplication(String configXml, int valueRefreshPeriod, int propertyRefreshPeriod) {
		this.valueRefreshPeriod = valueRefreshPeriod;
		this.propertyRefreshPeriod = propertyRefreshPeriod;
		this.configPath = configXml;
		propertyFactory.add(new XmlPropertyFactory(getConfigPath(), propertyRefreshPeriod));
		this.beanFactory = getXmlBeanFactory();
	}

	public String getConfigPath() {
		return configPath;
	}

	/**
	 * 配置文件全局默认的刷新时间 单位：秒
	 * 
	 * @return
	 */
	public static int getGlobalPropertyRefreshPeriod() {
		return StringUtils.parseInt(SystemPropertyUtils.getProperty("scw_global_property_refresh_period"),
				(int) TimeUnit.MINUTES.toSeconds(1));
	}

	public static void setGlobalPropertyRefreshPeriod(int period) {
		SystemPropertyUtils.setPrivateProperty("scw_global_property_refresh_period", period + "");
	}

	/**
	 * value注解全局默认的刷新时间 单位：秒
	 * 
	 * @return
	 */
	public static int getGlobalValueWiredRefreshPeriod() {
		return StringUtils.parseInt(SystemPropertyUtils.getProperty("scw_global_value_wired_refresh_period"),
				(int) TimeUnit.MINUTES.toSeconds(1));
	}

	public static void setGlobalValueWiredRefreshPeriod(int period) {
		SystemPropertyUtils.setPrivateProperty("scw_global_value_wired_refresh_period", period + "");
	}

	public ValueWiredManager getValueWiredManager() {
		return getBeanFactory().getValueWiredManager();
	}

	public int getValueRefreshPeriod() {
		return valueRefreshPeriod;
	}

	public int getPropertyRefreshPeriod() {
		return propertyRefreshPeriod;
	}

	public void addPropertyFactory(PropertyFactory propertyFactory) {
		this.propertyFactory.add(propertyFactory);
	}

	private XmlBeanFactory getXmlBeanFactory() {
		try {
			return new XmlBeanFactory(this.propertyFactory, getConfigPath(), getValueRefreshPeriod()) {
				@Override
				protected String getInitStaticPackage() {
					return getStaticAnnotationPackage();
				}

				@Override
				protected String getServicePackage() {
					return getServiceAnnotationPackage();
				}
			};
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public final XmlBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public final PropertyFactory getPropertyFactory() {
		return beanFactory.getPropertyFactory();
	}

	protected String getAnnotationPackage() {
		return BeanUtils.getAnnotationPackage(propertyFactory);
	}

	protected String getORMPackage() {
		String orm = BeanUtils.getORMPackage(propertyFactory);
		return orm == null ? getAnnotationPackage() : orm;
	}

	protected String getServiceAnnotationPackage() {
		String service = BeanUtils.getServiceAnnotationPackage(propertyFactory);
		return service == null ? getAnnotationPackage() : service;
	}

	protected String getCrontabAnnotationPackage() {
		String crontab = BeanUtils.getCrontabAnnotationPackage(propertyFactory);
		return crontab == null ? getAnnotationPackage() : crontab;
	}

	protected String getConsumerAnnotationPackage() {
		String consumer = BeanUtils.getConsumerAnnotationPackage(propertyFactory);
		return consumer == null ? getAnnotationPackage() : consumer;
	}

	protected String getStaticAnnotationPackage() {
		String init = BeanUtils.getInitStaticPackage(propertyFactory);
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

		String ormScanPackageName = propertyFactory.getProperty("orm.scan");
		if (StringUtils.isNotEmpty(ormScanPackageName)) {
			ORMUtils.registerCglibProxyTableBean(ormScanPackageName);
		} else {
			ORMUtils.registerCglibProxyTableBean(getORMPackage());
		}

		beanFactory.init();

		CrontabAnnotationUtils.crontabService(ResourceUtils.getClassList(getCrontabAnnotationPackage()), beanFactory);
		scanningConsumer();

		if (ResourceUtils.isExist(configPath)) {
			DubboUtils.exportService(beanFactory, propertyFactory, XmlBeanUtils.getRootNodeList(configPath));
		}
	}

	private void scanningConsumer() {
		Collection<Class<?>> classes = ResourceUtils.getClassList(getConsumerAnnotationPackage());
		AnnotationConsumerUtils.scanningAMQPConsumer(getBeanFactory(), classes);
		AnnotationConsumerUtils.scanningConsumer(beanFactory,
				new XmlConsumerFactory(beanFactory, propertyFactory, configPath), classes);
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

		propertyFactory.destroy();
		DubboUtils.destoryAll();
		beanFactory.destroy();
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
