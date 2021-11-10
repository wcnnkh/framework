package io.basc.framework.rpc.http.beans;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanDefinitionLoader;
import io.basc.framework.beans.BeanDefinitionLoaderChain;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.rpc.http.DefaultHttpRemoteResolvers;
import io.basc.framework.rpc.http.HttpRemoteCallableFactory;
import io.basc.framework.rpc.http.HttpRemoteResolver;
import io.basc.framework.rpc.support.RemoteCallableBeanDefinition;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class HttpRemoteBeanLoader implements BeanDefinitionLoader {

	@Override
	public BeanDefinition load(ConfigurableBeanFactory beanFactory, Class<?> sourceClass,
			BeanDefinitionLoaderChain loaderChain) {
		if (sourceClass == HttpRemoteResolver.class) {
			return DefaultBeanDefinition.create(beanFactory, DefaultHttpRemoteResolvers.class,
					() -> new DefaultHttpRemoteResolvers());
		}

		if (sourceClass == HttpRemoteCallableFactory.class) {
			return new HttpRemoteCallableFactoryDefinition(beanFactory);
		}

		if (beanFactory.isInstance(HttpRemoteResolver.class)) {
			HttpRemoteResolver remoteUriResolver = beanFactory.getInstance(HttpRemoteResolver.class);
			if (remoteUriResolver.canResolve(sourceClass)) {
				return new RemoteCallableBeanDefinition(beanFactory,
						() -> beanFactory.getInstance(HttpRemoteCallableFactory.class), sourceClass);
			}
		}
		return loaderChain.load(beanFactory, sourceClass);
	}

}
