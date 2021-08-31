package io.basc.framework.rpc.http.annotation;

import javax.ws.rs.Path;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanDefinitionLoader;
import io.basc.framework.beans.BeanDefinitionLoaderChain;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.http.client.DefaultHttpClient;
import io.basc.framework.http.client.HttpConnectionFactory;
import io.basc.framework.rpc.CallableFactory;
import io.basc.framework.rpc.support.RemoteCallableBeanDefinition;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class HttpConnectionCallableBeanDefinitionLoader implements BeanDefinitionLoader {

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
					beanFactory.getDefaultValueFactory());
		} else {
			callableFactory = new AnnotationHttpCallableFactory(httpConnectionFactory, path,
					beanFactory.getDefaultValueFactory());
		}
		return new RemoteCallableBeanDefinition(beanFactory, callableFactory, sourceClass);
	}

}