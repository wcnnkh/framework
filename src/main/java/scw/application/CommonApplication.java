package scw.application;

import java.util.Collection;

import com.alibaba.dubbo.config.ProtocolConfig;

import scw.beans.XmlBeanFactory;
import scw.beans.property.PropertiesFactory;
import scw.beans.property.XmlPropertiesFactory;
import scw.beans.rpc.dubbo.XmlDubboUtils;
import scw.common.Logger;
import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;

public class CommonApplication implements Application {
	private final XmlBeanFactory beanFactory;
	private String packageNames;
	private volatile boolean start = false;
	private final PropertiesFactory propertiesFactory;
	private final String configPath;

	public CommonApplication(String configPath, boolean initStatic, PropertiesFactory propertiesFactory) {
		this.configPath = configPath;
		this.propertiesFactory = propertiesFactory == null ? new XmlPropertiesFactory(configPath) : propertiesFactory;
		try {
			this.beanFactory = new XmlBeanFactory(this.propertiesFactory, configPath, initStatic);
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public CommonApplication(String configXml, boolean initStatic) {
		this(configXml, initStatic, new XmlPropertiesFactory(configXml));
	}

	public Collection<Class<?>> getClasses() {
		return ClassUtils.getClasses(packageNames);
	}

	public XmlBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public PropertiesFactory getPropertiesFactory() {
		return beanFactory.getPropertiesFactory();
	}

	public void init() {
		if (start) {
			throw new ShuChaoWenRuntimeException("已经启动了");
		}

		synchronized (this) {
			if (start) {
				throw new ShuChaoWenRuntimeException("已经启动了");
			}

			start = true;
		}

		beanFactory.init();
		try {
			if (!StringUtils.isNull(configPath)) {
				XmlDubboUtils.register(propertiesFactory, beanFactory, configPath);
			}
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public void destroy() {
		if (!start) {
			throw new ShuChaoWenRuntimeException("还未启动，无法销毁");
		}

		synchronized (this) {
			if (!start) {
				throw new ShuChaoWenRuntimeException("还未启动，无法销毁");
			}

			start = false;
		}

		beanFactory.destroy();
		ProtocolConfig.destroyAll();
		Logger.shutdown();
	}
}
