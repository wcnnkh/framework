package scw.beans.rpc.dubbo;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

import scw.beans.BeanFactory;
import scw.beans.annotation.Service;
import scw.beans.property.PropertiesFactory;
import scw.beans.xml.XmlBeanUtils;
import scw.common.exception.BeansException;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;
import scw.common.utils.XMLUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public final class XmlDubboUtils {
	private static Logger logger = LoggerFactory.getLogger(XmlDubboUtils.class);

	private XmlDubboUtils() {
	};

	private static void register(PropertiesFactory propertiesFactory, BeanFactory beanFactory,
			ApplicationConfig application, List<RegistryConfig> registryConfigs, List<ProtocolConfig> protocolConfigs,
			String version, Node serviceNode) throws ClassNotFoundException {
		String serviceClassName = XmlBeanUtils.getNodeAttributeValue(propertiesFactory, serviceNode, "service");
		if (StringUtils.isNull(serviceClassName)) {
			serviceClassName = serviceNode.getNodeValue();
		}

		int timeout = XmlBeanUtils.getIntegerValue(propertiesFactory, serviceNode, "timeout", -1);

		if (StringUtils.isNull(serviceClassName)) {
			throw new BeansException("not found dubbo service");
		}

		String v = XmlBeanUtils.getVersion(propertiesFactory, serviceNode);
		if (StringUtils.isNull(v)) {
			v = version;
		}

		Class<?> clz = Class.forName(serviceClassName);
		Object ref = beanFactory.get(serviceClassName);
		Class<?>[] interfaces = clz.getInterfaces();
		for (Class<?> i : interfaces) {
			ServiceConfig<Object> serviceConfig = new ServiceConfig<Object>();
			serviceConfig.setApplication(application);
			serviceConfig.setRegistries(registryConfigs);
			serviceConfig.setProtocols(protocolConfigs);
			serviceConfig.setInterface(i);
			serviceConfig.setRef(ref);
			serviceConfig.setVersion(v);

			if (timeout > 0) {
				serviceConfig.setTimeout(timeout);
			}
			// 暴露及注册服务
			serviceConfig.export();
		}
	}

	public static void register(PropertiesFactory propertiesFactory, BeanFactory beanFactory, String config)
			throws ClassNotFoundException {
		NodeList rootNodeList = XmlBeanUtils.getRootNode(config).getChildNodes();
		if (rootNodeList != null) {
			for (int x = 0; x < rootNodeList.getLength(); x++) {
				Node node = rootNodeList.item(x);
				if (node == null) {
					continue;
				}

				if ("dubbo:service".equals(node.getNodeName())) {
					XMLUtils.requireAttribute(node, "port", "address");
					String name = XmlBeanUtils.getNodeAttributeValue(propertiesFactory, node, "name");
					int port = Integer.parseInt(XmlBeanUtils.getNodeAttributeValue(propertiesFactory, node, "port"));
					logger.info("开始注册dubbo服务,name=" + name + ",port=" + port);
					int threads = XmlBeanUtils.getIntegerValue(propertiesFactory, node, "treads", 200);
					int timeout = XmlBeanUtils.getIntegerValue(propertiesFactory, node, "timeout", -1);

					ApplicationConfig application = new ApplicationConfig(name);
					List<RegistryConfig> registryConfigs = new ArrayList<RegistryConfig>();
					String[] addressArray = StringUtils.commonSplit(XmlBeanUtils.getAddress(propertiesFactory, node));
					for (String address : addressArray) {
						RegistryConfig registryConfig = new RegistryConfig();
						registryConfig.setAddress(address);
						registryConfigs.add(registryConfig);
						if (timeout > 0) {
							registryConfig.setTimeout(timeout);
						}
					}

					List<ProtocolConfig> protocolConfigs = new ArrayList<ProtocolConfig>();
					ProtocolConfig protocolConfig = new ProtocolConfig();
					protocolConfig.setName("dubbo");
					protocolConfig.setPort(port);
					protocolConfig.setThreads(threads);
					protocolConfigs.add(protocolConfig);

					String packageNames = XmlBeanUtils.getPackageName(propertiesFactory, node);
					String version = XmlBeanUtils.getVersion(propertiesFactory, node);
					for (Class<?> clz : ClassUtils.getClasses(packageNames)) {
						Service service = clz.getAnnotation(Service.class);
						if (service != null) {
							Class<?>[] interfaces = clz.getInterfaces();
							Object ref = beanFactory.get(clz);
							for (Class<?> i : interfaces) {
								ServiceConfig<Object> serviceConfig = new ServiceConfig<Object>();
								serviceConfig.setApplication(application);
								serviceConfig.setRegistries(registryConfigs);
								serviceConfig.setProtocols(protocolConfigs);
								serviceConfig.setInterface(i);
								serviceConfig.setRef(ref);
								serviceConfig.setVersion(version);
								// 暴露及注册服务
								serviceConfig.export();
							}
						}
					}

					NodeList nodeList = node.getChildNodes();
					if (nodeList != null) {
						for (int a = 0; a < nodeList.getLength(); a++) {
							Node n = nodeList.item(a);
							if (n == null) {
								continue;
							}
							register(propertiesFactory, beanFactory, application, registryConfigs, protocolConfigs,
									version, n);
						}
					}
					logger.info("dubbo服务注册完成,name=" + name + ",port=" + port);
				}
			}
		}
	}
}
