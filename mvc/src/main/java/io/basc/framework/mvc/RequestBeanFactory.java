package io.basc.framework.mvc;

import java.io.IOException;
import java.util.Map;

import io.basc.framework.context.Context;
import io.basc.framework.context.support.ContextConfigurator;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptors;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.Destroy;
import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.factory.ParameterFactory;
import io.basc.framework.factory.support.DefaultSingletonRegistry;
import io.basc.framework.mapper.Field;
import io.basc.framework.util.Accept;
import io.basc.framework.util.DefaultStatus;
import io.basc.framework.util.Status;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.annotation.RequestBody;

public class RequestBeanFactory extends RequestParameterFactory implements InstanceFactory, Destroy, ParameterFactory {
	private static final TypeDescriptor REQUEST_BODY_TYPE = TypeDescriptor.map(Map.class, String.class, Object.class);
	private final Context context;
	private final DefaultSingletonRegistry singletonRegistry;
	private final ServerHttpRequest request;

	public RequestBeanFactory(ServerHttpRequest request, WebMessageConverter messageConverter, Context context) {
		super(request, messageConverter);
		this.context = context;
		this.singletonRegistry = new DefaultSingletonRegistry();
		this.singletonRegistry.setParentBeanDefinitionFactory(context);
		this.singletonRegistry.getBeanResolver().setDefaultResolver(context.getBeanResolver());
		this.request = request;
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<? extends T> clazz) {
		return (T) getInstance(clazz.getName());
	}

	@SuppressWarnings("unchecked")
	public Object getInstance(String name) {
		Object instance = singletonRegistry.getSingleton(name);
		if (instance != null) {
			return instance;
		}

		final BeanDefinition beanDefinition = singletonRegistry.getDefinition(name);
		if (beanDefinition == null) {
			return null;
		}

		instance = singletonRegistry.getSingleton(beanDefinition.getId());
		if (instance != null) {
			return instance;
		}

		Status<Object> result = null;
		for (final ParameterDescriptors parameterDescriptors : beanDefinition) {
			if (isAccept(parameterDescriptors)) {
				if (beanDefinition.isSingleton()) {
					result = singletonRegistry.getSingleton(beanDefinition.getId(), () -> {
						return beanDefinition.create(parameterDescriptors.getTypes(),
								getParameters(parameterDescriptors));
					}, false);
				} else {
					result = new DefaultStatus<Object>(true, beanDefinition.create(parameterDescriptors.getTypes(),
							getParameters(parameterDescriptors)));
				}

				if (result != null && result.isActive()) {
					ContextConfigurator beanConfigurator = new ContextConfigurator(context);
					beanConfigurator.getContext().addFilter(new Accept<Field>() {

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

					Map<String, Object> parameterMap = (Map<String, Object>) context.getConversionService()
							.convert(body, TypeDescriptor.forObject(body), REQUEST_BODY_TYPE);
					beanConfigurator.transform(parameterMap, result.get());
				}
				break;
			}
		}

		if (result != null) {
			Object obj = result.get();
			if (result.isActive()) {
				singletonRegistry.dependence(obj, beanDefinition);
				singletonRegistry.init(obj, beanDefinition);
			}
			return obj;
		}
		return null;
	}

	public boolean isInstance(String name) {
		if (singletonRegistry.containsSingleton(name)) {
			return true;
		}

		BeanDefinition beanDefinition = singletonRegistry.getDefinition(name);
		if (beanDefinition == null) {
			return false;
		}

		if (singletonRegistry.containsSingleton(beanDefinition.getId())) {
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

	@Override
	public ClassLoader getClassLoader() {
		return context.getClassLoader();
	}

	public void destroy() {
		singletonRegistry.destroy();
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
