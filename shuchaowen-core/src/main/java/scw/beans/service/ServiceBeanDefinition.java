package scw.beans.service;

import java.util.Collection;
import java.util.HashSet;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.annotation.Service;
import scw.beans.builder.AutoBeanDefinition;
import scw.core.utils.ArrayUtils;
import scw.value.property.PropertyFactory;

public class ServiceBeanDefinition extends AutoBeanDefinition {

	public ServiceBeanDefinition(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass) {
		super(beanFactory, propertyFactory, targetClass);
	}

	@Override
	public Collection<String> getNames() {
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
}
