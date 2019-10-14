package scw.beans.dubbo;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConfigCenterConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.context.ConfigManager;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanFactory;
import scw.beans.annotation.Service;
import scw.beans.xml.XmlBeanUtils;
import scw.core.Destroy;
import scw.core.Init;
import scw.core.PropertyFactory;
import scw.core.reflect.CloneUtils;
import scw.core.reflect.PropertyMapper;
import scw.core.resource.ResourceUtils;
import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;

public final class XmlDubboUtils {
	private XmlDubboUtils() {
	};

	private static List<RegistryConfig> parseRegistryConfig(PropertyFactory propertyFactory, BeanFactory beanFactory,
			Node node) {
		RegistryConfig registryConfig = XMLUtils.newInstanceLoadAttributeBySetter(RegistryConfig.class, propertyFactory,
				node, new DubboConfigPropertyMapper(beanFactory) {
					@Override
					public Object mapper(String name, String value, Type type) throws Exception {
						if ("address".equals(name)) {
							return null;
						}
						return super.mapper(name, value, type);
					}
				});
		List<RegistryConfig> list = new LinkedList<RegistryConfig>();
		String[] addressArray = StringUtils.commonSplit(XmlBeanUtils.getAddress(propertyFactory, node));
		for (String address : addressArray) {
			RegistryConfig config = CloneUtils.copy(registryConfig, RegistryConfig.class);
			config.setAddress(address);
			list.add(config);
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node n = nodeList.item(i);
			if ("registry".equals(n.getNodeName())) {
				list.addAll(parseRegistryConfig(propertyFactory, beanFactory, n));
			}
		}
		return list;
	}

