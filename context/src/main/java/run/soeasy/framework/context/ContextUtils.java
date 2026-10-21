package run.soeasy.framework.context;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import lombok.NonNull;
import run.soeasy.framework.commons.io.ResourceAdapter;

public class ContextUtils {
	/**
	 * 匹配所有类的类型过滤器（始终返回true），用于无限制的类扫描场景。
	 */
	private static final TypeFilter INCLUDE_ALL_TYPE_FILTER = (a, b) -> true;

	/**
	 * 根据指定的资源位置模式，获取对应的资源适配器数组。
	 * <p>
	 * 支持的位置模式格式：
	 * <ul>
	 * <li>类路径资源：classpath:xxx.xml（仅加载第一个匹配资源）</li>
	 * <li>类路径通配资源：classpath*:xxx/*.class（加载所有匹配资源）</li>
	 * <li>文件系统资源：file:D:/xxx.txt</li>
	 * <li>URL资源：http://xxx.com/xxx.properties</li>
	 * </ul>
	 *
	 * @param locationPattern 资源位置模式，不能为空
	 * @return 资源适配器数组，包含所有匹配的资源
	 * @throws IOException 当资源查找过程中发生I/O异常（如路径不存在、权限不足等）时抛出
	 */
	public static ResourceAdapter[] getResources(@NonNull String locationPattern) throws IOException {
		return getResources(locationPattern, null);
	}

	/**
	 * 使用指定的类加载器，根据资源位置模式获取对应的资源适配器数组。
	 * <p>
	 * 支持的位置模式格式同 {@link #getResources(String)}，类加载器用于定位类路径资源。
	 *
	 * @param locationPattern 资源位置模式，不能为空
	 * @param classLoader     用于加载资源的类加载器，可为null（此时使用默认类加载器）
	 * @return 资源适配器数组，包含所有匹配的资源
	 * @throws IOException 当资源查找过程中发生I/O异常时抛出
	 */
	public static ResourceAdapter[] getResources(@NonNull String locationPattern, ClassLoader classLoader)
			throws IOException {
		PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver(
				new DefaultResourceLoader(classLoader));
		return ResourceAdapter.of(patternResolver.getResources(locationPattern));
	}

	/**
	 * 根据指定的资源位置，获取对应的单个资源适配器。
	 * <p>
	 * 支持的位置格式：
	 * <ul>
	 * <li>类路径资源：classpath:xxx.properties</li>
	 * <li>文件系统资源：file:D:/xxx.log</li>
	 * <li>URL资源：https://xxx.com/xxx.jpg</li>
	 * </ul>
	 *
	 * @param location 资源位置，不能为空
	 * @return 资源适配器，若资源不存在则返回null
	 */
	public static ResourceAdapter getResource(@NonNull String location) {
		return getResource(location, null);
	}

	/**
	 * 使用指定的类加载器，根据资源位置获取对应的单个资源适配器。
	 * <p>
	 * 支持的位置格式同 {@link #getResource(String)}，类加载器用于定位类路径资源。
	 *
	 * @param location    资源位置，不能为空
	 * @param classLoader 用于加载资源的类加载器，可为null（此时使用默认类加载器）
	 * @return 资源适配器，若资源不存在则返回null
	 */
	public static ResourceAdapter getResource(@NonNull String location, ClassLoader classLoader) {
		ResourceLoader resourceLoader = new DefaultResourceLoader(classLoader);
		Resource resource = resourceLoader.getResource(location);
		if (resource == null) {
			return null;
		}
		return ResourceAdapter.of(resource);
	}

	/**
	 * 在指定的基础包路径下扫描所有类，并返回它们的 {@link BeanDefinition}。
	 * <p>
	 * 此方法使用默认的类加载器和一个匹配所有类的过滤器。
	 *
	 * @param basePackage 要扫描的基础包路径，不能为空。
	 * @return 一个包含所有扫描到的 {@link BeanDefinition} 的 {@link Set}。
	 * @see #scanBeanDefinitions(String, ClassLoader, TypeFilter)
	 */
	public static Set<BeanDefinition> scanBeanDefinitions(@NonNull String basePackage) {
		return scanBeanDefinitions(basePackage, null);
	}

