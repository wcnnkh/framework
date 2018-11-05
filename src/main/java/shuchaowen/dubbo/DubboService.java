package shuchaowen.dubbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.StringUtils;

public class DubboService {
	private ApplicationConfig application;
	private List<RegistryConfig> registryConfigs;
	private List<ProtocolConfig> protocolConfigs;
	
	public DubboService(String name, String registryAddressList, int port){
		application = new ApplicationConfig(name);
		
		registryConfigs = new ArrayList<RegistryConfig>();
		List<String> addressList = StringUtils.splitList(String.class, registryAddressList, ",", true);
		for(String address : addressList){
			RegistryConfig registryConfig = new RegistryConfig();
			registryConfig.setAddress(address);
			registryConfigs.add(registryConfig);
		}
		
		protocolConfigs = new ArrayList<ProtocolConfig>();
		ProtocolConfig protocolConfig = new ProtocolConfig();
		protocolConfig.setName("dubbo");
		protocolConfig.setPort(port);
		protocolConfig.setThreads(200);
		protocolConfigs.add(protocolConfig);
	}

	public DubboService(ApplicationConfig application, List<RegistryConfig> registryConfigs,
			List<ProtocolConfig> protocolConfigs) {
		this.application = application;
		this.registryConfigs = registryConfigs;
		this.protocolConfigs = protocolConfigs;
	}

	@SuppressWarnings("unchecked")
	public <T> void registerService(Class<T> interfaceClass, Object ref, String version) {
		ServiceConfig<T> serviceConfig = new ServiceConfig<T>();
		serviceConfig.setApplication(application);
		serviceConfig.setRegistries(registryConfigs);
		serviceConfig.setProtocols(protocolConfigs);
		serviceConfig.setInterface(interfaceClass);
		serviceConfig.setRef((T)ref);
		serviceConfig.setVersion(version);
		// 暴露及注册服务
		serviceConfig.export();
	}
	
	public void registerService(BeanFactory beanFactory, Collection<Class<?>> classList, String version) {
		for (Class<?> clz : classList) {
			Service service = clz.getAnnotation(Service.class);
			if (service != null) {
				Class<?>[] interfaces = clz.getInterfaces();
				for (Class<?> i : interfaces) {
					registerService(i, beanFactory.get(i), version);
				}
			}
		}
	}
	
	public void registerService(BeanFactory beanFactory, String packageName, String version) {
		registerService(beanFactory, ClassUtils.getClasses(packageName), version);
	}

	public void registerService(BeanFactory beanFactory, String version) {
		registerService(beanFactory, "", version);
	}
}
