package scw.beans.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import scw.beans.BeanUtils;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.annotation.Service;
import scw.core.utils.ArrayUtils;

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
