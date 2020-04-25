package scw.beans.loader;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;

import scw.aop.Filter;
import scw.beans.annotation.AutoImpl;
import scw.beans.annotation.Proxy;
import scw.beans.builder.AutoBeanBuilder;
import scw.beans.builder.BeanBuilder;
import scw.beans.builder.ProxyBeanBuilder;
import scw.beans.builder.ThreadPoolExecutorBeanBuilder;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public final class DefaultBeanBuilderLoader implements BeanBuilderLoader {
	private static Logger logger = LoggerUtils
			.getLogger(DefaultBeanBuilderLoader.class);

	private BeanBuilder defaultLoading(LoaderContext context,
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
				return new ProxyBeanBuilder(context.getBeanFactory(),
						context.getPropertyFactory(), context.getTargetClass(),
						getProxyNames(proxy));
			}
		}

		if (!ReflectionUtils.isInstance(context.getTargetClass(), false)) {
			BeanBuilderLoaderChain autoBeanServiceChain = new NextAutoBeanServiceChain(
					InstanceUtils.getConfigurationClassList(
							BeanBuilderLoader.class,
							context.getPropertyFactory()), loaderChain);
			return autoBeanServiceChain.loading(context);
		}

		return new AutoBeanBuilder(context.getBeanFactory(),
				context.getPropertyFactory(), context.getTargetClass());
	}

	public BeanBuilder loading(LoaderContext context,
			BeanBuilderLoaderChain serviceChain) throws Exception {
		AutoImpl autoConfig = context.getTargetClass().getAnnotation(
				AutoImpl.class);
		if (autoConfig == null) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Collection<Class<?>> impls = InstanceUtils
					.getConfigurationClassList(
							(Class) context.getTargetClass(),
							context.getPropertyFactory());
			if (!CollectionUtils.isEmpty(impls)) {
				for (Class<?> impl : impls) {
					return defaultLoading(
							new LoaderContext(impl, context.getBeanFactory(),
									context.getPropertyFactory()), serviceChain);
				}
			}
			return defaultLoading(context, serviceChain);
		}

		Collection<Class<?>> implList = BeanBuilderLoaderUtils
				.getAutoImplClass(autoConfig, context.getTargetClass(),
						context.getPropertyFactory());
		if (CollectionUtils.isEmpty(implList)) {
			return defaultLoading(context, serviceChain);
		}

		for (Class<?> clz : implList) {
			BeanBuilder autoBean = BeanBuilderLoaderUtils.loading(
					new LoaderContext(clz, context.getBeanFactory(), context
							.getPropertyFactory()), autoConfig);
			if (autoBean != null && autoBean.isInstance()) {
				return autoBean;
			}
		}

		return defaultLoading(context, serviceChain);
	}

	private static class NextAutoBeanServiceChain extends
			AbstractBeanBuilderLoaderChain {
		private Iterator<Class<BeanBuilderLoader>> iterator;

		public NextAutoBeanServiceChain(
				Collection<Class<BeanBuilderLoader>> collection,
				BeanBuilderLoaderChain chain) {
			super(chain);
			if (!CollectionUtils.isEmpty(collection)) {
				this.iterator = collection.iterator();
			}
		}

		@Override
		protected BeanBuilderLoader getNext(LoaderContext context) {
			if (iterator == null) {
				return null;
			}

			if (iterator.hasNext()) {
				return context.getBeanFactory().getInstance(iterator.next());
			}

			return null;
		}
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
