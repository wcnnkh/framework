package io.basc.framework.dubbo;

import java.util.List;

import org.apache.dubbo.config.ServiceConfig;

public interface DubboServiceConfigure {
	List<ServiceConfig<?>> getServiceConfigList();
}
