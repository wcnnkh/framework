package shuchaowen.dubbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shuchaowen.core.beans.Bean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.proxy.ProxyFactory;
import shuchaowen.core.util.StringUtils;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

public class DubboReference implements ProxyFactory{
	private ApplicationConfig application;
	private List<RegistryConfig> registryConfigs;
	private Map<Class<?>, Object> proxyMap = new HashMap<Class<?>, Object>();
	private final String version;
	
	public String getVersion() {
		return version;
	}
	
	public DubboReference(String name, String registryAddressList){
		this(name, registryAddressList, "1.0.0");
	}

	public DubboReference(String name, String registryAddressList, String version) {
		application = new ApplicationConfig(name);

		registryConfigs = new ArrayList<RegistryConfig>();
		List<String> list = StringUtils.splitList(String.class, registryAddressList, ",", true);
		for (String address : list) {
			RegistryConfig registryConfig = new RegistryConfig();
			registryConfig.setAddress(address);
			registryConfigs.add(registryConfig);
		}
		this.version = version;
	}

	public DubboReference(ApplicationConfig application, List<RegistryConfig> registryConfigs, String version) {
		this.application = application;
		this.registryConfigs = registryConfigs;
		this.version = version;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getProxy(BeanFactory beanFactory, Class<T> type)
			throws Exception {
		Object obj = proxyMap.get(type);
		if(obj == null){
			synchronized (proxyMap) {
				obj = proxyMap.get(type);
				if(obj == null){
					ReferenceConfig<T> referenceConfig = new ReferenceConfig<T>();
					referenceConfig.setApplication(application);
					referenceConfig.setRegistries(registryConfigs);
					referenceConfig.setInterface(type);
					referenceConfig.setVersion(version);
					referenceConfig.setCheck(false);
					
					Bean bean = new DubboBean(type, referenceConfig);
					obj = bean.newInstance();
					proxyMap.put(type, obj);
				}
			}
		}
		return (T) obj;
	}
}
