package io.basc.framework.netflix.eureka;

import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;

public interface CloudEurekaInstanceConfig extends EurekaInstanceConfig {

	void setNonSecurePort(int port);

	void setSecurePort(int securePort);

	InstanceInfo.InstanceStatus getInitialStatus();
}
