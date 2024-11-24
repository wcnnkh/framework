package io.basc.framework.rpc.http.beans;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.support.BeanDefinitionLoader;
import io.basc.framework.beans.factory.support.BeanDefinitionLoaderChain;
import io.basc.framework.beans.factory.support.FactoryBeanDefinition;
import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.env.Environment;
import io.basc.framework.rpc.http.DefaultHttpRemoteResolvers;
import io.basc.framework.rpc.http.HttpRemoteCallableFactory;
import io.basc.framework.rpc.http.HttpRemoteResolver;
import io.basc.framework.rpc.support.RemoteCallableBeanDefinition;
import io.basc.framework.util.ClassUtils;

@ConditionalOnParameters(order = Ordered.LOWEST_PRECEDENCE)
public class HttpRemoteBeanLoader implements BeanDefinitionLoader {

	@Override
	public BeanDefinition load(BeanFactory beanFactory, ClassLoader classLoader, String name,
			BeanDefinitionLoaderChain loaderChain) {
		Class<?> sourceClass = ClassUtils.getClass(name, beanFactory.getClassLoader());
		if (sourceClass == null) {
			return null;
		}

		if (sourceClass == HttpRemoteResolver.class) {
			return new FactoryBeanDefinition(beanFactory, DefaultHttpRemoteResolvers.class);
		}

		if (sourceClass == HttpRemoteCallableFactory.class) {
			return new HttpRemoteCallableFactoryDefinition(beanFactory.getInstance(Environment.class));
		}

		if (beanFactory.isInstance(HttpRemoteResolver.class)) {
			HttpRemoteResolver remoteUriResolver = beanFactory.getInstance(HttpRemoteResolver.class);
			if (remoteUriResolver.canResolve(sourceClass)) {
				return new RemoteCallableBeanDefinition(beanFactory,
						() -> beanFactory.getInstance(HttpRemoteCallableFactory.class), sourceClass);
			}
		}
		return loaderChain.load(beanFactory, classLoader, name);
	}

}
