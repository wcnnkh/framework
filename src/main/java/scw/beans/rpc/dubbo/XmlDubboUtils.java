package scw.beans.rpc.dubbo;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanFactory;
import scw.beans.annotation.Service;
import scw.beans.xml.XmlBeanUtils;
import scw.core.PropertiesFactory;
import scw.core.reflect.PropertyMapper;
import scw.core.utils.ClassUtils;
import scw.core.utils.CloneUtils;
import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

public final class XmlDubboUtils {
	private XmlDubboUtils() {
	};

	private static List<RegistryConfig> parseRegistryConfig(
			PropertiesFactory propertiesFactory, BeanFactory beanFactory,
			Node node) {
		RegistryConfig registryConfig = XMLUtils
				.newInstanceLoadAttributeBySetter(RegistryConfig.class,
						propertiesFactory, node, new PropertyMapper<String>() {

							public Object mapper(String name, String value,
									Class<?> type) {
								if ("address".equals(name)) {
									return null;
								}

								return StringParse.defaultParse(value, type);
							}
						});

		List<RegistryConfig> list = new LinkedList<RegistryConfig>();
		String[] addressArray = StringUtils.commonSplit(XmlBeanUtils
				.getAddress(propertiesFactory, node));
		for (String address : addressArray) {
			RegistryConfig config = CloneUtils.clone(registryConfig, true);
			config.setAddress(address);
			list.add(config);
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node n = nodeList.item(i);
			if ("registry".equals(n.getNodeName())) {
				list.addAll(parseRegistryConfig(propertiesFactory, beanFactory,
						n));
			}
		}
		return list;
	}

