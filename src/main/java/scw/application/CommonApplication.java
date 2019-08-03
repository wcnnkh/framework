package scw.application;

import java.util.Collection;

import scw.application.consumer.AnnotationConsumerUtils;
import scw.application.consumer.XmlConsumerFactory;
import scw.application.crontab.CrontabAnnotationUtils;
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

import com.alibaba.dubbo.config.ProtocolConfig;

public class CommonApplication implements Application{
	private final XmlBeanFactory beanFactory;
	private volatile boolean start = false;
	private final PropertiesFactory propertiesFactory;
	private final String configPath;

	public CommonApplication() {
		this("classpath:benas.xml", false);
	}

	public CommonApplication(String configXml, boolean initStatic, PropertiesFactory propertiesFactory) {
		this.configPath = configXml;
		this.propertiesFactory = propertiesFactory == null ? new XmlPropertiesFactory(this.configPath)
				: propertiesFactory;
		try {
			this.beanFactory = new XmlBeanFactory(this.propertiesFactory, this.configPath, initStatic);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		createAfter();
	}

	public CommonApplication(String configXml, boolean initStatic) {
		this.configPath = configXml;
		this.propertiesFactory = new XmlPropertiesFactory(this.configPath);
		try {
			this.beanFactory = new XmlBeanFactory(this.propertiesFactory, this.configPath, initStatic);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		createAfter();
	}

	private void createAfter() {
		this.beanFactory.addFirstFilters(AsyncCompleteFilter.class.getName());
		this.beanFactory.addFirstFilters(TCCTransactionFilter.class.getName());
		this.beanFactory.addFirstFilters(TransactionFilter.class.getName());
	}

	public final String getConfigPath() {
		return configPath;
	}

	public final Collection<Class<?>> getClasses() {
		return ResourceUtils.getClassList(beanFactory.getPackages());
	}

	public final XmlBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public final PropertiesFactory getPropertiesFactory() {
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

		if(ResourceUtils.isExist(configPath)){
			DubboUtils.exportService(beanFactory, propertiesFactory, XmlBeanUtils.getRootNodeList(configPath));
		}

		CrontabAnnotationUtils.crontabService(getClasses(), beanFactory, getBeanFactory().getFilterNames());
		AnnotationConsumerUtils.scanningAMQPConsumer(getBeanFactory(), getClasses(), getBeanFactory().getFilterNames());
		AnnotationConsumerUtils.scanningConsumer(beanFactory,
				new XmlConsumerFactory(beanFactory, propertiesFactory, configPath), getClasses(),
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
