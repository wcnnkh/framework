package shuchaowen.dubbo;

import java.util.ArrayList;
import java.util.List;

import shuchaowen.core.beans.AbstractBeanFactory;
import shuchaowen.core.beans.Bean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.StringUtils;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

public class DubboBeanFactory extends AbstractBeanFactory{
	private final BeanFactory beanFactory;
	private ApplicationConfig application;
	private List<RegistryConfig> registryConfigs;
	private final String version;
	
	public String getVersion() {
		return version;
	}
	
	public DubboBeanFactory(BeanFactory beanFactory, String name, String registryAddressList){
		this(beanFactory, name, registryAddressList, "1.0.0");
	}

	public DubboBeanFactory(BeanFactory beanFactory, String name, String registryAddressList, String version) {
		this.beanFactory = beanFactory;
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

	public DubboBeanFactory(BeanFactory beanFactory, ApplicationConfig application, List<RegistryConfig> registryConfigs, String version) {
		this.beanFactory = beanFactory;
		this.application = application;
		this.registryConfigs = registryConfigs;
		this.version = version;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Bean newBean(String name) throws Exception {
		Class<?> type = ClassUtils.forName(name);
		ReferenceConfig referenceConfig = new ReferenceConfig();
		referenceConfig.setApplication(application);
		referenceConfig.setRegistries(registryConfigs);
		referenceConfig.setInterface(type);
		referenceConfig.setVersion(version);
		referenceConfig.setCheck(false);

		return new DubboBean(beanFactory, type, referenceConfig);
	}
}