	private static List<ProtocolConfig> parseProtocolConfig(
			PropertiesFactory propertiesFactory, BeanFactory beanFactory,
			Node node, final boolean root) {
		ProtocolConfig config = XMLUtils.newInstanceLoadAttributeBySetter(
				ProtocolConfig.class, propertiesFactory, node,
				new PropertyMapper<String>() {

					public Object mapper(String name, String value,
							Class<?> type) {
						if (root && "name".equals(name)) {
							return null;
						}

						return StringParse.defaultParse(value, type);
					}
				});
		List<ProtocolConfig> list = new LinkedList<ProtocolConfig>();
		list.add(config);
		NodeList nodeList = node.getChildNodes();
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node n = nodeList.item(i);
			if ("protocol".equals(n.getNodeName())) {
				list.addAll(parseProtocolConfig(propertiesFactory, beanFactory,
						n, false));
			}
		}
		return list;
	}

	private static ApplicationConfig parseApplicationConfig(
			PropertiesFactory propertiesFactory, final BeanFactory beanFactory,
			Node node) {
		return XMLUtils.newInstanceLoadAttributeBySetter(
				ApplicationConfig.class, propertiesFactory, node,
				new PropertyMapper<String>() {

					public Object mapper(String name, String value,
							Class<?> type) {
						if ("registry".equals(name)
								|| "registries".equals(name)) {
							return beanFactory.getInstance(value);
						}

						return StringParse.defaultParse(value, type);
					}
				});
	}

	private static List<ServiceConfig<?>> parseServiceConfig(
			PropertiesFactory propertiesFactory, final BeanFactory beanFactory,
			Node node) {
		ServiceConfig<?> serviceConfig = XMLUtils
				.newInstanceLoadAttributeBySetter(ServiceConfig.class,
						propertiesFactory, node, new PropertyMapper<String>() {

							public Object mapper(String name, String value,
									Class<?> type) {
								if (StringUtils.isEmpty(value)) {
									return null;
								}

								if ("registry".equals(name)
										|| "registries".equals(name)
										|| "ref".equals(name)) {
									return beanFactory.getInstance(value);
								}

								return StringParse.defaultParse(value, type);
							}
						});

		List<ServiceConfig<?>> serviceConfigs = new LinkedList<ServiceConfig<?>>();
		if (serviceConfig.getInterface() != null) {
			serviceConfigs.add(serviceConfig);
		}

		String packageName = XMLUtils.getNodeAttributeValue(propertiesFactory,
				node, "package");
		if (packageName != null) {
			for (Class<?> clz : ClassUtils.getClasses(packageName)) {
				Service service = clz.getAnnotation(Service.class);
				if (service != null) {
					Object ref = beanFactory.getInstance(clz);
					for (Class<?> i : clz.getInterfaces()) {
						@SuppressWarnings("unchecked")
						ServiceConfig<Object> config = (ServiceConfig<Object>) CloneUtils
								.clone(serviceConfig, true);
						config.setInterface(i);
						config.setRef(ref);
						serviceConfigs.add(config);
					}
				}
			}
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node n = nodeList.item(i);
			if ("service".equals(n.getNodeName())) {
				serviceConfigs.addAll(parseServiceConfig(propertiesFactory,
						beanFactory, n));
			}
		}
		return serviceConfigs;
	}

	private static List<ReferenceConfig<?>> parseReferenceConfig(
			PropertiesFactory propertiesFactory, final BeanFactory beanFactory,
			Node node) {
		ReferenceConfig<?> config = XMLUtils.newInstanceLoadAttributeBySetter(
				ReferenceConfig.class, propertiesFactory, node,
				new PropertyMapper<String>() {

					public Object mapper(String name, String value,
							Class<?> type) {
						if (StringUtils.isEmpty(value)) {
							return null;
						}

						if ("consumer".equals(name) || "methods".equals(name)
								|| "registries".equals(name)
								|| "registry".equals(name)) {
							return beanFactory.getInstance(value);
						}

						return StringParse.defaultParse(value, type);
					}
				});

		List<ReferenceConfig<?>> referenceConfigs = new LinkedList<ReferenceConfig<?>>();
		if (config.getInterface() != null) {
			referenceConfigs.add(config);
		}

		String packageName = XMLUtils.getNodeAttributeValue(propertiesFactory,
				node, "package");
		if (packageName != null) {
			for (Class<?> clz : ClassUtils.getClasses(packageName)) {
				if (clz.isInterface()) {
					ReferenceConfig<?> referenceConfig = CloneUtils.clone(
							config, true);
					referenceConfig.setInterface(clz);
					referenceConfigs.add(referenceConfig);
				}
			}
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node n = nodeList.item(i);
			if ("reference".equals(n.getNodeName())) {
				referenceConfigs.addAll(parseReferenceConfig(propertiesFactory,
						beanFactory, n));
			}
		}
		return referenceConfigs;
	}

	public static List<ServiceConfig<?>> getServiceConfigList(
			PropertiesFactory propertiesFactory, BeanFactory beanFactory,
			Node node) {
		ApplicationConfig applicationConfig = getApplicationConfig(
				propertiesFactory, beanFactory, node);

		List<ProtocolConfig> protocolConfigs = parseProtocolConfig(
				propertiesFactory, beanFactory, node, true);
		List<ServiceConfig<?>> serviceConfigs = parseServiceConfig(
				propertiesFactory, beanFactory, node);
		for (ServiceConfig<?> serviceConfig : serviceConfigs) {
			serviceConfig.setApplication(applicationConfig);
			serviceConfig.setProtocols(protocolConfigs);
		}
		return serviceConfigs;
	}

	private static ApplicationConfig getApplicationConfig(
			PropertiesFactory propertiesFactory, BeanFactory beanFactory,
			Node node) {
		ApplicationConfig applicationConfig = parseApplicationConfig(
				propertiesFactory, beanFactory, node);
		List<RegistryConfig> registryConfigs = parseRegistryConfig(
				propertiesFactory, beanFactory, node);
		if (applicationConfig.getRegistries() == null) {
			applicationConfig.setRegistries(registryConfigs);
		} else {
			applicationConfig.getRegistries().addAll(registryConfigs);
		}
		return applicationConfig;
	}

	public static List<ReferenceConfig<?>> getReferenceConfigList(
			PropertiesFactory propertiesFactory, BeanFactory beanFactory,
			Node node) {
		ApplicationConfig applicationConfig = getApplicationConfig(
				propertiesFactory, beanFactory, node);

		List<ReferenceConfig<?>> referenceConfigs = XmlDubboUtils
				.parseReferenceConfig(propertiesFactory, beanFactory, node);
		for (ReferenceConfig<?> referenceConfig : referenceConfigs) {
			referenceConfig.setApplication(applicationConfig);
		}
		return referenceConfigs;
	}
}
