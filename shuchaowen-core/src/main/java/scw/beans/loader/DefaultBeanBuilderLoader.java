package scw.beans.loader;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;

import scw.aop.Filter;
import scw.beans.annotation.Proxy;
import scw.beans.builder.AutoBeanBuilder;
import scw.beans.builder.BeanBuilder;
import scw.beans.builder.ProxyBeanBuilder;
import scw.beans.builder.ThreadPoolExecutorBeanBuilder;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.ClassUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

@Configuration(order = Integer.MIN_VALUE)
public final class DefaultBeanBuilderLoader implements BeanBuilderLoader {
	private static Logger logger = LoggerUtils
			.getLogger(DefaultBeanBuilderLoader.class);

	public BeanBuilder loading(LoaderContext context,
			BeanBuilderLoaderChain loaderChain) throws Exception {
		if (context.getTargetClass() == ExecutorService.class) {
			return new ThreadPoolExecutorBeanBuilder(context.getBeanFactory(),
					context.getPropertyFactory());
		}

		// 未注解service时接口默认实现
		if (context.getTargetClass().isInterface()) {
			String name = context.getTargetClass().getName() + "Impl";
			if (ClassUtils.isPresent(name)
					&& context.getBeanFactory().isInstance(name)) {
				logger.info("{} reference {}", context.getTargetClass()
						.getName(), name);
				return new AutoBeanBuilder(context.getBeanFactory(),
						context.getPropertyFactory(), ClassUtils.forName(name));
			} else {
				int index = context.getTargetClass().getName().lastIndexOf(".");
				name = index == -1 ? (context.getTargetClass().getName() + "Impl")
						: (context.getTargetClass().getName()
								.substring(0, index)
								+ ".impl."
								+ context.getTargetClass().getSimpleName() + "Impl");
				if (ClassUtils.isPresent(name)
						&& context.getBeanFactory().isInstance(name)) {
					logger.info("{} reference {}", context.getTargetClass()
							.getName(), name);
					return new AutoBeanBuilder(context.getBeanFactory(),
							context.getPropertyFactory(),
							ClassUtils.forName(name));
				}
			}
		}

		if (context.getTargetClass().isInterface()
				|| Modifier.isAbstract(context.getTargetClass().getModifiers())) {
			Proxy proxy = context.getTargetClass().getAnnotation(Proxy.class);
			if (proxy != null) {
				return new ProxyBeanBuilder(context, getProxyNames(proxy));
			}
		}

		return new AutoBeanBuilder(context);
	}

	public static LinkedList<String> getProxyNames(Proxy proxy) {
		LinkedList<String> list = new LinkedList<String>();
		if (proxy == null) {
			return list;
		}

		for (String name : proxy.names()) {
			list.add(name);
		}

		for (Class<? extends Filter> c : proxy.value()) {
			list.add(c.getName());
		}

		return list;
	}
}
