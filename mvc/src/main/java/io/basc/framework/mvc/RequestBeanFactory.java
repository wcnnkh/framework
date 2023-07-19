package io.basc.framework.mvc;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

import io.basc.framework.beans.factory.Destroy;
import io.basc.framework.beans.factory.InstanceFactory;
import io.basc.framework.beans.factory.ParameterFactory;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.support.DefaultSingletonBeanRegistry;
import io.basc.framework.context.Context;
import io.basc.framework.context.support.ContextConfigurator;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.Element;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterDescriptors;
import io.basc.framework.util.Return;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.annotation.RequestBody;

public class RequestBeanFactory extends RequestParameterFactory implements InstanceFactory, Destroy, ParameterFactory {
	private static final TypeDescriptor REQUEST_BODY_TYPE = TypeDescriptor.map(Map.class, String.class, Object.class);
	private final Context context;
	private final DefaultSingletonBeanRegistry singletonRegistry;
	private final ServerHttpRequest request;

	public RequestBeanFactory(ServerHttpRequest request, WebMessageConverter messageConverter, Context context) {
		super(request, messageConverter);
		this.context = context;
		this.singletonRegistry = new DefaultSingletonBeanRegistry();
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

		Return<Object> result = null;
		for (final ParameterDescriptors parameterDescriptors : beanDefinition) {
			if (isAccept(parameterDescriptors)) {
				if (beanDefinition.isSingleton()) {
					result = singletonRegistry.getSingleton(beanDefinition.getId(), () -> {
						return beanDefinition.create(parameterDescriptors.getTypes(),
								getParameters(parameterDescriptors));
					}, false);
				} else {
					result = Return.success(beanDefinition.create(parameterDescriptors.getTypes(),
							getParameters(parameterDescriptors)));
				}

				if (result != null && result.isSuccess()) {
					ContextConfigurator beanConfigurator = new ContextConfigurator(context);
					beanConfigurator.getContext().addFilter(new Predicate<Element>() {

						@Override
						public boolean test(Element field) {
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
			if (result.isSuccess()) {
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
		if (parameterDescriptor.getTypeDescriptor().getType().isAnnotationPresent(RequestBody.class)) {
			return isInstance(parameterDescriptor.getTypeDescriptor().getType());
		}
		return super.isAccept(parameterDescriptor);
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		if (parameterDescriptor.getTypeDescriptor().getType().isAnnotationPresent(RequestBody.class)) {
			return getInstance(parameterDescriptor.getTypeDescriptor().getType());
		}
		return super.getParameter(parameterDescriptor);
	}
}
