package scw.application;

import java.util.Collection;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import com.alibaba.dubbo.config.ProtocolConfig;

import scw.application.consumer.AnnotationConsumerUtils;
import scw.application.consumer.XmlConsumerFactory;
import scw.application.crontab.CrontabAnnotationUtils;
import scw.beans.BeanUtils;
import scw.beans.XmlBeanFactory;
import scw.beans.async.AsyncCompleteFilter;
import scw.beans.property.ValueWiredManager;
import scw.beans.property.XmlPropertyFactory;
import scw.beans.rpc.dubbo.DubboUtils;
import scw.beans.tcc.TCCTransactionFilter;
import scw.beans.xml.XmlBeanUtils;
import scw.core.PropertyFactory;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.XTime;
import scw.logger.LoggerFactory;
import scw.logger.LoggerUtils;
import scw.sql.orm.ORMUtils;
import scw.transaction.TransactionFilter;

public class CommonApplication implements Application {
	public static final String DEFAULT_BEANS_PATH = "classpath:/beans.xml";

	private final XmlBeanFactory beanFactory;
	private volatile boolean start = false;
	private final PropertyFactory propertyFactory;
	private final String configPath;
	private final Timer timer;
	private final long defaultRefreshPeriod;

	public String getConfigPath() {
		return configPath;
	}

	public CommonApplication(String configXml, PropertyFactory propertyFactory, Timer timer,
			long defaultRefreshPeriod) {
		this.timer = timer;
		this.defaultRefreshPeriod = defaultRefreshPeriod;
		this.configPath = configXml;
		this.propertyFactory = propertyFactory == null
				? new XmlPropertyFactory(this.configPath, timer, defaultRefreshPeriod) : propertyFactory;
		this.beanFactory = getXmlBeanFactory();
		createAfter();
	}

	public CommonApplication(String configXml, long defaultRefreshPeriod) {
		this.defaultRefreshPeriod = defaultRefreshPeriod;
		this.timer = new Timer(getClass().getName());
		this.configPath = configXml;
		this.propertyFactory = new XmlPropertyFactory(getConfigPath(), timer, defaultRefreshPeriod);
		this.beanFactory = getXmlBeanFactory();
		createAfter();
	}

	public static void setGlobalDefaultRefreshPeriod(long refreshPeriod, TimeUnit timeUnit) {
		System.setProperty("scw_default_refresh_period", timeUnit.toMillis(refreshPeriod) + "");
	}

	// 默认30秒刷新一次
	public static long getGlobalDefaultRefreshPeriod() {
		return StringUtils.parseLong(SystemPropertyUtils.getProperty("scw_default_refresh_period"),
				XTime.ONE_SECOND * 30);
	}

	/**
	 * 全局是否自动刷新Value注解
	 * 
	 * @return
	 */
	public static boolean isGlobalValueWiredRefresh() {
		return StringUtils.parseBoolean(SystemPropertyUtils.getProperty("scw_force_refresh_value"));
	}

	public static void setGlobalValueWiredRefresh(boolean forceRefresh) {
		System.setProperty("scw_force_refresh_value", forceRefresh ? "1" : "0");
	}

	public CommonApplication(String configXml) {
		this(configXml, getGlobalDefaultRefreshPeriod());
	}

	public ValueWiredManager getValueWiredManager() {
		return getBeanFactory().getValueWiredManager();
	}

	public long getDefaultRefreshPeriod() {
		return defaultRefreshPeriod;
	}

	private XmlBeanFactory getXmlBeanFactory() {
		try {
			return new XmlBeanFactory(this.propertyFactory, getConfigPath(), timer, getDefaultRefreshPeriod(),
					isGlobalValueWiredRefresh()) {
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

	private void createAfter() {
		this.beanFactory.addFirstFilters(AsyncCompleteFilter.class.getName());
		this.beanFactory.addFirstFilters(TCCTransactionFilter.class.getName());
		this.beanFactory.addFirstFilters(TransactionFilter.class.getName());
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

		String ormScanPackageName = propertyFactory.getProperty("orm.scan");
		if (!StringUtils.isEmpty(ormScanPackageName)) {
			ORMUtils.registerCglibProxyTableBean(ormScanPackageName);
		} else {
			ORMUtils.registerCglibProxyTableBean(getORMPackage());
		}

		beanFactory.init();

		if (ResourceUtils.isExist(configPath)) {
			DubboUtils.exportService(beanFactory, propertyFactory, XmlBeanUtils.getRootNodeList(configPath));
		}

		CrontabAnnotationUtils.crontabService(ResourceUtils.getClassList(getCrontabAnnotationPackage()), beanFactory,
				getBeanFactory().getFilterNames());
		scanningConsumer();
	}

	private void scanningConsumer() {
		Collection<Class<?>> classes = ResourceUtils.getClassList(getConsumerAnnotationPackage());
		AnnotationConsumerUtils.scanningAMQPConsumer(getBeanFactory(), classes, getBeanFactory().getFilterNames());
		AnnotationConsumerUtils.scanningConsumer(beanFactory,
				new XmlConsumerFactory(beanFactory, propertyFactory, configPath), classes,
				getBeanFactory().getFilterNames());
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
