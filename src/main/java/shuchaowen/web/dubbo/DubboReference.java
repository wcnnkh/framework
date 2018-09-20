package shuchaowen.web.dubbo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

import shuchaowen.core.util.LazyMap;
import shuchaowen.core.util.StringUtils;

public class DubboReference {
	private ApplicationConfig application;
	private List<RegistryConfig> registryConfigs;
	private LazyMap<String, Object> lazyMap = new LazyMap<String, Object>();
	
	public DubboReference(String name, String registryAddressList) {
		application = new ApplicationConfig(name);

		registryConfigs = new ArrayList<RegistryConfig>();
		List<String> list = StringUtils.splitList(String.class, registryAddressList, ",", true);
		for (String address : list) {
			RegistryConfig registryConfig = new RegistryConfig();
			registryConfig.setAddress(address);
			registryConfigs.add(registryConfig);
		}
	}

	public DubboReference(ApplicationConfig application, List<RegistryConfig> registryConfigs) {
		this.application = application;
		this.registryConfigs = registryConfigs;
	}
	
	public <T> ReferenceConfig<T> newReferenceConfig(){
		ReferenceConfig<T> referenceConfig = new ReferenceConfig<T>();
		referenceConfig.setApplication(application);
		referenceConfig.setRegistries(registryConfigs);
		return referenceConfig;
	}
	
	public <T> ReferenceConfig<T> getReferenceConfig(Class<T> interfaceClass){
		ReferenceConfig<T> referenceConfig = newReferenceConfig();
		referenceConfig.setInterface(interfaceClass);
		return referenceConfig;
	}

	@SuppressWarnings("unchecked")
	public <T> T getReference(final Class<T> interfaceClass, final String version) {
		return (T) lazyMap.get(version + interfaceClass.getName(), new Callable<Object>() {
			
			public Object call() throws Exception {
				ReferenceConfig<T> referenceConfig = new ReferenceConfig<T>();
				referenceConfig.setApplication(application);
				referenceConfig.setRegistries(registryConfigs);
				referenceConfig.setInterface(interfaceClass);
				referenceConfig.setVersion(version);
				referenceConfig.setCheck(false);
				// 和本地bean一样使用xxxService
				return referenceConfig.get();// 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用
			}
		});
	}
}
