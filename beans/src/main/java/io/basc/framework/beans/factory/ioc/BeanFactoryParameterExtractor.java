package io.basc.framework.beans.factory.ioc;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.BeanProvider;
import io.basc.framework.beans.factory.ListableBeanFactory;
import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.stractegy.CollectionFactory;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.core.execution.param.SimpleParameter;
import io.basc.framework.util.ServiceLoader;

public class BeanFactoryParameterExtractor implements AutowireParameterExtractor {

	@Override
	public boolean canExtractParameter(BeanFactory beanFactory, ParameterDescriptor parameterDescriptor) {
		Optional<Object> bean = beanFactory.getBeanProvider(parameterDescriptor.getTypeDescriptor().getResolvableType())
				.getUnique();
		if (bean.isPresent()) {
			return true;
		}

		if (beanFactory.isTypeMatch(parameterDescriptor.getName(), parameterDescriptor.getTypeDescriptor().getType())) {
			return true;
		}

		if (parameterDescriptor.getTypeDescriptor().getType() == BeanProvider.class) {
			return true;
		}

		if (parameterDescriptor.getTypeDescriptor().getType() == ServiceLoader.class) {
			return true;
		}

		if (parameterDescriptor.getTypeDescriptor().isCollection()) {
			if (parameterDescriptor.getTypeDescriptor().getResolvableType().getGenerics().length != 1) {
				return false;
			}
			TypeDescriptor typeDescriptor = parameterDescriptor.getTypeDescriptor().getGeneric(0);
			return !beanFactory.getBeanProvider(typeDescriptor.getResolvableType()).isEmpty();
		}

		if (parameterDescriptor.getTypeDescriptor().isMap()) {
			if (beanFactory instanceof ListableBeanFactory) {
				ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
				if (parameterDescriptor.getTypeDescriptor().getMapKeyTypeDescriptor().getType() == String.class) {
					if (!listableBeanFactory
							.getBeanNamesForType(parameterDescriptor.getTypeDescriptor().getResolvableType())
							.isEmpty()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public Parameter extractParameter(BeanFactory beanFactory, ParameterDescriptor parameterDescriptor) {
		Optional<Object> optional = beanFactory
				.getBeanProvider(parameterDescriptor.getTypeDescriptor().getResolvableType()).getUnique();
		Object bean = null;
		if (optional.isPresent()) {
			bean = optional.get();
		} else if (beanFactory.isTypeMatch(parameterDescriptor.getName(),
				parameterDescriptor.getTypeDescriptor().getType())) {
			bean = beanFactory.getBean(parameterDescriptor.getName(),
					parameterDescriptor.getTypeDescriptor().getType());
		} else if (parameterDescriptor.getTypeDescriptor().getType() == BeanProvider.class) {
			TypeDescriptor typeDescriptor = parameterDescriptor.getTypeDescriptor().getGeneric(0);
			bean = beanFactory.getBeanProvider(typeDescriptor.getResolvableType());
		} else if (parameterDescriptor.getTypeDescriptor().getType() == ServiceLoader.class) {
			TypeDescriptor typeDescriptor = parameterDescriptor.getTypeDescriptor().getGeneric(0);
			if (typeDescriptor.isGeneric()) {
				bean = beanFactory.getBeanProvider(typeDescriptor.getResolvableType());
			} else {
				if (beanFactory instanceof ServiceLoaderFactory) {
					ServiceLoaderFactory serviceLoaderFactory = (ServiceLoaderFactory) beanFactory;
					bean = serviceLoaderFactory.getServiceLoader(parameterDescriptor.getTypeDescriptor().getType());
				} else {
					bean = beanFactory.getBeanProvider(typeDescriptor.getType());
				}
			}
		} else if (parameterDescriptor.getTypeDescriptor().isCollection()) {
			Collection<Object> objects = beanFactory
					.getBeanProvider(parameterDescriptor.getTypeDescriptor().getResolvableType()).getServices()
					.toList();
			if (parameterDescriptor.getTypeDescriptor().getType().isArray()) {
				bean = objects.toArray();
			} else {
				Collection<Object> collection = CollectionFactory.createCollection(
						parameterDescriptor.getTypeDescriptor().getType(),
						parameterDescriptor.getTypeDescriptor().getElementTypeDescriptor().getType(), objects.size());
				collection.addAll(objects);
				bean = collection;
			}
		} else if (parameterDescriptor.getTypeDescriptor().isMap()) {
			if (beanFactory instanceof ListableBeanFactory) {
				ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
				if (parameterDescriptor.getTypeDescriptor().getMapKeyTypeDescriptor().getType() == String.class) {
					Map<String, Object> beans = listableBeanFactory
							.getBeansOfType(parameterDescriptor.getTypeDescriptor().getResolvableType());
					Map<String, Object> map = CollectionFactory
							.createMap(parameterDescriptor.getTypeDescriptor().getType(), String.class, beans.size());
					map.putAll(beans);
					bean = map;
				}
			}
		}

		SimpleParameter parameter = new SimpleParameter(parameterDescriptor);
		parameter.setValue(bean);
		return parameter;
	}

}
