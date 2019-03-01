package scw.beans.rpc.dubbo;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Node;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

import scw.beans.Bean;
import scw.beans.BeanFactory;
import scw.beans.property.PropertiesFactory;
import scw.beans.xml.XmlBeanUtils;
import scw.common.exception.BeansException;
import scw.common.exception.NotSupportException;
import scw.common.utils.StringUtils;

public class XmlDubboBean implements Bean {
	private final String[] names;
	private final String id;
	private final String version;
	private final boolean check;
	private final boolean singleton;
	private final Class<?> type;
	private final ApplicationConfig application;
	private final List<RegistryConfig> registryConfigs;
	private final int timeout;

	public XmlDubboBean(BeanFactory beanFactory, ApplicationConfig applicationConfig,
			List<RegistryConfig> registryConfigs, String version, Class<?> interfaceClass, boolean singleton,
			boolean check) {
		this.application = applicationConfig;
		this.registryConfigs = registryConfigs;
		this.id = interfaceClass.getName();
		this.version = version;
		this.check = check;
		this.singleton = singleton;
		this.type = interfaceClass;
		this.timeout = -1;
		this.names = null;
	}

	public XmlDubboBean(BeanFactory beanFactory, PropertiesFactory propertiesFactory,
			ApplicationConfig applicationConfig, List<RegistryConfig> registryConfigs, Node node)
			throws ClassNotFoundException {
		this.application = applicationConfig;
		this.registryConfigs = registryConfigs;
		this.names = XmlBeanUtils.getNames(node);
		this.check = XmlBeanUtils.getBooleanValue(propertiesFactory, node, "check", false);
		this.singleton = XmlBeanUtils.isSingleton(node);

		String id = XmlBeanUtils.getNodeAttributeValue(propertiesFactory, node, "id");
		String interfaceName = XmlBeanUtils.getNodeAttributeValue(propertiesFactory, node, "interface");
		if (StringUtils.isNull(interfaceName)) {
			interfaceName = node.getNodeValue();
		}

		if (StringUtils.isNull(interfaceName)) {
			throw new BeansException("not found interface");
		}

		this.type = Class.forName(interfaceName);

		if (StringUtils.isNull(id)) {
			this.id = interfaceName;
		} else {
			this.id = id;
		}

		this.version = XmlBeanUtils.getNodeAttributeValue(propertiesFactory, node, "version");
		this.timeout = XmlBeanUtils.getIntegerValue(propertiesFactory, node, "timeout", -1);
	}

	public String getId() {
		return id;
	}

	public String[] getNames() {
		return names;
	}

	public Class<?> getType() {
		return type;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public boolean isProxy() {
		return true;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		ReferenceConfig<T> referenceConfig = new ReferenceConfig<T>();
		referenceConfig.setApplication(application);
		referenceConfig.setRegistries(registryConfigs);
		referenceConfig.setInterface(type);
		referenceConfig.setVersion(version);
		referenceConfig.setCheck(check);

		if (timeout != -1) {
			referenceConfig.setTimeout(timeout);
		}

		return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type },
				new TransactionProxy(type, referenceConfig.get()));
	}

	public <T> T newInstance(Class<?>[] parameterTypes, Object... args) {
		throw new NotSupportException(Arrays.toString(parameterTypes));
	}

	public void autowrite(Object bean) throws Exception {
	}

	public void init(Object bean) throws Exception {
	}

	public void destroy(Object bean) throws Exception {
	}
}
