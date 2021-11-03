package io.basc.framework.mvc.message;

import java.io.IOException;
import java.util.Map;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeanUtils;
import io.basc.framework.beans.SingletonBeanRegistry;
import io.basc.framework.beans.annotation.ConfigurationProperties;
import io.basc.framework.beans.support.DefaultSingletonBeanRegistry;
import io.basc.framework.context.Destroy;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptors;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.mapper.Field;
import io.basc.framework.mvc.message.annotation.RequestBody;
import io.basc.framework.orm.convert.EntityConversionService;
import io.basc.framework.util.Accept;
import io.basc.framework.util.DefaultStatus;
import io.basc.framework.util.Status;
import io.basc.framework.value.support.MapPropertyFactory;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebUtils;

public class RequestBeanFactory extends RequestParameterFactory
		implements NoArgsInstanceFactory, Destroy, ParameterFactory {
	private static final TypeDescriptor REQUEST_BODY_TYPE = TypeDescriptor.map(Map.class, String.class, Object.class);
	private final BeanFactory beanFactory;
	private final SingletonBeanRegistry singletonBeanRegistry;
	private final ServerHttpRequest request;

	public RequestBeanFactory(ServerHttpRequest request, WebMessageConverter messageConverter,
			BeanFactory beanFactory) {
		super(request, messageConverter);
		this.beanFactory = beanFactory;
		this.singletonBeanRegistry = new DefaultSingletonBeanRegistry(beanFactory);
		this.request = request;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public <T> T getInstance(Class<T> clazz) {
		return getInstance(clazz.getName());
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		Object instance = singletonBeanRegistry.getSingleton(name);
		if (instance != null) {
			return (T) instance;
		}

		final BeanDefinition beanDefinition = beanFactory.getDefinition(name);
		if (beanDefinition == null) {
			return null;
		}

		instance = singletonBeanRegistry.getSingleton(beanDefinition.getId());
		if (instance != null) {
			return (T) instance;
		}

		Status<Object> result = null;
		for (final ParameterDescriptors parameterDescriptors : beanDefinition) {
			if (isAccept(parameterDescriptors)) {
				if (beanDefinition.isSingleton()) {
					result = singletonBeanRegistry.getSingleton(beanDefinition.getId(), () -> {
						return beanDefinition.create(parameterDescriptors.getTypes(),
								getParameters(parameterDescriptors));
					});
				} else {
					result = new DefaultStatus<Object>(true, beanDefinition.create(parameterDescriptors.getTypes(),
							getParameters(parameterDescriptors)));
				}

				if (result != null && result.isActive()) {
					EntityConversionService conversionService = BeanUtils.createEntityConversionService(
							beanFactory.getEnvironment(),
							beanDefinition.getAnnotatedElement().getAnnotation(ConfigurationProperties.class));
					conversionService.setUseSuperClass(true);
					conversionService.getFieldAccept().add(new Accept<Field>() {

						@Override
						public boolean accept(Field field) {
							for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
								if (parameterDescriptor.getName().equals(field.getSetter().getName())) {
									return false;
								}
							}
							return true;
						}
					});

					Object body;
					try {
						body = WebUtils.getRequestBody(request);
					} catch (IOException e) {
						throw new WebMessagelConverterException(request.toString());
					}

					Map<String, Object> parameterMap = (Map<String, Object>) beanFactory.getEnvironment()
							.getConversionService().convert(body, TypeDescriptor.forObject(body), REQUEST_BODY_TYPE);
					conversionService.configurationProperties(new MapPropertyFactory(parameterMap), result.get());
				}
				break;
			}
		}

		if (result != null) {
			Object obj = result.get();
			if (result.isActive()) {
				beanDefinition.dependence(obj);
				beanDefinition.init(obj);
			}
			return (T) obj;
		}
		return null;
	}

	public boolean isInstance(String name) {
		if (singletonBeanRegistry.containsSingleton(name)) {
			return true;
		}

		BeanDefinition beanDefinition = beanFactory.getDefinition(name);
		if (beanDefinition == null) {
			return false;
		}

		if (singletonBeanRegistry.containsSingleton(beanDefinition.getId())) {
			return true;
		}

		if (beanDefinition.isSingleton() && beanDefinition.isInstance()) {
			return true;
		}

		for (ParameterDescriptors parameterDescriptors : beanDefinition) {
			if (isAccept(parameterDescriptors)) {
				return true;
			}
		}
		return false;
	}

	public boolean isInstance(Class<?> clazz) {
		return isInstance(clazz.getName());
	}

	public ClassLoader getClassLoader() {
		return beanFactory.getClassLoader();
	}

	public void destroy() {
		singletonBeanRegistry.destroyAll();
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		if (parameterDescriptor.getType().isAnnotationPresent(RequestBody.class)) {
			return isInstance(parameterDescriptor.getType());
		}
		return super.isAccept(parameterDescriptor);
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		if (parameterDescriptor.getType().isAnnotationPresent(RequestBody.class)) {
			return getInstance(parameterDescriptor.getType());
		}
		return super.getParameter(parameterDescriptor);
	}
}
