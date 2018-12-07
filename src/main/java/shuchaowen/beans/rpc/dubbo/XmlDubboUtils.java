package shuchaowen.beans.rpc.dubbo;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

import shuchaowen.beans.BeanFactory;
import shuchaowen.beans.annotaion.Service;
import shuchaowen.beans.property.PropertiesFactory;
import shuchaowen.beans.xml.XmlBeanUtils;
import shuchaowen.common.exception.BeansException;
import shuchaowen.core.util.StringUtils;
import shuchaowen.reflect.ClassUtils;

public final class XmlDubboUtils {
	private XmlDubboUtils(){};
	
	private static void register(PropertiesFactory propertiesFactory, BeanFactory beanFactory,
			ApplicationConfig application,
			List<RegistryConfig> registryConfigs,
			List<ProtocolConfig> protocolConfigs, String version,
			Node serviceNode) throws ClassNotFoundException {
		String serviceClassName = XmlBeanUtils.getNodeAttributeValue(propertiesFactory,
				serviceNode, "service");
		if (StringUtils.isNull(serviceClassName)) {
			serviceClassName = serviceNode.getNodeValue();
		}

		if (StringUtils.isNull(serviceClassName)) {
			throw new BeansException("not found dubbo service");
		}
		
		String v = XmlBeanUtils.getVersion(propertiesFactory, serviceNode);
		if(StringUtils.isNull(v)){
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
					XmlBeanUtils.requireAttribute(node, "port", "address");
					ApplicationConfig application = new ApplicationConfig(
							XmlBeanUtils.getNodeAttributeValue(propertiesFactory, node, "name"));
					int port = Integer.parseInt(XmlBeanUtils
							.getNodeAttributeValue(propertiesFactory, node, "port"));
					int threads = XmlBeanUtils.getIntegerValue(propertiesFactory, node, "treads",
							200);

					List<RegistryConfig> registryConfigs = new ArrayList<RegistryConfig>();
					String[] addressArray = StringUtils
							.commonSplit(XmlBeanUtils.getAddress(propertiesFactory, node));
					for (String address : addressArray) {
						RegistryConfig registryConfig = new RegistryConfig();
						registryConfig.setAddress(address);
						registryConfigs.add(registryConfig);
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
							register(propertiesFactory, beanFactory, application, registryConfigs,
									protocolConfigs, version, n);
						}
					}
				}
			}
		}
	}
}
