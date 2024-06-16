package io.basc.framework.dubbo;

import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.config.AbstractConfig;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ArgumentConfig;
import org.apache.dubbo.config.ConfigCenterConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.MethodConfig;
import org.apache.dubbo.config.MetricsConfig;
import org.apache.dubbo.config.ModuleConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.SslConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.rpc.model.ModuleModel;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.xml.XmlBeanUtils;
import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.env.Environment;
import io.basc.framework.io.Resource;
import io.basc.framework.io.scan.TypeScanner;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.mapper.stereotype.Copy;
import io.basc.framework.mapper.stereotype.FieldDescriptor;
import io.basc.framework.mapper.stereotype.ObjectMapping;
import io.basc.framework.util.StringUtils;

public class DubboRegistry {
	private static interface ConfigFilter<T> {
		boolean doFilter(List<T> list, Node node, T config);
	}

	private static final String DEFAULT_PROTOCOL_NAME = "dubbo";

	private static final String DUBBO_SCAN_PACKAGE = "package";

	private static final String DUBBO_SERVICE_REF = "ref";

	private static Logger logger = LoggerFactory.getLogger(DubboRegistry.class);

	private static final String TAG_NAME_PREFIX = "dubbo:";

	private static String getPackageName(Environment environment, Node node) {
		return DomUtils.getNodeAttributeValue(environment, node, DUBBO_SCAN_PACKAGE).getAsString();
	}

	private static <T> void loader(Object instance, Environment environment, Node node) {
		NamedNodeMap namedNodeMap = node.getAttributes();
		ObjectMapping fields = ObjectMapping.getFields(instance.getClass()).all().filter(FieldFeature.SETTER);
		for (int i = 0, len = namedNodeMap.getLength(); i < len; i++) {
			Node n = namedNodeMap.item(i);
			String name = n.getNodeName();
			if (name.equals(DUBBO_SERVICE_REF) || name.equals(DUBBO_SCAN_PACKAGE)) {
				continue;
			}

			String value = n.getNodeValue();
			if (StringUtils.isEmpty(value)) {
				continue;
			}

			value = environment.getProperties().replacePlaceholders(value);
			FieldDescriptor field = fields.getBySetterName(name, null);
			if (field == null) {
				logger.warn("{} ignore attribute name={}, value={}", instance.getClass(), name, value);
				continue;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("{} set name={}, value={}", instance.getClass(), name, value);
			}
			MapperUtils.setValue(environment.getConversionService(), instance, field, value);
		}
	}

	public static List<ApplicationConfig> parseApplicationConfigList(final Environment environment, NodeList nodeList,
			@Nullable ApplicationConfig defaultConfig) {
		return parseConfigList(ApplicationConfig.class, environment, nodeList, defaultConfig,
				new ConfigFilter<ApplicationConfig>() {
					@Override
					public boolean doFilter(List<ApplicationConfig> list, Node node, ApplicationConfig config) {
						List<RegistryConfig> registryConfigs = parseRegistryConfigList(environment,
								node.getChildNodes(), null);
						if (!registryConfigs.isEmpty()) {
							config.setRegistries(registryConfigs);
						}

						return config.isValid();
					}
				});
	}

	private static List<ArgumentConfig> parseArgumentConfigList(Environment environment, NodeList nodeList) {
		return parseConfigList(ArgumentConfig.class, environment, nodeList, null);
	}

	public static List<ConfigCenterConfig> parseConfigCenterConfigs(Environment environment, NodeList nodeList) {
		return parseConfigList(ConfigCenterConfig.class, environment, nodeList, null);
	}

	private static <T> List<T> parseConfigList(Class<? extends T> type, Environment environment, NodeList nodeList,
			@Nullable T defaultConfig) {
		return parseConfigList(type, environment, nodeList, defaultConfig, null);
	}

