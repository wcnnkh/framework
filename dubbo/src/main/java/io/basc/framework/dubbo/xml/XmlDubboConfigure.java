package io.basc.framework.dubbo.xml;

import io.basc.framework.beans.xml.XmlBeanUtils;
import io.basc.framework.dubbo.DubboConfigure;
import io.basc.framework.env.Environment;
import io.basc.framework.io.Resource;
import io.basc.framework.util.stream.Processor;

import java.util.Collections;
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

public class XmlDubboConfigure implements DubboConfigure {
	private final Resource resource;
	private final Environment environment;

	public XmlDubboConfigure(Environment environment, Resource resource) {
		this.environment = environment;
		this.resource = resource;
	}

	public Resource getResource() {
		return resource;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public <T> T read(Processor<NodeList, T, Throwable> processor) {
		return XmlBeanUtils.readResourceBeans(environment, resource, processor);
	}

	@Override
	public List<ApplicationConfig> getApplicationConfigList() {
		List<ApplicationConfig> list = read((nodeList) -> XmlDubboUtils
				.parseApplicationConfigList(environment, nodeList, null));
		return list == null ? Collections.emptyList() : list;
	}

	@Override
	public List<MetadataReportConfig> getMetadataReportConfigList() {
		List<MetadataReportConfig> list = read((nodeList) -> XmlDubboUtils
				.parseMetadataReportConfigList(environment, nodeList, null));
		return list == null ? Collections.emptyList() : list;
	}

	@Override
	public List<RegistryConfig> getRegistryConfigList() {
		List<RegistryConfig> list = read((nodeList) -> XmlDubboUtils
				.parseRegistryConfigList(environment, nodeList, null));
		return list == null ? Collections.emptyList() : list;
	}

	@Override
	public List<SslConfig> getSslConfigList() {
		List<SslConfig> list = read((nodeList) -> XmlDubboUtils
				.parseSslConfigList(environment, nodeList));
		return list == null ? Collections.emptyList() : list;
	}

	@Override
	public List<MetricsConfig> getMetricsConfigList() {
		List<MetricsConfig> list = read((nodeList) -> XmlDubboUtils
				.parseMetricsConfigList(environment, nodeList));
		return list == null ? Collections.emptyList() : list;
	}

	@Override
	public List<ModuleConfig> getModuleConfigList() {
		List<ModuleConfig> list = read((nodeList) -> XmlDubboUtils
				.parseModuleConfigList(environment, nodeList));
		return list == null ? Collections.emptyList() : list;
	}

	@Override
	public List<MonitorConfig> getMonitorConfigList() {
		List<MonitorConfig> list = read((nodeList) -> XmlDubboUtils
				.parseMonitorConfigList(environment, nodeList));
		return list == null ? Collections.emptyList() : list;
	}

	@Override
	public List<ConfigCenterConfig> getConfigCenterConfigList() {
		List<ConfigCenterConfig> list = read((nodeList) -> XmlDubboUtils
				.parseConfigCenterConfigs(environment, nodeList));
		return list == null ? Collections.emptyList() : list;
	}

	@Override
	public List<ConsumerConfig> getConsumerConfigList() {
		List<ConsumerConfig> list = read((nodeList) -> XmlDubboUtils
				.parseConsumerConfigList(environment, nodeList, null));
		return list == null ? Collections.emptyList() : list;
	}

	@Override
	public List<ProtocolConfig> getProtocolConfigList() {
		List<ProtocolConfig> list = read((nodeList) -> XmlDubboUtils
				.parseProtocolConfigList(environment, nodeList, null));
		return list == null ? Collections.emptyList() : list;
	}

	@Override
	public List<ProviderConfig> getProviderConfigList() {
		List<ProviderConfig> list = read((nodeList) -> XmlDubboUtils
				.parseProviderConfigList(environment, nodeList, null));
		return list == null ? Collections.emptyList() : list;
	}

}
