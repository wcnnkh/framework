package scw.beans.rpc.dubbo;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

import scw.beans.BeanFactory;
import scw.beans.annotation.Service;
import scw.beans.xml.XmlBeanUtils;
import scw.core.PropertiesFactory;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.reflect.PropertyMapper;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;

public final class XmlDubboUtils {
	private XmlDubboUtils() {
	};

	private static List<RegistryConfig> parseRegistryConfig(PropertiesFactory propertiesFactory,
			BeanFactory beanFactory, Node node) {
		RegistryConfig registryConfig = XMLUtils.newInstanceLoadAttributeBySetter(RegistryConfig.class,
				propertiesFactory, node, new PropertyMapper<String>() {

					public Object mapper(String name, String value, Class<?> type) {
						if ("address".equals(name)) {
							return null;
						}

						return StringUtils.conversion(value, type);
					}
				});

		List<RegistryConfig> list = new LinkedList<RegistryConfig>();
		String[] addressArray = StringUtils.commonSplit(XmlBeanUtils.getAddress(propertiesFactory, node));
		for (String address : addressArray) {
			RegistryConfig config = ReflectUtils.clone(registryConfig, true);
			config.setAddress(address);
			list.add(config);
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node n = nodeList.item(i);
			if ("registry".equals(n.getNodeName())) {
				list.addAll(parseRegistryConfig(propertiesFactory, beanFactory, n));
			}
		}
		return list;
	}