	private static <T> List<T> parseConfigList(Class<? extends T> type, Environment environment, NodeList nodeList,
			@Nullable T defaultConfig, ConfigFilter<T> filter) {
		List<T> list = new ArrayList<T>(4);
		if (nodeList != null) {
			String tagName = TAG_NAME_PREFIX + AbstractConfig.getTagName(type);
			for (int i = 0, len = nodeList.getLength(); i < len; i++) {
				Node node = nodeList.item(i);
				if (!node.getNodeName().equalsIgnoreCase(tagName)) {
					continue;
				}

				T config = defaultConfig == null ? ReflectionApi.newInstance(type) : Copy.copy(defaultConfig, type);
				loader(config, environment, node);

				if (filter != null && !filter.doFilter(list, node, config)) {
					continue;
				}

				if (config instanceof AbstractConfig) {
					if (((AbstractConfig) config).isValid()) {
						list.add(config);
					} else {
						logger.error(config.toString());
					}
				} else {
					list.add(config);
				}

				list.addAll(parseConfigList(type, environment, node.getChildNodes(), config, filter));
			}
		}
		return list;
	}

	public static List<ConsumerConfig> parseConsumerConfigList(Environment environment, NodeList nodeList,
			ConsumerConfig defaultConfig) {
		return parseConfigList(ConsumerConfig.class, environment, nodeList, defaultConfig);
	}

	public static List<MetadataReportConfig> parseMetadataReportConfigList(Environment environment, NodeList nodeList,
			@Nullable MetadataReportConfig defaultConfig) {
		return parseConfigList(MetadataReportConfig.class, environment, nodeList, defaultConfig);
	}

	private static List<MethodConfig> parseMethodConfigList(final Environment environment, NodeList nodeList) {
		return parseConfigList(MethodConfig.class, environment, nodeList, null, new ConfigFilter<MethodConfig>() {
			@Override
			public boolean doFilter(List<MethodConfig> list, Node node, MethodConfig config) {
				if (config.isValid()) {
					List<ArgumentConfig> argumentConfigs = parseArgumentConfigList(environment, node.getChildNodes());
					if (!argumentConfigs.isEmpty()) {
						config.setArguments(argumentConfigs);
					}
					return true;
				}
				return false;
			}
		});
	}

	public static List<MetricsConfig> parseMetricsConfigList(Environment environment, NodeList nodeList) {
		return parseConfigList(MetricsConfig.class, environment, nodeList, null);
	}

	public static List<ModuleConfig> parseModuleConfigList(Environment environment, NodeList nodeList) {
		return parseConfigList(ModuleConfig.class, environment, nodeList, null);
	}

	public static List<MonitorConfig> parseMonitorConfigList(Environment environment, NodeList nodeList) {
		return parseConfigList(MonitorConfig.class, environment, nodeList, null);
	}

	public static List<ProtocolConfig> parseProtocolConfigList(Environment environment, NodeList nodeList,
			ProtocolConfig defaultConfig) {
		return parseConfigList(ProtocolConfig.class, environment, nodeList, defaultConfig,
				new ConfigFilter<ProtocolConfig>() {
					@Override
					public boolean doFilter(List<ProtocolConfig> list, Node node, ProtocolConfig config) {
						if (StringUtils.isEmpty(config.getName())) {
							config.setName(DEFAULT_PROTOCOL_NAME);
						}
						return config.isValid();
					}
				});
	}

	public static List<ProviderConfig> parseProviderConfigList(Environment environment, NodeList nodeList,
			ProviderConfig defaultConfig) {
		return parseConfigList(ProviderConfig.class, environment, nodeList, defaultConfig);
	}

