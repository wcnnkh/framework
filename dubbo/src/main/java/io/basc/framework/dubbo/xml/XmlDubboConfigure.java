package io.basc.framework.dubbo.xml;

import io.basc.framework.beans.xml.XmlBeanUtils;
import io.basc.framework.dubbo.DubboConfigure;
import io.basc.framework.env.Environment;
import io.basc.framework.io.Resource;
import io.basc.framework.util.stream.Processor;

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

	public <T> T read(Processor<NodeList, T, Throwable> processor){
		return XmlBeanUtils.readResourceBeans(environment, resource, processor);
	}

	@Override
	public List<ApplicationConfig> getApplicationConfigList() {
		return read((nodeList) -> XmlDubboUtils.parseApplicationConfigList(environment, nodeList, null));
	}

	@Override
	public List<MetadataReportConfig> getMetadataReportConfigList() {
		return read((nodeList) -> XmlDubboUtils.parseMetadataReportConfigList(environment, nodeList, null));
	}

	@Override
	public List<RegistryConfig> getRegistryConfigList() {
		return read((nodeList) -> XmlDubboUtils.parseRegistryConfigList(environment, nodeList, null));
	}

	@Override
	public List<SslConfig> getSslConfigList() {
		return read((nodeList) -> XmlDubboUtils.parseSslConfigList(environment, nodeList));
	}

	@Override
	public List<MetricsConfig> getMetricsConfigList() {
		return read((nodeList) -> XmlDubboUtils.parseMetricsConfigList(environment, nodeList));
	}

	@Override
	public List<ModuleConfig> getModuleConfigList() {
		return read((nodeList) -> XmlDubboUtils.parseModuleConfigList(environment, nodeList));
	}

	@Override
	public List<MonitorConfig> getMonitorConfigList() {
		return read((nodeList) -> XmlDubboUtils.parseMonitorConfigList(environment, nodeList));
	}

	@Override
	public List<ConfigCenterConfig> getConfigCenterConfigList() {
		return read((nodeList) -> XmlDubboUtils.parseConfigCenterConfigs(environment, nodeList));
	}

	@Override
	public List<ConsumerConfig> getConsumerConfigList() {
		return read((nodeList) -> XmlDubboUtils.parseConsumerConfigList(environment, nodeList, null));
	}

	@Override
	public List<ProtocolConfig> getProtocolConfigList() {
		return read((nodeList) -> XmlDubboUtils.parseProtocolConfigList(environment, nodeList, null));
	}

	@Override
	public List<ProviderConfig> getProviderConfigList() {
		return read((nodeList) -> XmlDubboUtils.parseProviderConfigList(environment, nodeList, null));
	}

}
