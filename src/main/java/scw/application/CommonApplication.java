package scw.application;

import java.util.Collection;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

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
	private final int valueRefreshPeriod;
	private final int propertyRefreshPeriod;

	public String getConfigPath() {
		return configPath;
	}

	public CommonApplication(String configXml, PropertyFactory propertyFactory, Timer timer, int valueRefreshPeriod,
			int propertyRefreshPeriod) {
		this.timer = timer;
		this.valueRefreshPeriod = valueRefreshPeriod;
		this.propertyRefreshPeriod = propertyRefreshPeriod;
		this.configPath = configXml;
		this.propertyFactory = propertyFactory == null
				? new XmlPropertyFactory(this.configPath, timer, propertyRefreshPeriod) : propertyFactory;
		this.beanFactory = getXmlBeanFactory();
		createAfter();
	}

	public CommonApplication(String configXml, int valueRefreshPeriod, int propertyRefreshPeriod) {
		this.timer = new Timer(getClass().getName());
		this.valueRefreshPeriod = valueRefreshPeriod;
		this.propertyRefreshPeriod = propertyRefreshPeriod;
		this.configPath = configXml;
		this.propertyFactory = new XmlPropertyFactory(getConfigPath(), timer, propertyRefreshPeriod);
		this.beanFactory = getXmlBeanFactory();
		createAfter();
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
		System.setProperty("scw_global_property_refresh_period", period + "");
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
		System.setProperty("scw_global_value_wired_refresh_period", period + "");
	}

	public CommonApplication(String configXml) {
		this(configXml, getGlobalValueWiredRefreshPeriod(), getGlobalPropertyRefreshPeriod());
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

	private XmlBeanFactory getXmlBeanFactory() {
		try {
			return new XmlBeanFactory(this.propertyFactory, getConfigPath(), timer, getValueRefreshPeriod()) {
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

		beanFactory.init();

		String ormScanPackageName = propertyFactory.getProperty("orm.scan");
		if (!StringUtils.isEmpty(ormScanPackageName)) {
			ORMUtils.registerCglibProxyTableBean(ormScanPackageName);
		} else {
			ORMUtils.registerCglibProxyTableBean(getORMPackage());
		}

		CrontabAnnotationUtils.crontabService(ResourceUtils.getClassList(getCrontabAnnotationPackage()), beanFactory,
				getBeanFactory().getFilterNames());
		scanningConsumer();

		if (ResourceUtils.isExist(configPath)) {
			DubboUtils.exportService(beanFactory, propertyFactory, XmlBeanUtils.getRootNodeList(configPath));
		}
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

		DubboUtils.destoryAll();
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
