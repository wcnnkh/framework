package shuchaowen.dubbo;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

import shuchaowen.core.beans.AbstractBeanFactory;
import shuchaowen.core.beans.Bean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.StringUtils;

public class DubboBeanFactory extends AbstractBeanFactory{
	private final BeanFactory beanFactory;
	private ApplicationConfig application;
	private List<RegistryConfig> registryConfigs;
	private final String version;
	private String[] packageNames;
	
	public String getVersion() {
		return version;
	}
	
	public DubboBeanFactory(BeanFactory beanFactory, String packageName, String name, String registryAddressList){
		this(beanFactory, packageName, name, registryAddressList, "1.0.0");
	}

	public DubboBeanFactory(BeanFactory beanFactory, String packageName, String name, String registryAddressList, String version) {
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
		this.packageNames = StringUtils.commonSplit(packageName);
	}

	public DubboBeanFactory(BeanFactory beanFactory, String packageName, ApplicationConfig application, List<RegistryConfig> registryConfigs, String version) {
		this.beanFactory = beanFactory;
		this.application = application;
		this.registryConfigs = registryConfigs;
		this.version = version;
		this.packageNames = StringUtils.commonSplit(packageName);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Bean newBean(String name) throws Exception {
		if(!contains(name)){
			return null;
		}
		
		Class<?> type = ClassUtils.forName(name);
		ReferenceConfig referenceConfig = new ReferenceConfig();
		referenceConfig.setApplication(application);
		referenceConfig.setRegistries(registryConfigs);
		referenceConfig.setInterface(type);
		referenceConfig.setVersion(version);
		referenceConfig.setCheck(false);

		return new DubboBean(beanFactory, type, referenceConfig);
	}
	
	@Override
	public boolean contains(String name) {
		boolean find = false;
		if (packageNames != null) {
			for (String packagePrefix : packageNames) {
				if (name.startsWith(packagePrefix)) {
					find = true;
					break;
				}
			}
		}
		
		return find || super.contains(name);
	}
}
