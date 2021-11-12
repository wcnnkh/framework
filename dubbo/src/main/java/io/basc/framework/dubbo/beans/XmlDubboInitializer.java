package io.basc.framework.dubbo.beans;

import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.dubbo.DubboConfigure;
import io.basc.framework.dubbo.DubboServiceConfigure;

import java.util.List;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.MetricsConfig;
import org.apache.dubbo.config.ModuleConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.SslConfig;
import org.apache.dubbo.rpc.model.ApplicationModel;

/**
 * 暴露dubbo服务
 * 
 * @author shuchaowen
 *
 */
@Provider
public class XmlDubboInitializer implements ApplicationPostProcessor {

	@Override
	public void postProcessApplication(ConfigurableApplication application) throws Throwable {
		if(application.isInstance(DubboConfigure.class)){
			DubboConfigure configure = application.getInstance(DubboConfigure.class);
			List<RegistryConfig> registryConfigs = configure.getRegistryConfigList();
			for (ApplicationConfig config : configure.getApplicationConfigList()) {
				config.setRegistries(registryConfigs);
				ApplicationModel.defaultModel().getApplicationConfigManager().setApplication(config);
			}
			
			for(ProtocolConfig config : configure.getProtocolConfigList()){
				ApplicationModel.defaultModel().getApplicationConfigManager().addProtocol(config);
			}
			
			for (MetadataReportConfig config : configure.getMetadataReportConfigList()) {
				ApplicationModel.defaultModel().getApplicationConfigManager().addMetadataReport(config);
			}

			for (SslConfig config : configure.getSslConfigList()) {
				ApplicationModel.defaultModel().getApplicationConfigManager().setSsl(config);
			}

			for (MetricsConfig config : configure.getMetricsConfigList()) {
				ApplicationModel.defaultModel().getApplicationConfigManager().setMetrics(config);
			}

			for (MonitorConfig config : configure.getMonitorConfigList()) {
				ApplicationModel.defaultModel().getApplicationConfigManager().setMonitor(config);
			}
			
			//default module
			
			for (ModuleConfig config : configure.getModuleConfigList()) {
				ApplicationModel.defaultModel().getDefaultModule().getConfigManager().setModule(config);
			}
			
			for(ProviderConfig config : configure.getProviderConfigList()){
				ApplicationModel.defaultModel().getDefaultModule().getConfigManager().addProvider(config);
			}
			
			for(ConsumerConfig config : configure.getConsumerConfigList()){
				ApplicationModel.defaultModel().getDefaultModule().getConfigManager().addConsumer(config);
			}
		}
		
		if(application.isInstance(DubboServiceConfigure.class)){
			DubboServiceConfigure configure = application.getInstance(DubboServiceConfigure.class);
			for(ServiceConfig<?> config : configure.getServiceConfigList()){
				config.export();
			}
		}
	}
}