	@SuppressWarnings("rawtypes")
	public static List<ReferenceConfig> parseReferenceConfigList(final Environment environment, NodeList nodeList,
			ReferenceConfig<?> defaultConfig, final TypeScanner typeScanner) {
		return parseConfigList(ReferenceConfig.class, environment, nodeList, defaultConfig,
				new ConfigFilter<ReferenceConfig>() {
					@Override
					public boolean doFilter(List<ReferenceConfig> list, Node node, ReferenceConfig config) {
						String packageName = getPackageName(environment, node);
						if (StringUtils.isNotEmpty(packageName)) {
							for (MetadataReader metadataReader : typeScanner.scan(Package.getPackage(packageName), null, (e, m) -> e.getClassMetadata().isInterface())) {
								ReferenceConfig<?> referenceConfig = Copy.copy(config, ReferenceConfig.class);
								referenceConfig.setInterface(metadataReader.getClassMetadata().getClassName());
								if (referenceConfig.isValid()) {
									list.add(referenceConfig);
								}
							}
						}

						List<RegistryConfig> registryConfigs = parseRegistryConfigList(environment,
								node.getChildNodes(), null);
						if (!registryConfigs.isEmpty()) {
							config.setRegistries(registryConfigs);
						}

						if (config.isValid() && config.getInterfaceClass() != null) {
							List<MethodConfig> methodConfigs = parseMethodConfigList(environment, node.getChildNodes());
							if (!methodConfigs.isEmpty()) {
								config.setMethods(methodConfigs);
							}
							return true;
						}
						return false;
					}
				});
	}

	public static List<RegistryConfig> parseRegistryConfigList(Environment environment, NodeList nodeList,
			@Nullable RegistryConfig defaultConfig) {
		return parseConfigList(RegistryConfig.class, environment, nodeList, defaultConfig);
	}

	@SuppressWarnings("rawtypes")
	public static List<ServiceConfig> parseServiceConfigList(final ApplicationContext context, NodeList nodeList,
			ServiceConfig<?> defaultConfig) {
		return parseConfigList(ServiceConfig.class, context, nodeList, defaultConfig,
				new ConfigFilter<ServiceConfig>() {

					@SuppressWarnings("unchecked")
					@Override
					public boolean doFilter(List<ServiceConfig> list, Node node, ServiceConfig config) {
						String ref = DomUtils.getNodeAttributeValue(context, node, "ref").getAsString();
						if (StringUtils.isNotEmpty(ref) && context.isInstance(ref)) {
							config.setRef(context.getInstance(ref));
						}

						String packageName = getPackageName(context, node);
						if (StringUtils.isNotEmpty(packageName)) {
							for (Class<?> clazz : context.getClassScanner().scan(packageName,
									(e, m) -> e.getAnnotationMetadata().isInterface())) {
								if (!context.isInstance(clazz)) {
									logger.warn("{} not supported get instance", clazz);
									continue;
								}

								Object refInstance = context.getInstance(clazz);
								ServiceConfig<Object> scanService = Copy.copy(config, ServiceConfig.class);
								scanService.setInterface(clazz);
								scanService.setRef(refInstance);
								if (scanService.isValid()) {
									list.add(scanService);
								}
							}
						}

						List<RegistryConfig> registryConfigs = parseRegistryConfigList(context, node.getChildNodes(),
								null);
						if (!registryConfigs.isEmpty()) {
							config.setRegistries(registryConfigs);
						}

						List<ProtocolConfig> protocolConfigs = parseProtocolConfigList(context, node.getChildNodes(),
								null);
						if (!protocolConfigs.isEmpty()) {
							config.setProtocols(protocolConfigs);
						}

						if (config.isValid() && config.getRef() != null
								&& StringUtils.isNotEmpty(config.getInterface())) {
							List<MethodConfig> methodConfigs = parseMethodConfigList(context, node.getChildNodes());
							if (!methodConfigs.isEmpty()) {
								config.setMethods(methodConfigs);
							}
							return true;
						}

						return false;
					}
				});
	}

	public static List<SslConfig> parseSslConfigList(Environment environment, NodeList nodeList) {
		return parseConfigList(SslConfig.class, environment, nodeList, null);
	}

	private List<ApplicationConfig> applicationConfigs;
	private List<ConfigCenterConfig> configCenterConfigs;
	private List<ConsumerConfig> consumerConfigs;
	private List<MetadataReportConfig> metadataReportConfigs;
	private List<MetricsConfig> metricsConfigs;
	private List<ModuleConfig> moduleConfigs;
	private List<ModuleModel> moduleModels;
	private List<MonitorConfig> monitorConfigs;
	private List<ProtocolConfig> protocolConfigs;
	private List<ProviderConfig> providerConfigs;
	private List<RegistryConfig> registryConfigs;
	private List<SslConfig> sslConfigs;

