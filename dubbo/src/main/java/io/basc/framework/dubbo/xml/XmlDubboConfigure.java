package io.basc.framework.dubbo.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConfigCenterConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.MetricsConfig;
import org.apache.dubbo.config.ModuleConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.SslConfig;
import org.w3c.dom.NodeList;

import io.basc.framework.context.xml.XmlBeanUtils;
import io.basc.framework.dubbo.DubboConfigure;
import io.basc.framework.env.Environment;
import io.basc.framework.io.Resource;
import io.basc.framework.util.stream.Processor;

public class XmlDubboConfigure implements DubboConfigure {
	private final List<Resource> resources;
	private final Environment environment;

	public XmlDubboConfigure(Environment environment, List<Resource> resources) {
		this.environment = environment;
		this.resources = resources;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public Environment getEnvironment() {
		return environment;
	}

	@Override
	public List<ApplicationConfig> getApplicationConfigList() {
		return parse((nodeList) -> XmlDubboUtils.parseApplicationConfigList(environment, nodeList, null));
	}

	public <T, E extends Throwable> List<T> parse(Processor<NodeList, List<T>, E> processor) throws E {
		List<T> list = new ArrayList<T>();
		for (Resource resource : resources) {
			list.addAll(XmlBeanUtils.parse(environment.getResourceLoader(), resource, processor));
		}
		return list;
	}

	@Override
	public List<MetadataReportConfig> getMetadataReportConfigList() {
		return parse((nodeList) -> XmlDubboUtils.parseMetadataReportConfigList(environment, nodeList, null));
	}

	@Override
	public List<RegistryConfig> getRegistryConfigList() {
		return parse((nodeList) -> XmlDubboUtils.parseRegistryConfigList(environment, nodeList, null));
	}

	@Override
	public List<SslConfig> getSslConfigList() {
		return parse((nodeList) -> XmlDubboUtils.parseSslConfigList(environment, nodeList));
	}

	@Override
	public List<MetricsConfig> getMetricsConfigList() {
		return parse((nodeList) -> XmlDubboUtils.parseMetricsConfigList(environment, nodeList));
	}

	@Override
	public List<ModuleConfig> getModuleConfigList() {
		return parse((nodeList) -> XmlDubboUtils.parseModuleConfigList(environment, nodeList));
	}

	@Override
	public List<MonitorConfig> getMonitorConfigList() {
		return parse((nodeList) -> XmlDubboUtils.parseMonitorConfigList(environment, nodeList));
	}

	@Override
	public List<ConfigCenterConfig> getConfigCenterConfigList() {
		return parse((nodeList) -> XmlDubboUtils.parseConfigCenterConfigs(environment, nodeList));
	}

	@Override
	public List<ConsumerConfig> getConsumerConfigList() {
		return parse((nodeList) -> XmlDubboUtils.parseConsumerConfigList(environment, nodeList, null));
	}

	@Override
	public List<ProtocolConfig> getProtocolConfigList() {
		return parse((nodeList) -> XmlDubboUtils.parseProtocolConfigList(environment, nodeList, null));
	}

	@Override
	public List<ProviderConfig> getProviderConfigList() {
		return parse((nodeList) -> XmlDubboUtils.parseProviderConfigList(environment, nodeList, null));
	}

}
