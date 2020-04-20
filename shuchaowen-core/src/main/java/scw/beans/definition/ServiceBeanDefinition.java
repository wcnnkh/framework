package scw.beans.definition;

import java.util.Collection;
import java.util.HashSet;

import scw.beans.BeanFactory;
import scw.beans.annotation.Service;
import scw.beans.definition.builder.AutoBeanBuilder;
import scw.core.Init;
import scw.core.annotation.AnnotationUtils;
import scw.util.value.property.PropertyFactory;

public class ServiceBeanDefinition extends DefaultBeanDefinition {

	public ServiceBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass) {
		super(beanFactory, propertyFactory, targetClass,
				new AutoBeanBuilder(beanFactory, propertyFactory,
						targetClass));
	}

	@Override
	public Collection<String> getNames() {
		Service service = getAnnotatedElement().getAnnotation(Service.class);
		HashSet<String> list = new HashSet<String>(4);
		if (service == null) {
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
