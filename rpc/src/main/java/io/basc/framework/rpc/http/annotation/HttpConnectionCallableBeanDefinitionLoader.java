package io.basc.framework.rpc.http.annotation;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanDefinitionLoader;
import io.basc.framework.beans.BeanDefinitionLoaderChain;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.parameter.ParameterDefaultValueFactory;
import io.basc.framework.http.client.DefaultHttpClient;
import io.basc.framework.http.client.HttpConnectionFactory;
import io.basc.framework.rpc.CallableFactory;
import io.basc.framework.rpc.support.RemoteCallableBeanDefinition;

import javax.ws.rs.Path;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class HttpConnectionCallableBeanDefinitionLoader implements BeanDefinitionLoader {
	private volatile ParameterDefaultValueFactory defaultValueFactory;

	public ParameterDefaultValueFactory getDefaultValueFactory(BeanFactory beanFactory) {
		if (defaultValueFactory == null) {
			synchronized (this) {
				if (defaultValueFactory == null) {
					defaultValueFactory = new ParameterDefaultValueFactory(beanFactory);
				}
			}
		}
		return defaultValueFactory;
	}

	public BeanDefinition load(ConfigurableBeanFactory beanFactory, Class<?> sourceClass,
			BeanDefinitionLoaderChain loaderChain) {
		HttpRemote remote = sourceClass.getAnnotation(HttpRemote.class);
		Path path = sourceClass.getAnnotation(Path.class);
		if (remote == null && path == null) {
			return loaderChain.load(beanFactory, sourceClass);
		}

		HttpConnectionFactory httpConnectionFactory = new DefaultHttpClient(
				beanFactory.getEnvironment().getConversionService(), beanFactory);
		CallableFactory callableFactory;
		if (remote != null) {
			callableFactory = new AnnotationHttpCallableFactory(httpConnectionFactory, remote,
					getDefaultValueFactory(beanFactory));
		} else {
			callableFactory = new AnnotationHttpCallableFactory(httpConnectionFactory, path,
					getDefaultValueFactory(beanFactory));
		}
		return new RemoteCallableBeanDefinition(beanFactory, callableFactory, sourceClass);
	}

}
