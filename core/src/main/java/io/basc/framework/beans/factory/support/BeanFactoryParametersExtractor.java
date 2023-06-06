package io.basc.framework.beans.factory.support;

import java.util.Collection;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.ListableBeanFactory;
import io.basc.framework.beans.factory.NoUniqueBeanDefinitionException;
import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.parameter.GenericParametersExtractor;
import io.basc.framework.execution.parameter.ParameterException;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.ServiceLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BeanFactoryParametersExtractor extends GenericParametersExtractor {
	private final BeanFactory beanFactory;
	private final ConversionService conversionService;

	@Override
	public boolean canExtractParameter(ParameterDescriptor parameterDescriptor) {
		if (beanFactory instanceof ServiceLoaderFactory) {
			if (parameterDescriptor.getTypeDescriptor().getType() == ServiceLoader.class) {
				return true;
			}
		}

		if (beanFactory instanceof ListableBeanFactory) {
			ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
			if (parameterDescriptor.getTypeDescriptor().isMap()) {
				if (parameterDescriptor.getTypeDescriptor().getMapKeyTypeDescriptor().getType() == String.class) {
					return !listableBeanFactory
							.getBeanNamesForType(parameterDescriptor.getTypeDescriptor().getResolvableType()).isEmpty();
				}
				return false;
			}

			if (parameterDescriptor.getTypeDescriptor().isCollection()) {
				return listableBeanFactory
						.getBeanNamesForType(
								parameterDescriptor.getTypeDescriptor().getElementTypeDescriptor().getResolvableType())
						.isEmpty();
			}

			if (listableBeanFactory.getBeanNamesForType(parameterDescriptor.getTypeDescriptor().getResolvableType())
					.isSingleton()) {
				return true;
			}

			return beanFactory.containsBean(parameterDescriptor.getName());
		}
		return true;
	}

	@Override
	public Object extractParameter(ParameterDescriptor parameterDescriptor) throws ParameterException {
		if (beanFactory instanceof ServiceLoaderFactory) {
			ServiceLoaderFactory serviceLoaderFactory = (ServiceLoaderFactory) beanFactory;
			if (parameterDescriptor.getTypeDescriptor().getType() == ServiceLoader.class) {
				TypeDescriptor typeDescriptor = parameterDescriptor.getTypeDescriptor().getGeneric(0);
				if (typeDescriptor.isGeneric()) {
					// TODO 待处理
				} else {
					return serviceLoaderFactory.getServiceLoader(parameterDescriptor.getTypeDescriptor().getType());
				}
			}
		}

		if (beanFactory instanceof ListableBeanFactory) {
			ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
			if (parameterDescriptor.getTypeDescriptor().isMap()) {
				if (parameterDescriptor.getTypeDescriptor().getMapKeyTypeDescriptor().getType() == String.class) {
					return listableBeanFactory
							.getBeansOfType(parameterDescriptor.getTypeDescriptor().getResolvableType());
				}
				return false;
			}

			if (parameterDescriptor.getTypeDescriptor().isCollection()) {
				Collection<Object> objects = listableBeanFactory
						.getBeansOfType(parameterDescriptor.getTypeDescriptor().getResolvableType()).values();
				return conversionService.convert(objects,
						TypeDescriptor.collection(Collection.class,
								parameterDescriptor.getTypeDescriptor().getResolvableType()),
						parameterDescriptor.getTypeDescriptor());
			}

			if (listableBeanFactory.getBeanNamesForType(parameterDescriptor.getTypeDescriptor().getResolvableType())
					.isSingleton()) {
				return listableBeanFactory.getBean(parameterDescriptor.getTypeDescriptor().getResolvableType());
			}

			return beanFactory.getBean(parameterDescriptor.getName(),
					parameterDescriptor.getTypeDescriptor().getType());
		}
		try {
			return beanFactory.getBean(parameterDescriptor.getTypeDescriptor().getResolvableType());
		} catch (NoUniqueBeanDefinitionException e) {
			return beanFactory.getBean(parameterDescriptor.getName(),
					parameterDescriptor.getTypeDescriptor().getType());
		}
	}

}
