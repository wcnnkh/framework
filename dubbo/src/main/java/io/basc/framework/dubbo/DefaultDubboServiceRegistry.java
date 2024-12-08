package io.basc.framework.dubbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.dubbo.config.ServiceConfig;

import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.xml.XmlBeanUtils;
import io.basc.framework.util.io.Resource;

public class DefaultDubboServiceRegistry extends DefaultDubboReferenceRegistry implements DubboServiceRegistry {
	private List<ServiceConfig<?>> serviceConfigs = Collections.synchronizedList(new ArrayList<>());

	@Override
	public <T> ServiceConfig<T> register(Class<? extends T> serviceClass, T service) {
		ServiceConfig<T> serviceConfig = new ServiceConfig<>();
		serviceConfig.setInterface(serviceClass.getName());
		serviceConfig.setInterfaceClassLoader(serviceClass.getClassLoader());
		serviceConfig.setRef(service);
		register(serviceConfig);
		return serviceConfig;
	}

	@Override
	public void register(ServiceConfig<?> serviceConfig) {
		getDubboBootstrap().service(serviceConfig);
		addServiceConfig(serviceConfig);
	}

	public void addServiceConfig(ServiceConfig<?> serviceConfig) {
		serviceConfigs.add(serviceConfig);
	}

	@Override
	public Collection<ServiceConfig<?>> getServices() {
		return Collections.unmodifiableCollection(serviceConfigs);
	}

	@Override
	public void loadXml(Resource resource, ApplicationContext context) {
		super.loadXml(resource, context);

		List<ServiceConfig<?>> serviceConfigs = XmlBeanUtils
				.parse(context.getResourceLoader(), resource,
						(nodeList) -> parseServiceConfigList(context, nodeList, null))
				.stream().map((e) -> (ServiceConfig<?>) e).collect(Collectors.toList());
		for (ServiceConfig<?> serviceConfig : serviceConfigs) {
			register(serviceConfig);
		}
	}
}
