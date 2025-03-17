package run.soeasy.framework.beans.factory.ioc;

import java.util.Collection;
import java.util.Map;

import lombok.Data;
import run.soeasy.framework.beans.factory.BeanFactory;
import run.soeasy.framework.beans.factory.ListableBeanFactory;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.transform.stereotype.Property;
import run.soeasy.framework.core.convert.transform.stereotype.PropertyDescriptor;
import run.soeasy.framework.core.execution.Executable;
import run.soeasy.framework.core.execution.Parameter;
import run.soeasy.framework.core.execution.ParameterDescriptorTemplate;
import run.soeasy.framework.core.execution.Parameters;
import run.soeasy.framework.util.collections.CollectionUtils;
import run.soeasy.framework.util.collections.ServiceLoader;

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
