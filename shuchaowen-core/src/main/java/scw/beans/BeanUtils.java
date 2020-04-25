package scw.beans;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import scw.aop.Filter;
import scw.beans.annotation.AutoImpl;
import scw.beans.annotation.Bean;
import scw.beans.builder.AutoBeanBuilder;
import scw.beans.builder.BeanBuilder;
import scw.beans.loader.BeanBuilderLoader;
import scw.beans.loader.BeanBuilderLoaderChain;
import scw.beans.loader.DefaultBeanBuilderLoader;
import scw.beans.loader.IteratorBeanBuilderLoaderChain;
import scw.beans.loader.LoaderContext;
import scw.beans.xml.XmlBeanParameter;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.core.parameter.ParameterUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.ObjectUtils;
import scw.core.utils.StringUtils;
import scw.lang.Ignore;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

public final class BeanUtils {
	private static Logger logger = LoggerUtils
			.getLogger(DefaultBeanBuilderLoader.class);

	private BeanUtils() {
	};

	private static XmlBeanParameter[] sortParameters(String[] paramNames,
			Class<?>[] parameterTypes, XmlBeanParameter[] beanMethodParameters) {
		XmlBeanParameter[] methodParameters = new XmlBeanParameter[beanMethodParameters.length];
		Class<?>[] types = new Class<?>[methodParameters.length];
		for (int i = 0; i < methodParameters.length; i++) {
			XmlBeanParameter beanMethodParameter = beanMethodParameters[i]
					.clone();
			if (!StringUtils.isNull(beanMethodParameter.getDisplayName())) {
				for (int a = 0; a < paramNames.length; a++) {
					if (paramNames[a].equals(beanMethodParameter
							.getDisplayName())) {
						types[a] = parameterTypes[a];
						methodParameters[a] = beanMethodParameters[i].clone();
						methodParameters[a].setType(parameterTypes[a]);
					}
				}
			} else if (beanMethodParameter.getType() != null) {
				methodParameters[i] = beanMethodParameter;
				types[i] = beanMethodParameter.getType();
			} else {
				types[i] = parameterTypes[i];
				methodParameters[i] = beanMethodParameter;
				methodParameters[i].setType(types[i]);
			}
		}

		return ObjectUtils.equals(Arrays.asList(parameterTypes),
				Arrays.asList(types)) ? methodParameters : null;
	}

	/**
	 * 对参数重新排序
	 * 
	 * @param executable
	 * @param beanMethodParameters
	 * @return
	 */
	public static XmlBeanParameter[] sortParameters(Method method,
			XmlBeanParameter[] beanMethodParameters) {
		if (method.getParameterTypes().length != beanMethodParameters.length) {
			return null;
		}

		return sortParameters(ParameterUtils.getParameterName(method),
				method.getParameterTypes(), beanMethodParameters);
	}

	public static XmlBeanParameter[] sortParameters(Constructor<?> constructor,
			XmlBeanParameter[] beanMethodParameters) {
		if (constructor.getParameterTypes().length != beanMethodParameters.length) {
			return null;
		}

		return sortParameters(ParameterUtils.getParameterName(constructor),
				constructor.getParameterTypes(), beanMethodParameters);
	}

	public static Object[] getBeanMethodParameterArgs(
			XmlBeanParameter[] beanParameters, InstanceFactory instanceFactory,
			PropertyFactory propertyFactory) throws Exception {
		Object[] args = new Object[beanParameters.length];
		for (int i = 0; i < args.length; i++) {
			XmlBeanParameter xmlBeanParameter = beanParameters[i];
			args[i] = xmlBeanParameter.parseValue(instanceFactory,
					propertyFactory);
		}
		return args;
	}

	public static boolean isSingletion(Class<?> type,
			AnnotatedElement annotatedElement) {
		Bean bean = annotatedElement.getAnnotation(Bean.class);
		return bean == null ? true : bean.singleton();
	}

	public static boolean isProxy(Class<?> type,
			AnnotatedElement annotatedElement) {
		if (Modifier.isFinal(type.getModifiers())) {// final修饰的类无法代理
			return false;
		}

		if (Filter.class.isAssignableFrom(type)) {
			return false;
		}

		Bean bean = annotatedElement.getAnnotation(Bean.class);
		if (bean == null) {
			return true;
		}
		return bean.proxy();
	}

	public static String getScanAnnotationPackageName() {
		return GlobalPropertyFactory.getInstance().getValue(
				"scw.scan.beans.package", String.class,
				InstanceUtils.getScanAnnotationPackageName());
	}

	public static BeanBuilder loading(LoaderContext context,
			BeanBuilderLoaderChain chain) {
		AutoImpl autoImpl = context.getTargetClass().getAnnotation(
				AutoImpl.class);
		if (autoImpl == null) {
			if (context.getTargetClass().getAnnotation(Ignore.class) != null) {
				return null;
			}
		} else {
			Collection<Class<?>> impls = getAutoImplClass(autoImpl, context);
			if (!CollectionUtils.isEmpty(impls)) {
				for (Class<?> impl : impls) {
					BeanBuilder beanBuilder = loading(new LoaderContext(impl,
							context), chain);
					if (beanBuilder != null) {
						return beanBuilder;
					}
				}
			}
		}

		for (Class<?> impl : InstanceUtils.getConfigurationClassList(
				context.getTargetClass(), context.getPropertyFactory())) {
			BeanBuilder beanBuilder = loading(new LoaderContext(impl, context),
					chain);
			if (beanBuilder != null) {
				return beanBuilder;
			}
		}

		BeanBuilder beanBuilder = new AutoBeanBuilder(context);
		if (beanBuilder.isInstance()) {
			return beanBuilder;
		}

		Collection<BeanBuilderLoader> loaders = InstanceUtils
				.getConfigurationList(BeanBuilderLoader.class,
						context.getBeanFactory(), context.getPropertyFactory());
		BeanBuilderLoaderChain loaderChain = new IteratorBeanBuilderLoaderChain(
				loaders, chain);
		return loaderChain.loading(context);
	}

	private static Collection<Class<?>> getAutoImplClass(AutoImpl autoConfig,
			LoaderContext context) {
		LinkedList<Class<?>> list = new LinkedList<Class<?>>();
		for (String name : autoConfig.className()) {
			if (StringUtils.isEmpty(name)) {
				continue;
			}

			name = context.getPropertyFactory().format(name, true);
			if (!ClassUtils.isPresent(name)) {
				continue;
			}

			Class<?> clz = ClassUtils.forNameNullable(name);
			if (clz == null) {
				continue;
			}

			if (context.getTargetClass().isAssignableFrom(clz)) {
				list.add(clz);
			} else {
				logger.warn("{} not is assignable from name {}",
						context.getTargetClass(), clz);
			}
		}

		for (Class<?> clz : autoConfig.value()) {
			if (context.getTargetClass().isAssignableFrom(clz)) {
				list.add(clz);
			} else {
				logger.warn("{} not is assignable from {}",
						context.getTargetClass(), clz);
			}
		}
		return list;
	}
}
