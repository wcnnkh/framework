package io.basc.framework.dubbo;

import java.util.List;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConfigCenterConfig;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.MetricsConfig;
import org.apache.dubbo.config.ModuleConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.SslConfig;

public interface DubboConfigure {
	List<ApplicationConfig> getApplicationConfigList();

	List<MetadataReportConfig> getMetadataReportConfigList();

	List<RegistryConfig> getRegistryConfigList();

	List<SslConfig> getSslConfigList();

	List<MetricsConfig> getMetricsConfigList();

	List<ModuleConfig> getModuleConfigList();

	List<MonitorConfig> getMonitorConfigList();

	List<ConfigCenterConfig> getConfigCenterConfigList();
}
