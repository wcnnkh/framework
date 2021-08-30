package io.basc.framework.nacos.client;

import io.basc.framework.cloud.DiscoveryClient;
import io.basc.framework.cloud.DiscoveryClientException;
import io.basc.framework.cloud.ServiceInstance;
import io.basc.framework.cloud.ServiceRegistry;
import io.basc.framework.cloud.ServiceRegistryException;
import io.basc.framework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;

/**
 * nacos客户端<br/>
 * {@link https://nacos.io/zh-cn/docs/sdk.html}<br/>
 * 
 * @author shuchaowen
 *
 */
public class NacosClient implements ServiceRegistry<ServiceInstance>,
		DiscoveryClient {
	private final NamingService namingService;
	private final String groupName;

	public NacosClient(NamingService namingService) {
		this(namingService, null);
	}

	public NacosClient(NamingService namingService, String groupName) {
		this.namingService = namingService;
		this.groupName = groupName;
	}

	public List<ServiceInstance> getInstances(String name) {
		List<Instance> instances;
		try {
			instances = groupName == null ? namingService.getAllInstances(name)
					: namingService.getAllInstances(name, groupName);
		} catch (NacosException e) {
			throw new DiscoveryClientException("获取[" + name + "]服务列表错误", e);
		}

		if (CollectionUtils.isEmpty(instances)) {
			return Collections.emptyList();
		}

		List<ServiceInstance> list = new ArrayList<ServiceInstance>(
				instances.size());
		for (Instance instance : instances) {
			list.add(new NacosServiceInstance(instance));
		}
		return list;
	}

	public List<String> getServices() {
		List<ServiceInfo> serviceInfos;
		try {
			serviceInfos = namingService.getSubscribeServices();
		} catch (NacosException e) {
			throw new DiscoveryClientException("获取全部服务信息错误", e);
		}

		if (CollectionUtils.isEmpty(serviceInfos)) {
			return Collections.emptyList();
		}

		List<String> names = new ArrayList<String>(serviceInfos.size());
		for (ServiceInfo info : serviceInfos) {
			if (groupName == null || groupName.equals(info.getGroupName())) {
				names.add(info.getName());
			}
		}
		return names;
	}

	public void register(ServiceInstance instance) {
		Instance registion = new Instance();
		registion.setInstanceId(instance.getId());
		registion.setServiceName(instance.getName());
		registion.setHealthy(true);
		registion.setMetadata(instance.getMetadata());
		registion.setPort(instance.getPort());
		registion.setIp(instance.getHost());
		try {
			if (groupName == null) {
				namingService.registerInstance(instance.getName(), registion);
			} else {
				namingService.registerInstance(instance.getName(), groupName,
						registion);
			}
		} catch (NacosException e) {
			throw new ServiceRegistryException("注册["+instance+"]异常", e);
		}
	}

	public void deregister(ServiceInstance instance) {
		Instance registion = new Instance();
		registion.setInstanceId(instance.getId());
		registion.setServiceName(instance.getName());
		registion.setHealthy(true);
		registion.setMetadata(instance.getMetadata());
		registion.setPort(instance.getPort());
		registion.setIp(instance.getHost());
		try {
			if (groupName == null) {
				namingService.deregisterInstance(instance.getName(), registion);
			} else {
				namingService.deregisterInstance(instance.getName(), groupName,
						registion);
			}
		} catch (NacosException e) {
			throw new ServiceRegistryException("注册["+instance+"]异常", e);
		}
	}
}
