package scw.beans.loader;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import scw.beans.BeansException;
import scw.beans.annotation.AutoImpl;
import scw.beans.builder.BeanBuilder;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

public final class BeanBuilderLoaderUtils {
	private static Logger logger = LoggerUtils
			.getLogger(BeanBuilderLoaderUtils.class);

	private BeanBuilderLoaderUtils() {
	};

	private static Collection<BeanBuilderLoader> getBeanBuilderLoaders(
			AutoImpl autoConfig, LoaderContext context) {
		LinkedList<BeanBuilderLoader> autoBeanServices = new LinkedList<BeanBuilderLoader>();
		if (autoConfig != null) {
			for (String name : autoConfig.serviceName()) {
				if (StringUtils.isEmpty(name)) {
					continue;
				}

				name = context.getPropertyFactory().format(name, true);
				autoBeanServices.add((BeanBuilderLoader) context
						.getBeanFactory().getInstance(name));
			}

			for (Class<? extends BeanBuilderLoader> service : autoConfig
					.service()) {
				if (service == null) {
					continue;
				}

				autoBeanServices.add(context.getBeanFactory().getInstance(
						service));
			}
		}

		autoBeanServices.add(new DefaultBeanBuilderLoader());
		return autoBeanServices;
	}

	public static BeanBuilder loading(LoaderContext context, AutoImpl autoConfig) {
		Collection<BeanBuilderLoader> loaders = getBeanBuilderLoaders(
				autoConfig, context);
		if (!CollectionUtils.isEmpty(loaders)) {
			BeanBuilderLoaderChain loaderChain = new IteratorBeanBuilderLoaderChain(
					loaders, null);
			try {
				return loaderChain.loading(context);
			} catch (Exception e) {
				throw new BeansException(context.getTargetClass().getName(), e);
			}
		}
		return null;
	}

	public static Collection<Class<?>> getAutoImplClass(AutoImpl autoConfig,
			Class<?> type, PropertyFactory propertyFactory) {
		LinkedList<Class<?>> list = new LinkedList<Class<?>>();
		for (String name : autoConfig.className()) {
			if (StringUtils.isEmpty(name)) {
				continue;
			}

			name = propertyFactory.format(name, true);
			if (!ClassUtils.isPresent(name)) {
				continue;
			}

			Class<?> clz = ClassUtils.forNameNullable(name);
			if (clz == null) {
				continue;
			}

			if (type.isAssignableFrom(clz)) {
				list.add(clz);
			} else {
				logger.warn("{} not is assignable from name {}", type, clz);
			}
		}

		for (Class<?> clz : autoConfig.value()) {
			if (type.isAssignableFrom(clz)) {
				list.add(clz);
			} else {
				logger.warn("{} not is assignable from {}", type, clz);
			}
		}
		return list;
	}

	private static final class IteratorBeanBuilderLoaderChain extends
			AbstractBeanBuilderLoaderChain {
		private Iterator<BeanBuilderLoader> iterator;

		public IteratorBeanBuilderLoaderChain(
				Collection<BeanBuilderLoader> autoBeanServices,
				BeanBuilderLoaderChain chain) {
			super(chain);
			if (!CollectionUtils.isEmpty(autoBeanServices)) {
				iterator = autoBeanServices.iterator();
			}
		}

		@Override
		protected BeanBuilderLoader getNext(LoaderContext context) {
			if (iterator == null) {
				return null;
			}

			if (iterator.hasNext()) {
				return iterator.next();
			}

			return null;
		}
	}
}
