package io.basc.framework.beans.factory.ioc;

import java.util.Collection;
import java.util.Map;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.ListableBeanFactory;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Executable;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptorTemplate;
import io.basc.framework.core.execution.Parameters;
import io.basc.framework.core.mapping.Property;
import io.basc.framework.core.mapping.PropertyDescriptor;
import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.util.collections.ServiceLoader;
import lombok.Data;

@Data
public class DependencyInjection implements BeanLifecycleResolver, BeanParameterResolver, BeanPropertyResolver {
	private final ConfigurableBeanLifecycleResolver configurableBeanLifecycleResolver = new ConfigurableBeanLifecycleResolver();
	private final ConfigurableBeanParameterResolver configurableBeanParameterResolver = new ConfigurableBeanParameterResolver();
	private final ConfigurableBeanPropertyResolver configurableBeanPropertyResolver = new ConfigurableBeanPropertyResolver();

	@Override
	public boolean canResolveProperty(BeanFactory beanFactory, PropertyDescriptor propertyDescriptor) {
		return configurableBeanPropertyResolver.canResolveProperty(beanFactory, propertyDescriptor);
	}

	@Override
	public Property resolveProperty(BeanFactory beanFactory, PropertyDescriptor propertyDescriptor) {
		return configurableBeanPropertyResolver.resolveProperty(beanFactory, propertyDescriptor);
	}

	@Override
	public boolean canResolveParameters(BeanFactory beanFactory, ParameterDescriptorTemplate template) {
		return configurableBeanParameterResolver.canResolveParameters(beanFactory, template)
				|| template.getParameterDescriptors().allMatch((e) -> canResolveProperty(beanFactory, e));
	}

	@Override
	public Parameters resolveParameters(BeanFactory beanFactory, ParameterDescriptorTemplate template) {
		if (configurableBeanParameterResolver.canResolveParameters(beanFactory, template)) {
			return configurableBeanParameterResolver.resolveParameters(beanFactory, template);
		}

		Parameter[] args = template.getParameterDescriptors().map((e) -> {
			Property property = resolveProperty(beanFactory, e);
			return Parameter.of(e.getIndex(), property);
		}).toArray(Parameter[]::new);
		return Parameters.completed(args);
	}

	@Override
	public boolean isStartupExecute(BeanFactory beanFactory, Executable executable) {
		return configurableBeanLifecycleResolver.isStartupExecute(beanFactory, executable);
	}

	@Override
	public boolean isStoppedExecute(BeanFactory beanFactory, Executable executable) {
		return configurableBeanLifecycleResolver.isStoppedExecute(beanFactory, executable);
	}

	public Object getBean(BeanFactory beanFactory, PropertyDescriptor propertyDescriptor) {
		TypeDescriptor typeDescriptor = propertyDescriptor.getRequiredTypeDescriptor();
		ServiceLoader<Object> serviceLoader = beanFactory.getServiceLoader(typeDescriptor.getResolvableType());
		Object bean = null;
		if (serviceLoader.isUnique()) {
			bean = serviceLoader.getUnique();
		} else if (beanFactory.isTypeMatch(propertyDescriptor.getName(), typeDescriptor.getType())) {
			bean = beanFactory.getBean(propertyDescriptor.getName(), typeDescriptor.getType());
		} else if (typeDescriptor.getType() == ServiceLoader.class) {
			bean = serviceLoader;
		} else if (typeDescriptor.isCollection()) {
			Collection<Object> objects = serviceLoader.toList();
			if (typeDescriptor.getType().isArray()) {
				bean = objects.toArray();
			} else {
				Collection<Object> collection = CollectionUtils.createCollection(typeDescriptor.getType(),
						typeDescriptor.getElementTypeDescriptor().getType(), objects.size());
				collection.addAll(objects);
				bean = collection;
			}
		} else if (typeDescriptor.isMap()) {
			if (beanFactory instanceof ListableBeanFactory) {
				ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
				if (typeDescriptor.getMapKeyTypeDescriptor().getType() == String.class) {
					Map<String, Object> beans = listableBeanFactory.getBeansOfType(typeDescriptor.getResolvableType());
					Map<String, Object> map = CollectionUtils.createMap(typeDescriptor.getType(), String.class,
							beans.size());
					map.putAll(beans);
					bean = map;
				}
			}
		}
		return bean;
	}
}
