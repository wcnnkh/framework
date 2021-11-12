package io.basc.framework.dubbo;

import java.util.List;

import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.ServiceConfig;

public interface DubboServiceConfigure {
	List<ProtocolConfig> getProtocolConfigList();

	List<ProviderConfig> getProviderConfigList();

	List<ServiceConfig<?>> getServiceConfigList();
}
