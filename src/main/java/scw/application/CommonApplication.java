package scw.application;

import java.util.Collection;

import com.alibaba.dubbo.config.ProtocolConfig;

import scw.application.consumer.AnnotationConsumerUtils;
import scw.application.consumer.XmlConsumerFactory;
import scw.application.crontab.CrontabAnnotationUtils;
import scw.beans.BeanUtils;
import scw.beans.XmlBeanFactory;
import scw.beans.async.AsyncCompleteFilter;
import scw.beans.property.XmlPropertiesFactory;
import scw.beans.rpc.dubbo.DubboUtils;
import scw.beans.tcc.TCCTransactionFilter;
import scw.beans.xml.XmlBeanUtils;
import scw.core.PropertiesFactory;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.logger.LoggerFactory;
import scw.sql.orm.ORMUtils;
import scw.transaction.TransactionFilter;

public class CommonApplication implements Application {
	private final XmlBeanFactory beanFactory;
	private volatile boolean start = false;
	private final PropertiesFactory propertiesFactory;
	private final String configPath;

	public CommonApplication() {
		this(null);
	}

	public String getConfigPath() {
		return configPath;
	}

	public CommonApplication(String configXml, PropertiesFactory propertiesFactory) {
		this.configPath = configXml;
		this.propertiesFactory = propertiesFactory == null ? new XmlPropertiesFactory(this.configPath)
				: propertiesFactory;
		this.beanFactory = getXmlBeanFactory();
		createAfter();
	}

	public CommonApplication(String configXml) {
		this.configPath = configXml;
		this.propertiesFactory = new XmlPropertiesFactory(getConfigPath());
		this.beanFactory = getXmlBeanFactory();
		createAfter();
	}

	private XmlBeanFactory getXmlBeanFactory() {
		try {
			return new XmlBeanFactory(this.propertiesFactory, getConfigPath()) {
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

	public final PropertiesFactory getPropertiesFactory() {
		return beanFactory.getPropertiesFactory();
	}

	protected String getAnnotationPackage() {
		return BeanUtils.getAnnotationPackage(propertiesFactory);
	}

	protected String getORMPackage() {
		String orm = BeanUtils.getORMPackage(propertiesFactory);
		return orm == null ? getAnnotationPackage() : orm;
	}

	protected String getServiceAnnotationPackage() {
		String service = BeanUtils.getServiceAnnotationPackage(propertiesFactory);
		return service == null ? getAnnotationPackage() : service;
	}

	protected String getCrontabAnnotationPackage() {
		String crontab = BeanUtils.getCrontabAnnotationPackage(propertiesFactory);
		return crontab == null ? getAnnotationPackage() : crontab;
	}

	protected String getConsumerAnnotationPackage() {
		String consumer = BeanUtils.getConsumerAnnotationPackage(propertiesFactory);
		return consumer == null ? getAnnotationPackage() : consumer;
	}

	protected String getStaticAnnotationPackage() {
		String init = BeanUtils.getInitStaticPackage(propertiesFactory);
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

		String ormScanPackageName = propertiesFactory.getValue("orm.scan");
		if (!StringUtils.isEmpty(ormScanPackageName)) {
			ORMUtils.registerCglibProxyTableBean(ormScanPackageName);
		}

		ORMUtils.registerCglibProxyTableBean(getORMPackage());

		beanFactory.init();

		if (ResourceUtils.isExist(configPath)) {
			DubboUtils.exportService(beanFactory, propertiesFactory, XmlBeanUtils.getRootNodeList(configPath));
		}

		CrontabAnnotationUtils.crontabService(ResourceUtils.getClassList(getCrontabAnnotationPackage()), beanFactory,
				getBeanFactory().getFilterNames());
		scanningConsumer();
	}

	private void scanningConsumer() {
		Collection<Class<?>> classes = ResourceUtils.getClassList(getConsumerAnnotationPackage());
		AnnotationConsumerUtils.scanningAMQPConsumer(getBeanFactory(), classes, getBeanFactory().getFilterNames());
		AnnotationConsumerUtils.scanningConsumer(beanFactory,
				new XmlConsumerFactory(beanFactory, propertiesFactory, configPath), classes,
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
}