	private static List<ProtocolConfig> parseProtocolConfig(PropertyFactory propertyFactory, BeanFactory beanFactory,
			Node node, final boolean root) {
		ProtocolConfig config = XMLUtils.newInstanceLoadAttributeBySetter(ProtocolConfig.class, propertyFactory, node,
				new DubboConfigPropertyMapper(beanFactory) {
					@Override
					public Object mapper(String name, String value, Type type) throws Exception {
						if (root && "name".equals(name)) {
							return null;
						}
						return super.mapper(name, value, type);
					}
				});
		List<ProtocolConfig> list = new LinkedList<ProtocolConfig>();
		list.add(config);
		NodeList nodeList = node.getChildNodes();
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node n = nodeList.item(i);
			if ("protocol".equals(n.getNodeName())) {
				list.addAll(parseProtocolConfig(propertyFactory, beanFactory, n, false));
			}
		}
		return list;
	}

	private static List<ServiceConfig<?>> parseServiceConfig(PropertyFactory propertyFactory,
			final BeanFactory beanFactory, Node node) {
		ServiceConfig<?> serviceConfig = XMLUtils.newInstanceLoadAttributeBySetter(ServiceConfig.class, propertyFactory,
				node, new DubboConfigPropertyMapper(beanFactory));
		List<ServiceConfig<?>> serviceConfigs = new LinkedList<ServiceConfig<?>>();
		if (serviceConfig.getInterface() != null) {
			serviceConfigs.add(serviceConfig);
		}

		String packageName = XMLUtils.getNodeAttributeValue(propertyFactory, node, "package");
		if (packageName != null) {
			Collection<Class<?>> clazzList = ResourceUtils.getClassList(packageName);
			for (Class<?> clz : clazzList) {
				Service service = clz.getAnnotation(Service.class);
				if (service != null) {
					Object ref = beanFactory.getInstance(clz);
					for (Class<?> i : clz.getInterfaces()) {
						if(i == Init.class || i == Destroy.class){
							continue;
						}
						
						@SuppressWarnings("unchecked")
						ServiceConfig<Object> config = CloneUtils.copy(serviceConfig, ServiceConfig.class);
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
			if ("service".equalsIgnoreCase(n.getNodeName())) {
				serviceConfigs.addAll(parseServiceConfig(propertyFactory, beanFactory, n));
			}
		}
		return serviceConfigs;
	}

	private static List<ReferenceConfig<?>> parseReferenceConfig(PropertyFactory propertyFactory,
			final BeanFactory beanFactory, Node node) {
		ReferenceConfig<?> config = XMLUtils.newInstanceLoadAttributeBySetter(ReferenceConfig.class, propertyFactory,
				node, new DubboConfigPropertyMapper(beanFactory));
		List<ReferenceConfig<?>> referenceConfigs = new LinkedList<ReferenceConfig<?>>();
		if (config.getInterface() != null) {
			referenceConfigs.add(config);
		}

		String packageName = XMLUtils.getNodeAttributeValue(propertyFactory, node, "package");
		if (packageName != null) {
			for (Class<?> clz : ResourceUtils.getClassList(packageName)) {
				if (clz.isInterface()) {
					ReferenceConfig<?> referenceConfig = CloneUtils.copy(config, ReferenceConfig.class);
					referenceConfig.setInterface(clz);
					referenceConfigs.add(referenceConfig);
				}
			}
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node n = nodeList.item(i);
			if ("reference".equals(n.getNodeName())) {
				referenceConfigs.addAll(parseReferenceConfig(propertyFactory, beanFactory, n));
			}
		}
		return referenceConfigs;
	}

	public static List<ServiceConfig<?>> getServiceConfigList(PropertyFactory propertyFactory, BeanFactory beanFactory,
			Node node) {
		List<ProtocolConfig> protocolConfigs = parseProtocolConfig(propertyFactory, beanFactory, node, true);
		List<ServiceConfig<?>> serviceConfigs = parseServiceConfig(propertyFactory, beanFactory, node);
		List<RegistryConfig> registryConfigs = parseRegistryConfig(propertyFactory, beanFactory, node);
		for (ServiceConfig<?> serviceConfig : serviceConfigs) {
			serviceConfig.setProtocols(protocolConfigs);
			serviceConfig.setRegistries(registryConfigs);
		}
		return serviceConfigs;
	}

	public static List<ReferenceConfig<?>> getReferenceConfigList(PropertyFactory propertyFactory,
			BeanFactory beanFactory, Node node) {
		List<RegistryConfig> registryConfigs = parseRegistryConfig(propertyFactory, beanFactory, node);
		List<ReferenceConfig<?>> referenceConfigs = XmlDubboUtils.parseReferenceConfig(propertyFactory, beanFactory,
				node);
		for (ReferenceConfig<?> referenceConfig : referenceConfigs) {
			referenceConfig.setRegistries(registryConfigs);
		}
		return referenceConfigs;
	}

	/**
	 * 在dubbo2.7.0之后只能被注册一次
	 * 
	 * @param propertyFactory
	 * @param beanFactory
	 * @param nodeList
	 */
	public static void initConfig(PropertyFactory propertyFactory, final BeanFactory beanFactory, NodeList nodeList) {
		if (nodeList == null) {
			return;
		}

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node == null) {
				return;
			}

			if (DubboUtils.isApplicationNode(node)) {
				Optional<ApplicationConfig> optional = ConfigManager.getInstance().getApplication();
				if (!optional.isPresent()) {
					ApplicationConfig config = XMLUtils.newInstanceLoadAttributeBySetter(ApplicationConfig.class,
							propertyFactory, node, new DubboConfigPropertyMapper(beanFactory));
					ConfigManager.getInstance().setApplication(config);
				}
			} else if (DubboUtils.isConfigCenterNode(node)) {
				Optional<ConfigCenterConfig> optional = ConfigManager.getInstance().getConfigCenter();
				if (!optional.isPresent()) {
					ConfigCenterConfig configCenterConfig = XMLUtils.newInstanceLoadAttributeBySetter(
							ConfigCenterConfig.class, propertyFactory, node,
							new DubboConfigPropertyMapper(beanFactory));
					ConfigManager.getInstance().setConfigCenter(configCenterConfig);
				}
			}
		}
	}

	private static class DubboConfigPropertyMapper implements PropertyMapper<String> {
		private BeanFactory beanFactory;

		public DubboConfigPropertyMapper(BeanFactory beanFactory) {
			this.beanFactory = beanFactory;
		}

		public Object mapper(java.lang.String name, String value, Type type) throws Exception {
			if (StringUtils.isEmpty(value)) {
				return null;
			}

			if (type.toString().startsWith("org.apache.dubbo.config.") || "registry".equalsIgnoreCase(name)
					|| "registries".equalsIgnoreCase(name) || "ref".equalsIgnoreCase(name)) {
				return beanFactory.getInstance(value);
			}

			return StringParse.defaultParse(value, type);
		}

	}
}
