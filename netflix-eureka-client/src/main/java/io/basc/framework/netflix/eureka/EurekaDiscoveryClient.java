package io.basc.framework.netflix.eureka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;

import io.basc.framework.cloud.DiscoveryClient;
import io.basc.framework.cloud.Service;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.util.collect.CollectionUtils;

/**
 * A {@link DiscoveryClient} implementation for Eureka.
 *
 */
@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class EurekaDiscoveryClient implements DiscoveryClient {

	private final EurekaClient eurekaClient;

	public EurekaDiscoveryClient(EurekaClient eurekaClient) {
		this.eurekaClient = eurekaClient;
	}

	@Override
	public List<Service> getInstances(String serviceId) {
		List<InstanceInfo> infos = this.eurekaClient.getInstancesByVipAddress(serviceId, false);
		List<Service> instances = new ArrayList<>();
		for (InstanceInfo info : infos) {
			instances.add(new EurekaServiceInstance(info));
		}
		return instances;
	}

	@Override
	public List<String> getServices() {
		Applications applications = this.eurekaClient.getApplications();
		if (applications == null) {
			return Collections.emptyList();
		}
		List<Application> registered = applications.getRegisteredApplications();
		List<String> names = new ArrayList<>();
		for (Application app : registered) {
			List<InstanceInfo> instanceInfos = app.getInstances();
			if(CollectionUtils.isEmpty(instanceInfos)) {
				continue;
			}
			names.add(app.getName().toLowerCase());

		}
		return names;
	}
}
