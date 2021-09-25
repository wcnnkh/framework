/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.basc.framework.netflix.eureka;

import io.basc.framework.cloud.DiscoveryClient;
import io.basc.framework.cloud.ServiceInstance;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;

/**
 * A {@link DiscoveryClient} implementation for Eureka.
 *
 * @author Spencer Gibb
 * @author Tim Ysewyn
 */
@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class EurekaDiscoveryClient implements DiscoveryClient {

	private final EurekaClient eurekaClient;

	public EurekaDiscoveryClient(EurekaClient eurekaClient) {
		this.eurekaClient = eurekaClient;
	}

	@Override
	public List<ServiceInstance> getInstances(String serviceId) {
		List<InstanceInfo> infos = this.eurekaClient.getInstancesByVipAddress(serviceId, false);
		List<ServiceInstance> instances = new ArrayList<>();
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
