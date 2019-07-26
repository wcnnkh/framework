package scw.application;

import java.io.File;
import java.util.Collection;

import com.alibaba.dubbo.config.ProtocolConfig;

import scw.application.consumer.AnnotationConsumerUtils;
import scw.application.consumer.XmlConsumerFactory;
import scw.application.crontab.CrontabAnnotationUtils;
import scw.beans.CommonFilter;
import scw.beans.XmlBeanFactory;
import scw.beans.property.XmlPropertiesFactory;
import scw.beans.rpc.dubbo.DubboUtils;
import scw.beans.xml.XmlBeanUtils;
import scw.core.PropertiesFactory;
import scw.core.utils.ClassUtils;
import scw.core.utils.ConfigUtils;
import scw.core.utils.StringUtils;
import scw.io.FileUtils;
import scw.logger.LoggerFactory;
import scw.sql.orm.ORMUtils;

public class CommonApplication implements Application {
	private final XmlBeanFactory beanFactory;
	private volatile boolean start = false;
	private final PropertiesFactory propertiesFactory;
	private final String configPath;

	public CommonApplication(String configXml, boolean initStatic, PropertiesFactory propertiesFactory) {
		this.configPath = StringUtils.isEmpty(configXml) ? null : ConfigUtils.getFile(configXml).getPath();
		this.propertiesFactory = propertiesFactory == null ? new XmlPropertiesFactory(this.configPath)
				: propertiesFactory;
		try {
			this.beanFactory = new XmlBeanFactory(this.propertiesFactory, this.configPath, initStatic);
			this.beanFactory.addFirstFilters(CommonFilter.class.getName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public CommonApplication(String configXml, boolean initStatic) {
		this.configPath = StringUtils.isEmpty(configXml) ? null : ConfigUtils.getFile(configXml).getPath();
		this.propertiesFactory = new XmlPropertiesFactory(this.configPath);
		try {
			this.beanFactory = new XmlBeanFactory(this.propertiesFactory, this.configPath, initStatic);
			this.beanFactory.addFirstFilters(CommonFilter.class.getName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getConfigPath() {
		return configPath;
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

	public static String getDefaultConfigPath() {
		String defaultFileName = "beans.xml";
		String beans = FileUtils.searchFileName(defaultFileName, ConfigUtils.getClassPath(), true);
		if (StringUtils.isEmpty(beans) && ConfigUtils.getWorkPath() != null) {
			beans = FileUtils.searchFileName(defaultFileName, ConfigUtils.getWorkPath(), true);
		}

		if (StringUtils.isEmpty(beans)) {
			return null;
		}

		File file = new File(beans);
		if (!file.exists()) {
			return null;
		}
		return beans;
	}
}
