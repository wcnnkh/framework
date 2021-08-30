package io.basc.framework.beans.support;

import io.basc.framework.beans.BeanUtils;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.annotation.Service;
import io.basc.framework.util.ArrayUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class ServiceBeanDefinition extends DefaultBeanDefinition {
	private Collection<String> names;
	
	public ServiceBeanDefinition(ConfigurableBeanFactory beanFactory, Class<?> targetClass) {
		super(beanFactory, targetClass);
		this.names = getInternalNames();
		this.names = Arrays.asList(names.toArray(new String[0]));
	}
	
	private Collection<String> getInternalNames(){
		Service service = getAnnotatedElement().getAnnotation(Service.class);
		if (service == null) {
			return super.getNames();
		}

		if (!ArrayUtils.isEmpty(service.value())) {
			HashSet<String> list = new HashSet<String>(super.getNames());
			for (String name : service.value()) {
				list.add(name);
			}
			return list;
		}

		Class<?> serviceInterface = BeanUtils.getServiceInterface(getTargetClass());
		if (serviceInterface == null) {
			return super.getNames();
		}

		HashSet<String> list = new HashSet<String>(super.getNames());
		list.add(serviceInterface.getName());
		return list;
	}

	@Override
	public Collection<String> getNames() {
		return names;
	}
}
