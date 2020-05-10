package scw.dubbo;

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
import scw.beans.BeanUtils;
import scw.beans.annotation.Service;
import scw.beans.xml.XmlBeanUtils;
import scw.core.annotation.AnnotationUtils;
import scw.core.utils.StringUtils;
import scw.io.ResourceUtils;
import scw.mapper.Copy;
import scw.mapper.Field;
import scw.mapper.MapperUtils;
import scw.util.value.property.PropertyFactory;
import scw.xml.XMLUtils;

public final class XmlDubboUtils {
	private XmlDubboUtils() {
	};

	private static List<RegistryConfig> parseRegistryConfig(PropertyFactory propertyFactory, BeanFactory beanFactory,
			Node node) throws Exception {
		XmlDubboMapping mapper = new XmlDubboMapping(beanFactory, propertyFactory, node) {
			@Override
			protected Object getNodeValue(String name, String value, Class<?> type, Field field,
					Node node) {
				if ("address".equals(name)) {
					return null;
				}
				return super.getNodeValue(name, value, type, field, node);
			}
		};
		
		RegistryConfig registryConfig = MapperUtils.getMapper().mapping(RegistryConfig.class, null, mapper);
		List<RegistryConfig> list = new LinkedList<RegistryConfig>();
		String[] addressArray = StringUtils.commonSplit(XmlBeanUtils.getAddress(propertyFactory, node));
		for (String address : addressArray) {
			RegistryConfig config = Copy.copy(RegistryConfig.class, registryConfig);
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
			Node node, final boolean root) throws Exception {
		XmlDubboMapping mapper = new XmlDubboMapping(beanFactory, propertyFactory, node) {
			@Override
			protected Object getNodeValue(String name, String value, Class<?> type, Field field,
					Node node) {
				if (root && "name".equals(name)) {
					return null;
				}
				return super.getNodeValue(name, value, type, field, node);
			}
		};
		ProtocolConfig config = MapperUtils.getMapper().mapping(ProtocolConfig.class, null, mapper);
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
			final BeanFactory beanFactory, Node node) throws Exception {
		XmlDubboMapping mapper = new XmlDubboMapping(beanFactory, propertyFactory, node);
		ServiceConfig<?> serviceConfig = MapperUtils.getMapper().mapping(ServiceConfig.class, null, mapper);
		List<ServiceConfig<?>> serviceConfigs = new LinkedList<ServiceConfig<?>>();
		if (serviceConfig.getInterface() != null) {
			serviceConfigs.add(serviceConfig);
		}

		String packageName = XMLUtils.getNodeAttributeValue(propertyFactory, node, "package");
		if (packageName != null) {
			Collection<Class<?>> clazzList = ResourceUtils.getPackageScan().getClasses(packageName);
			for (Class<?> clz : clazzList) {
				if (clz.isInterface()) {
					continue;
				}

				Service service = clz.getAnnotation(Service.class);
				if (service != null) {
					Class<?>[] interfaces = BeanUtils.getServiceInterfaces(clz);
					if (scw.core.utils.ArrayUtils.isEmpty(interfaces)) {
						continue;
					}
					Object ref = beanFactory.getInstance(clz);
					for (Class<?> i : interfaces) {
						@SuppressWarnings("unchecked")
						ServiceConfig<Object> config = Copy.copy(ServiceConfig.class, serviceConfig);
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
			final BeanFactory beanFactory, Node node) throws Exception {
		XmlDubboMapping mapper = new XmlDubboMapping(beanFactory, propertyFactory, node);
		ReferenceConfig<?> config = MapperUtils.getMapper().mapping(ReferenceConfig.class, null, mapper);
		List<ReferenceConfig<?>> referenceConfigs = new LinkedList<ReferenceConfig<?>>();
		if (config.getInterface() != null) {
			referenceConfigs.add(config);
		}

		String packageName = XMLUtils.getNodeAttributeValue(propertyFactory, node, "package");
		if (packageName != null) {
			for (Class<?> clz : ResourceUtils.getPackageScan().getClasses(packageName)) {
				if (!clz.isInterface() || AnnotationUtils.isIgnore(clz)) {
					continue;
				}

				ReferenceConfig<?> referenceConfig = Copy.copy(ReferenceConfig.class, config);
				referenceConfig.setInterface(clz);
				referenceConfigs.add(referenceConfig);
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
			Node node) throws Exception {
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
			BeanFactory beanFactory, Node node) throws Exception {
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
	 * @throws Exception 
	 */
	public static void initConfig(PropertyFactory propertyFactory, final BeanFactory beanFactory, NodeList nodeList) throws Exception {
		if (nodeList == null) {
			return;
		}

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node == null) {
				return;
			}

			XmlDubboMapping mapper = new XmlDubboMapping(beanFactory, propertyFactory, node);
			if (DubboUtils.isApplicationNode(node)) {
				Optional<ApplicationConfig> optional = ConfigManager.getInstance().getApplication();
				if (!optional.isPresent()) {
					ApplicationConfig config = MapperUtils.getMapper().mapping(ApplicationConfig.class, null, mapper);
					ConfigManager.getInstance().setApplication(config);
				}
			} else if (DubboUtils.isConfigCenterNode(node)) {
				Optional<ConfigCenterConfig> optional = ConfigManager.getInstance().getConfigCenter();
				if (!optional.isPresent()) {
					ConfigCenterConfig configCenterConfig = MapperUtils.getMapper().mapping(ConfigCenterConfig.class, null, mapper);
					ConfigManager.getInstance().setConfigCenter(configCenterConfig);
				}
			}
		}
	}
}
