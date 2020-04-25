package scw.beans.service;

import java.util.Collection;
import java.util.HashSet;

import scw.beans.BeanFactory;
import scw.beans.DefaultBeanDefinition;
import scw.beans.annotation.Service;
import scw.beans.builder.AutoBeanBuilder;
import scw.core.Init;
import scw.core.annotation.AnnotationUtils;
import scw.core.utils.ArrayUtils;
import scw.util.value.property.PropertyFactory;

public class ServiceBeanDefinition extends DefaultBeanDefinition {

	public ServiceBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass) {
		super(beanFactory, propertyFactory, targetClass, new AutoBeanBuilder(
				beanFactory, propertyFactory, targetClass));
	}

	@Override
	public Collection<String> getNames() {
		Service service = getAnnotatedElement().getAnnotation(Service.class);
		if (service == null) {
			return super.getNames();
		}

		HashSet<String> list = new HashSet<String>(4);
		if (ArrayUtils.isEmpty(service.name())
				&& ArrayUtils.isEmpty(service.value())) {
			Class<?>[] clzs = getTargetClass().getInterfaces();
			if (clzs != null) {
				for (Class<?> i : clzs) {
					if (AnnotationUtils.isIgnore(i)) {
						continue;
					}

					if (i.getName().startsWith("java.")
							|| i.getName().startsWith("javax.")
							|| i == scw.core.Destroy.class || i == Init.class) {
						continue;
					}

					list.add(i.getName());
				}
			}
		} else {
			for (Class<?> name : service.value()) {
				list.add(name.getName());
			}

			for (String name : service.name()) {
				list.add(name);
			}
		}
		return list;
	}
}