	public List<ConsumerConfig> getConsumerConfigs() {
		return consumerConfigs;
	}

	public DubboBootstrap getDubboBootstrap() {
		DubboBootstrap bootstrap = DubboBootstrap.getInstance();
		if (applicationConfigs != null) {
			for (ApplicationConfig applicationConfig : applicationConfigs) {
				bootstrap = bootstrap.application(applicationConfig);
			}
		}

		if (configCenterConfigs != null) {
			bootstrap = bootstrap.configCenters(configCenterConfigs);
		}

		if (registryConfigs != null) {
			bootstrap = bootstrap.registries(registryConfigs);
		}

		if (protocolConfigs != null) {
			bootstrap = bootstrap.protocols(protocolConfigs);
		}

		if (metadataReportConfigs != null) {
			bootstrap = bootstrap.metadataReports(metadataReportConfigs);
		}

		if (this.sslConfigs != null) {
			for (SslConfig sslConfig : sslConfigs) {
				bootstrap = bootstrap.ssl(sslConfig);
			}
		}

		if (this.metricsConfigs != null) {
			for (MetricsConfig metricsConfig : metricsConfigs) {
				bootstrap = bootstrap.metrics(metricsConfig);
			}
		}

		if (this.moduleConfigs != null) {
			for (ModuleConfig moduleConfig : moduleConfigs) {
				if (this.moduleModels == null) {
					bootstrap = bootstrap.module(moduleConfig);
				} else {
					for (ModuleModel moduleModel : moduleModels) {
						bootstrap = bootstrap.module(moduleConfig, moduleModel);
					}
				}
			}
		}

		List<ConsumerConfig> consumerConfigs = getConsumerConfigs();
		if (consumerConfigs != null) {
			bootstrap = bootstrap.consumers(consumerConfigs);
		}

		List<ProviderConfig> providerConfigs = getProviderConfigs();
		if (protocolConfigs != null) {
			bootstrap = bootstrap.providers(providerConfigs);
		}
		return bootstrap;
	}

	public List<MetadataReportConfig> getMetadataReportConfigs() {
		return metadataReportConfigs;
	}

	public List<MonitorConfig> getMonitorConfigs() {
		return monitorConfigs;
	}

	public List<ProtocolConfig> getProtocolConfigs() {
		return protocolConfigs;
	}

	public List<ProviderConfig> getProviderConfigs() {
		return providerConfigs;
	}

	public List<RegistryConfig> getRegistryConfigs() {
		return registryConfigs;
	}

	public void loadXml(ApplicationContext context) {
		for (Resource resource : context.getResources().getServices()) {
			if (resource.exists() && resource.getName().endsWith(".xml")) {
				loadXml(resource, context);
			}
		}
	}