	private static List<ProtocolConfig> parseProtocolConfig(PropertiesFactory propertiesFactory,
			BeanFactory beanFactory, Node node, final boolean root) {
		ProtocolConfig config = XMLUtils.newInstanceLoadAttributeBySetter(ProtocolConfig.class, propertiesFactory, node,
				new PropertyMapper<String>() {

					public Object mapper(String name, String value, Class<?> type) {
						if (root && "name".equals(name)) {
							return null;
						}

						return StringUtils.conversion(value, type);
					}
				});
		List<ProtocolConfig> list = new LinkedList<ProtocolConfig>();
		list.add(config);
		NodeList nodeList = node.getChildNodes();
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node n = nodeList.item(i);
			if ("protocol".equals(n.getNodeName())) {
				list.addAll(parseProtocolConfig(propertiesFactory, beanFactory, n, false));
			}
		}
		return list;
	}

	private static ApplicationConfig parseApplicationConfig(PropertiesFactory propertiesFactory,
			final BeanFactory beanFactory, Node node) {
		return XMLUtils.newInstanceLoadAttributeBySetter(ApplicationConfig.class, propertiesFactory, node,
				new PropertyMapper<String>() {

					public Object mapper(String name, String value, Class<?> type) {
						if ("registry".equals(name) || "registries".equals(name)) {
							return beanFactory.get(value);
						}

						return StringUtils.conversion(value, type);
					}
				});
	}

	private static List<ServiceConfig<?>> parseServiceConfig(PropertiesFactory propertiesFactory,
			final BeanFactory beanFactory, Node node) {
		ServiceConfig<?> serviceConfig = XMLUtils.newInstanceLoadAttributeBySetter(ServiceConfig.class,
				propertiesFactory, node, new PropertyMapper<String>() {

					public Object mapper(String name, String value, Class<?> type) {
						if (StringUtils.isEmpty(value)) {
							return null;
						}

						if ("registry".equals(name) || "registries".equals(name) || "ref".equals(name)) {
							return beanFactory.get(value);
						}

						return StringUtils.conversion(value, type);
					}
				});

		List<ServiceConfig<?>> serviceConfigs = new LinkedList<ServiceConfig<?>>();
		if (serviceConfig.getInterface() != null) {
			serviceConfigs.add(serviceConfig);
		}

		String packageName = XMLUtils.getNodeAttributeValue(propertiesFactory, node, "package");
		if (packageName != null) {
			for (Class<?> clz : ClassUtils.getClasses(packageName)) {
				Service service = clz.getAnnotation(Service.class);
				if (service != null) {
					Object ref = beanFactory.get(clz);
					for (Class<?> i : clz.getInterfaces()) {
						@SuppressWarnings("unchecked")
						ServiceConfig<Object> config = (ServiceConfig<Object>) ReflectUtils.clone(serviceConfig, true);
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
				serviceConfigs.addAll(parseServiceConfig(propertiesFactory, beanFactory, n));
			}
		}
		return serviceConfigs;
	}

	/**
	 * 暴露服务
	 * 
	 * @param propertiesFactory
	 * @param beanFactory
	 * @param config
	 */
	public static void serviceExport(PropertiesFactory propertiesFactory, final BeanFactory beanFactory, String config,
			Runnable callback) {
		new ServiceExort(propertiesFactory, beanFactory, config, callback).start();
	}

	private static List<ReferenceConfig<?>> parseReferenceConfig(PropertiesFactory propertiesFactory,
			final BeanFactory beanFactory, Node node) {
		ReferenceConfig<?> config = XMLUtils.newInstanceLoadAttributeBySetter(ReferenceConfig.class, propertiesFactory,
				node, new PropertyMapper<String>() {

					public Object mapper(String name, String value, Class<?> type) {
						if (StringUtils.isEmpty(value)) {
							return null;
						}

						if ("consumer".equals(name) || "methods".equals(name) || "registries".equals(name)
								|| "registry".equals(name)) {
							return beanFactory.get(value);
						}

						return StringUtils.conversion(value, type);
					}
				});

		List<ReferenceConfig<?>> referenceConfigs = new LinkedList<ReferenceConfig<?>>();
		if (config.getInterface() != null) {
			referenceConfigs.add(config);
		}

		String packageName = XMLUtils.getNodeAttributeValue(propertiesFactory, node, "package");
		if (packageName != null) {
			for (Class<?> clz : ClassUtils.getClasses(packageName)) {
				if (clz.isInterface()) {
					ReferenceConfig<?> referenceConfig = ReflectUtils.clone(config, true);
					referenceConfig.setInterface(clz);
					referenceConfigs.add(referenceConfig);
				}
			}
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node n = nodeList.item(i);
			if ("reference".equals(n.getNodeName())) {
				referenceConfigs.addAll(parseReferenceConfig(propertiesFactory, beanFactory, n));
			}
		}
		return referenceConfigs;
	}

	public static List<ServiceConfig<?>> getServiceConfigList(PropertiesFactory propertiesFactory,
			BeanFactory beanFactory, Node node) {
		ApplicationConfig applicationConfig = getApplicationConfig(propertiesFactory, beanFactory, node);

		List<ProtocolConfig> protocolConfigs = parseProtocolConfig(propertiesFactory, beanFactory, node, true);
		List<ServiceConfig<?>> serviceConfigs = parseServiceConfig(propertiesFactory, beanFactory, node);
		for (ServiceConfig<?> serviceConfig : serviceConfigs) {
			serviceConfig.setApplication(applicationConfig);
			serviceConfig.setProtocols(protocolConfigs);
		}
		return serviceConfigs;
	}

	private static ApplicationConfig getApplicationConfig(PropertiesFactory propertiesFactory, BeanFactory beanFactory,
			Node node) {
		ApplicationConfig applicationConfig = parseApplicationConfig(propertiesFactory, beanFactory, node);
		List<RegistryConfig> registryConfigs = parseRegistryConfig(propertiesFactory, beanFactory, node);
		if (applicationConfig.getRegistries() == null) {
			applicationConfig.setRegistries(registryConfigs);
		} else {
			applicationConfig.getRegistries().addAll(registryConfigs);
		}
		return applicationConfig;
	}

	public static List<ReferenceConfig<?>> getReferenceConfigList(PropertiesFactory propertiesFactory,
			BeanFactory beanFactory, Node node) {
		ApplicationConfig applicationConfig = getApplicationConfig(propertiesFactory, beanFactory, node);

		List<ReferenceConfig<?>> referenceConfigs = XmlDubboUtils.parseReferenceConfig(propertiesFactory, beanFactory,
				node);
		for (ReferenceConfig<?> referenceConfig : referenceConfigs) {
			referenceConfig.setApplication(applicationConfig);
		}
		return referenceConfigs;
	}

}

class ServiceExort extends Thread {
	private static Logger logger = LoggerFactory.getLogger(ServiceExort.class);

	private final PropertiesFactory propertiesFactory;
	private final BeanFactory beanFactory;
	private final String config;
	private boolean tag = false;
	private int size = 0;
	private Thread thread;
	private Runnable callback;

	public ServiceExort(PropertiesFactory propertiesFactory, BeanFactory beanFactory, String config,
			Runnable callback) {
		this.propertiesFactory = propertiesFactory;
		this.beanFactory = beanFactory;
		this.config = config;
		this.callback = callback;
	}

	@Override
	public void run() {
		try {
			export();
		} finally {
			tag = true;
			if(callback != null){
				callback.run();
			}
		}

		if (size > 0) {
			logger.trace("-------dubbo服务注册完成-------");
		}
	}

	private void check() {
		if (thread == null) {
			thread = new Thread(new Runnable() {

				public void run() {
					try {
						while (!tag && !Thread.currentThread().isInterrupted()) {
							Thread.sleep(3000L);
							if (tag) {
								break;
							}
							logger.trace("-------正在检查dubbo服务是否注册完成-------");
						}
					} catch (InterruptedException e) {
					}
				}
			});
		} else {
			thread.interrupt();
		}
		thread.start();
	}

	private void export() {
		NodeList rootNodeList = XmlBeanUtils.getRootNode(config).getChildNodes();
		if (rootNodeList != null) {
			for (int x = 0; x < rootNodeList.getLength(); x++) {
				Node node = rootNodeList.item(x);
				if (node == null) {
					continue;
				}

				if ("dubbo:service".equals(node.getNodeName())) {
					if (size == 0) {
						logger.trace("-------开始注册dubbo服务-------");
						check();
					}
					size++;
					List<ServiceConfig<?>> serviceConfigs = XmlDubboUtils.getServiceConfigList(propertiesFactory,
							beanFactory, node);
					for (ServiceConfig<?> serviceConfig : serviceConfigs) {
						size++;
						serviceConfig.export();
					}
				}
			}
		}
	}
}
