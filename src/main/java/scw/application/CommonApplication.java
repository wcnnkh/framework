package scw.application;

import java.util.Collection;

import com.alibaba.dubbo.config.ProtocolConfig;

import scw.beans.CommonFilter;
import scw.beans.XmlBeanFactory;
import scw.beans.property.PropertiesFactory;
import scw.beans.property.XmlPropertiesFactory;
import scw.beans.rpc.dubbo.XmlDubboUtils;
import scw.common.exception.NestedRuntimeException;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;
import scw.logger.LoggerFactory;
import scw.sql.orm.ORMUtils;

public class CommonApplication implements Application {
	private static final String ORM_SCAN = "orm.scan";
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
			throw new NestedRuntimeException(e);
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

		String ormScanPackageName = propertiesFactory.getValue(ORM_SCAN);
		if (StringUtils.isEmpty(ormScanPackageName)) {
			ORMUtils.registerCglibProxyTableBean(ormScanPackageName);
		}

		beanFactory.init();

		if (!StringUtils.isNull(configPath)) {
			new Thread(new Runnable() {

				public void run() {
					XmlDubboUtils.serviceExport(propertiesFactory, beanFactory, configPath);
				}
			}).start();
		}
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