	/**
	 * 使用指定的类加载器，在指定的基础包路径下扫描所有类，并返回它们的 {@link BeanDefinition}。
	 * <p>
	 * 此方法使用一个匹配所有类的过滤器。
	 *
	 * @param basePackage 要扫描的基础包路径，不能为空。
	 * @param classLoader 用于加载资源的类加载器，可以为 {@code null}，此时将使用默认的类加载器。
	 * @return 一个包含所有扫描到的 {@link BeanDefinition} 的 {@link Set}。
	 * @see #scanBeanDefinitions(String, ClassLoader, TypeFilter)
	 */
	public static Set<BeanDefinition> scanBeanDefinitions(@NonNull String basePackage, ClassLoader classLoader) {
		return scanBeanDefinitions(basePackage, classLoader, INCLUDE_ALL_TYPE_FILTER);
	}

	/**
	 * 使用指定的类加载器和类型过滤器，在指定的基础包路径下扫描类，并返回符合条件的 {@link BeanDefinition}。
	 * <p>
	 * 这是组件扫描功能最灵活的实现，允许完全定制扫描过程。
	 *
	 * @param basePackage 要扫描的基础包路径，不能为空。
	 * @param classLoader 用于加载资源的类加载器，可以为 {@code null}。
	 * @param typeFilter  用于筛选类的 {@link TypeFilter}，不能为空。
	 * @return 一个包含所有符合条件的 {@link BeanDefinition} 的 {@link Set}。
	 */
	public static Set<BeanDefinition> scanBeanDefinitions(@NonNull String basePackage, ClassLoader classLoader,
			@NonNull TypeFilter typeFilter) {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
		if (classLoader != null) {
			scanner.setResourceLoader(new DefaultResourceLoader(classLoader));
		}
		scanner.addIncludeFilter(typeFilter);
		return scanner.findCandidateComponents(basePackage);
	}

	/**
	 * 将一组 {@link BeanDefinition} 转换为对应的 {@link Class} 对象集合。
	 * <p>
	 * 此方法会遍历 {@link BeanDefinition}，并尝试加载其指定的类。任何加载失败的类都会被静默忽略。
	 *
	 * @param beanDefinitions 要转换的 {@link BeanDefinition} 集合。
	 * @param classLoader     用于加载类的类加载器，可以为 {@code null}。
	 * @return 一个包含成功加载的 {@link Class} 对象的 {@link Set}。
	 */
	private static Set<Class<?>> toClassSet(Set<BeanDefinition> beanDefinitions, ClassLoader classLoader) {
		return beanDefinitions.stream().map((def) -> {
			try {
				return ClassUtils.forName(def.getBeanClassName(), classLoader);
			} catch (Throwable e) {
				// ignore
			}
			return null;
		}).filter((e) -> e != null).collect(Collectors.toSet());
	}

	/**
	 * 使用指定的类加载器和类型过滤器，在指定的基础包路径下扫描类，并返回符合条件的 {@link Class} 对象集合。
	 * <p>
	 * 这是 {@link #scanBeanDefinitions(String, ClassLoader, TypeFilter)}
	 * 的便捷方法，直接返回加载好的 Class 对象。
	 *
	 * @param basePackage 要扫描的基础包路径，不能为空。
	 * @param classLoader 用于加载类的类加载器，可以为 {@code null}。
	 * @param typeFilter  用于筛选类的 {@link TypeFilter}，不能为空。
	 * @return 一个包含所有符合条件的 {@link Class} 对象的 {@link Set}。
	 */
	public static Set<Class<?>> scanClasses(@NonNull String basePackage, ClassLoader classLoader,
			@NonNull TypeFilter typeFilter) {
		return toClassSet(scanBeanDefinitions(basePackage, classLoader, typeFilter), classLoader);
	}

	/**
	 * 使用指定的类加载器，在指定的基础包路径下扫描所有类，并返回它们的 {@link Class} 对象集合。
	 * <p>
	 * 此方法使用一个匹配所有类的过滤器。
	 *
	 * @param basePackage 要扫描的基础包路径，不能为空。
	 * @param classLoader 用于加载类的类加载器，可以为 {@code null}。
	 * @return 一个包含所有扫描到的 {@link Class} 对象的 {@link Set}。
	 */
	public static Set<Class<?>> scanClasses(@NonNull String basePackage, ClassLoader classLoader) {
		return toClassSet(scanBeanDefinitions(basePackage, classLoader), classLoader);
	}

	/**
	 * 在指定的基础包路径下扫描所有类，并返回它们的 {@link Class} 对象集合。
	 * <p>
	 * 此方法使用默认的类加载器和一个匹配所有类的过滤器。
	 *
	 * @param basePackage 要扫描的基础包路径，不能为空。
	 * @return 一个包含所有扫描到的 {@link Class} 对象的 {@link Set}。
	 */
	public static Set<Class<?>> scanClasses(@NonNull String basePackage) {
		return toClassSet(scanBeanDefinitions(basePackage), null);
	}
}
