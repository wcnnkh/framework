package io.basc.framework.beans.factory.support;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.BeanProvider;
import io.basc.framework.beans.factory.ListableBeanFactory;
import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.ExtractParameterException;
import io.basc.framework.execution.param.ParameterExtractors;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.spi.ServiceLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BeanFactoryParameterExtractor extends ParameterExtractors {
	private final BeanFactory beanFactory;

	@Override
	public boolean canExtractParameter(ParameterDescriptor parameterDescriptor) {
		if (super.canExtractParameter(parameterDescriptor)) {
			return true;
		}

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
	public Object extractParameter(ParameterDescriptor parameterDescriptor) throws ExtractParameterException {
		if (super.canExtractParameter(parameterDescriptor)) {
			return super.extractParameter(parameterDescriptor);
		}

		Optional<Object> bean = beanFactory.getBeanProvider(parameterDescriptor.getTypeDescriptor().getResolvableType())
				.getUnique();
		if (bean.isPresent()) {
			return bean;
		}

		if (beanFactory.isTypeMatch(parameterDescriptor.getName(), parameterDescriptor.getTypeDescriptor().getType())) {
			return beanFactory.getBean(parameterDescriptor.getName(),
					parameterDescriptor.getTypeDescriptor().getType());
		}

		if (parameterDescriptor.getTypeDescriptor().getType() == BeanProvider.class) {
			TypeDescriptor typeDescriptor = parameterDescriptor.getTypeDescriptor().getGeneric(0);
			return beanFactory.getBeanProvider(typeDescriptor.getResolvableType());
		}

		if (parameterDescriptor.getTypeDescriptor().getType() == ServiceLoader.class) {
			TypeDescriptor typeDescriptor = parameterDescriptor.getTypeDescriptor().getGeneric(0);
			if (typeDescriptor.isGeneric()) {
				return beanFactory.getBeanProvider(typeDescriptor.getResolvableType());
			} else {
				if (beanFactory instanceof ServiceLoaderFactory) {
					ServiceLoaderFactory serviceLoaderFactory = (ServiceLoaderFactory) beanFactory;
					return serviceLoaderFactory.getServiceLoader(parameterDescriptor.getTypeDescriptor().getType());
				} else {
					return beanFactory.getBeanProvider(typeDescriptor.getType());
				}
			}
		}

		if (parameterDescriptor.getTypeDescriptor().isCollection()) {
			Collection<Object> objects = beanFactory
					.getBeanProvider(parameterDescriptor.getTypeDescriptor().getResolvableType()).getServices()
					.toList();
			if (parameterDescriptor.getTypeDescriptor().getType().isArray()) {
				return objects.toArray();
			} else {
				Collection<Object> collection = CollectionFactory.createCollection(
						parameterDescriptor.getTypeDescriptor().getType(),
						parameterDescriptor.getTypeDescriptor().getElementTypeDescriptor().getType(), objects.size());
				collection.addAll(objects);
				return collection;
			}
		}

		if (parameterDescriptor.getTypeDescriptor().isMap()) {
			if (beanFactory instanceof ListableBeanFactory) {
				ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
				if (parameterDescriptor.getTypeDescriptor().getMapKeyTypeDescriptor().getType() == String.class) {
					Map<String, Object> beans = listableBeanFactory
							.getBeansOfType(parameterDescriptor.getTypeDescriptor().getResolvableType());
					Map<String, Object> map = CollectionFactory
							.createMap(parameterDescriptor.getTypeDescriptor().getType(), String.class, beans.size());
					map.putAll(beans);
					return map;
				}
			}
		}
		return null;
	}

}