	public void loadXml(Resource resource, ApplicationContext context) {
		List<ApplicationConfig> applicationConfigs = XmlBeanUtils.parse(context.getResourceLoader(), resource,
				(nodeList) -> parseApplicationConfigList(context, nodeList, null));
		if (applicationConfigs != null) {
			if (this.applicationConfigs == null) {
				this.applicationConfigs = new ArrayList<>();
			}
			this.applicationConfigs.addAll(applicationConfigs);
		}

		List<MetadataReportConfig> metadataReportConfigs = XmlBeanUtils.parse(context.getResourceLoader(), resource,
				(nodeList) -> parseMetadataReportConfigList(context, nodeList, null));
		if (metadataReportConfigs != null) {
			if (this.metadataReportConfigs == null) {
				this.metadataReportConfigs = new ArrayList<>();
			}
			this.metadataReportConfigs.addAll(metadataReportConfigs);
		}

		List<RegistryConfig> registryConfigs = XmlBeanUtils.parse(context.getResourceLoader(), resource,
				(nodeList) -> parseRegistryConfigList(context, nodeList, null));
		if (registryConfigs != null) {
			if (this.registryConfigs == null) {
				this.registryConfigs = new ArrayList<>();
			}
			this.registryConfigs.addAll(registryConfigs);
		}

		List<SslConfig> sslConfigs = XmlBeanUtils.parse(context.getResourceLoader(), resource,
				(nodeList) -> parseSslConfigList(context, nodeList));
		if (sslConfigs != null) {
			if (this.sslConfigs == null) {
				this.sslConfigs = new ArrayList<>();
			}
			this.sslConfigs.addAll(sslConfigs);
		}

		List<MetricsConfig> metricsConfigs = XmlBeanUtils.parse(context.getResourceLoader(), resource,
				(nodeList) -> parseMetricsConfigList(context, nodeList));
		if (metricsConfigs != null) {
			if (this.metricsConfigs == null) {
				this.metricsConfigs = new ArrayList<>();
			}
			this.metricsConfigs.addAll(metricsConfigs);
		}

		List<ModuleConfig> moduleConfigs = XmlBeanUtils.parse(context.getResourceLoader(), resource,
				(nodeList) -> parseModuleConfigList(context, nodeList));
		if (moduleConfigs != null) {
			if (this.moduleConfigs == null) {
				this.moduleConfigs = new ArrayList<>();
			}
			this.moduleConfigs.addAll(moduleConfigs);
		}

		List<MonitorConfig> monitorConfigs = XmlBeanUtils.parse(context.getResourceLoader(), resource,
				(nodeList) -> parseMonitorConfigList(context, nodeList));
		if (monitorConfigs != null) {
			if (this.monitorConfigs == null) {
				this.monitorConfigs = new ArrayList<>();
			}
			this.monitorConfigs.addAll(monitorConfigs);
		}

		List<ConfigCenterConfig> configCenterConfigs = XmlBeanUtils.parse(context.getResourceLoader(), resource,
				(nodeList) -> parseConfigCenterConfigs(context, nodeList));
		if (configCenterConfigs != null) {
			if (this.configCenterConfigs == null) {
				this.configCenterConfigs = new ArrayList<>();
			}
			this.configCenterConfigs.addAll(configCenterConfigs);
		}

		List<ConsumerConfig> consumerConfigs = XmlBeanUtils.parse(context.getResourceLoader(), resource,
				(nodeList) -> parseConsumerConfigList(context, nodeList, null));
		if (consumerConfigs != null) {
			if (this.consumerConfigs == null) {
				this.consumerConfigs = new ArrayList<>();
			}
			this.consumerConfigs.addAll(consumerConfigs);
		}

		List<ProtocolConfig> protocolConfigs = XmlBeanUtils.parse(context.getResourceLoader(), resource,
				(nodeList) -> parseProtocolConfigList(context, nodeList, null));
		if (protocolConfigs != null) {
			if (this.protocolConfigs == null) {
				this.protocolConfigs = new ArrayList<>();
			}
			this.protocolConfigs.addAll(protocolConfigs);
		}

		List<ProviderConfig> providerConfigs = XmlBeanUtils.parse(context.getResourceLoader(), resource,
				(nodeList) -> parseProviderConfigList(context, nodeList, null));
		if (providerConfigs != null) {
			if (this.providerConfigs == null) {
				this.providerConfigs = new ArrayList<>();
			}
			this.providerConfigs.addAll(providerConfigs);
		}
	}

	public void setConsumerConfigs(List<ConsumerConfig> consumerConfigs) {
		this.consumerConfigs = consumerConfigs;
	}

	public void setMetadataReportConfigs(List<MetadataReportConfig> metadataReportConfigs) {
		this.metadataReportConfigs = metadataReportConfigs;
	}

	public void setMonitorConfigs(List<MonitorConfig> monitorConfigs) {
		this.monitorConfigs = monitorConfigs;
	}

	public void setProtocolConfigs(List<ProtocolConfig> protocolConfigs) {
		this.protocolConfigs = protocolConfigs;
	}

	public void setProviderConfigs(List<ProviderConfig> providerConfigs) {
		this.providerConfigs = providerConfigs;
	}

	public void setRegistryConfigs(List<RegistryConfig> registryConfigs) {
		this.registryConfigs = registryConfigs;
	}
}
