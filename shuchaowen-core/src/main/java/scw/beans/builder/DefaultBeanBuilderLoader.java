package scw.beans.builder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import scw.aop.MethodInterceptor;
import scw.beans.BeanDefinition;
import scw.beans.DefaultBeanDefinition;
import scw.beans.annotation.Proxy;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.ClassUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

@Configuration(order = Integer.MIN_VALUE)
public final class DefaultBeanBuilderLoader implements BeanBuilderLoader {
	private static Logger logger = LoggerUtils.getLogger(DefaultBeanBuilderLoader.class);

	public BeanDefinition loading(LoaderContext context, BeanBuilderLoaderChain loaderChain) {
		// 未注解service时接口默认实现
		if (context.getTargetClass().isInterface()) {
			String name = context.getTargetClass().getName() + "Impl";
			if (ClassUtils.isPresent(name) && context.getBeanFactory().isInstance(name)) {
				logger.info("{} reference {}", context.getTargetClass().getName(), name);
				return new DefaultBeanDefinition(context.getBeanFactory(), context.getPropertyFactory(),
						ClassUtils.forNameNullable(name));
			} else {
				int index = context.getTargetClass().getName().lastIndexOf(".");
				name = index == -1 ? (context.getTargetClass().getName() + "Impl")
						: (context.getTargetClass().getName().substring(0, index) + ".impl."
								+ context.getTargetClass().getSimpleName() + "Impl");
				if (ClassUtils.isPresent(name) && context.getBeanFactory().isInstance(name)) {
					logger.info("{} reference {}", context.getTargetClass().getName(), name);
					return context.getBeanFactory().getDefinition(name);
				}
			}
		}

		if (context.getTargetClass().isInterface() || Modifier.isAbstract(context.getTargetClass().getModifiers())) {
			Proxy proxy = context.getTargetClass().getAnnotation(Proxy.class);
			if (proxy != null) {
				return new ProxyBeanDefinition(context, getProxyNames(proxy));
			}
		}
		return loaderChain.loading(context);
	}

	public static List<String> getProxyNames(Proxy proxy) {
		if (proxy == null) {
			return Collections.emptyList();
		}

		List<String> list = new ArrayList<String>();
		for (String name : proxy.names()) {
			list.add(name);
		}

		for (Class<? extends MethodInterceptor> c : proxy.value()) {
			list.add(c.getName());
		}

		return Arrays.asList(list.toArray(new String[0]));
	}
}
