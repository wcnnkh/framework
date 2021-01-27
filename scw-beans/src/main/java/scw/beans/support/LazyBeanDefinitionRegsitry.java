package scw.beans.support;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import scw.aop.MethodInterceptor;
import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.annotation.AutoImpl;
import scw.beans.annotation.Proxy;
import scw.context.annotation.ProviderClassesLoader;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.instance.InstanceUtils;
import scw.instance.ServiceLoader;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

@SuppressWarnings("unchecked")
public class LazyBeanDefinitionRegsitry extends
		DefaultBeanDefinitionRegistry {
	private static Logger logger = LoggerFactory
			.getLogger(LazyBeanDefinitionRegsitry.class);
	private ConfigurableBeanFactory beanFactory;

	public LazyBeanDefinitionRegsitry(ConfigurableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	private BeanDefinition load(Class<?> clazz) {
		if(BeanDefinitionLoader.class.isAssignableFrom(clazz)){
			return null;
		}
		
		ServiceLoader<BeanDefinitionLoader> serviceLoader = beanFactory.getServiceLoader(BeanDefinitionLoader.class);
		return new IteratorBeanDefinitionLoaderChain(serviceLoader.iterator()).load(beanFactory, clazz);
	}

	private BeanDefinition reference(Class<?> sourceClass) {
		// 未注解service时接口默认实现
		if (sourceClass.isInterface()) {
			String name = sourceClass.getName() + "Impl";
			Class<?> targetClass = ClassUtils.getClass(name, beanFactory.getClassLoader());
			if(targetClass != null && sourceClass.isAssignableFrom(targetClass) && beanFactory.isInstance(targetClass)){
				return beanFactory.getDefinition(targetClass.getName());
			}
			
			int index = sourceClass.getName().lastIndexOf(".");
			name = index == -1 ? (sourceClass.getName() + "Impl")
					: (sourceClass.getName().substring(0, index) + ".impl."
							+ sourceClass.getSimpleName() + "Impl");
			targetClass = ClassUtils.getClass(name, beanFactory.getClassLoader());
			if(targetClass != null && sourceClass.isAssignableFrom(targetClass) && beanFactory.isInstance(targetClass)){
				return beanFactory.getDefinition(targetClass.getName());
			}
		}
		return null;
	}
	
	private BeanDefinition autoImpl(Class<?> sourceClass){
		AutoImpl autoImpl = sourceClass.getAnnotation(AutoImpl.class);
		Collection<Class<?>> autoImpls = autoImpl == null ? null
				: getAutoImplClass(autoImpl, sourceClass);
		if (!CollectionUtils.isEmpty(autoImpls)) {
			for (Class<?> impl : autoImpls) {
				BeanDefinition definition = super.getDefinition(impl);
				if (definition == null) {
					definition = new DefaultBeanDefinition(beanFactory,
							impl);
				}
				if (definition != null && definition.isInstance()) {
					logger.info("Auto {} impl {}", sourceClass, impl);
					return definition;
				}
			}
		}
		return null;
	}
	
	private BeanDefinition provider(Class<?> sourceClass){
		@SuppressWarnings({ "rawtypes"})
		ProviderClassesLoader<?> classesLoader = new ProviderClassesLoader(beanFactory.getContextClassesLoader(), sourceClass);
		for (Class<?> impl : classesLoader) {
			BeanDefinition definition = super.getDefinition(impl);
			if (definition == null) {
				definition = new DefaultBeanDefinition(beanFactory, impl);
			}

			if (definition.isInstance()) {
				logger.info("Service provider {} impl {}", sourceClass, impl);
				return definition;
			}
		}
		return null;
	}
	
	private BeanDefinition proxy(Class<?> sourceClass){
		if (sourceClass.isInterface()
				|| Modifier.isAbstract(sourceClass.getModifiers())) {
			Proxy proxy = sourceClass.getAnnotation(Proxy.class);
			if (proxy != null) {
				return new ProxyBeanDefinition(beanFactory,
						sourceClass, getProxyNames(proxy));
			}
		}
		return null;
	}

	public BeanDefinition load(String name) {
		Class<?> clazz = ClassUtils.getClass(name,
				beanFactory.getClassLoader());
		if (clazz == null) {
			return null;
		}

		if (!InstanceUtils.isSupported(clazz)) {
			return null;
		}
		
		BeanDefinition definition = autoImpl(clazz);
		if(definition == null){
			definition = provider(clazz);
		}
		
		if(definition == null){
			definition = load(clazz);
		}

		if (definition == null) {
			definition = reference(clazz);
		}
		
		if(definition == null){
			definition = proxy(clazz);
		}
		return definition == null? new DefaultBeanDefinition(beanFactory, clazz):definition;
	}

	@Override
	public BeanDefinition getDefinition(String name) {
		BeanDefinition definition = super.getDefinition(name);
		if (definition == null) {
			synchronized (getRegisterDefinitionMutex()) {
				definition = super.getDefinition(name);
				if(definition == null){
					BeanDefinition first = load(name);
					definition = first;
					if (definition == null || !definition.isInstance()) {
						String[] aliases = getAliases(name);
						for (String alias : aliases) {
							definition = load(alias);
							if (definition != null && definition.isInstance()) {
								break;
							}
						}
					}

					if (definition == null) {
						definition = first;
					}

					if (definition != null) {
						definition = registerDefinition(name, definition);
					}
				}
			}
		}
		return definition;
	}

	private Collection<Class<?>> getAutoImplClass(AutoImpl autoConfig, Class<?> sourceClass) {
		List<Class<?>> list = new ArrayList<Class<?>>();
		for (String name : autoConfig.names()) {
			if (StringUtils.isEmpty(name)) {
				continue;
			}

			name = beanFactory.getEnvironment().resolvePlaceholders(name);
			Class<?> clz = ClassUtils.getClass(name,
					beanFactory.getClassLoader());
			if (clz == null) {
				continue;
			}

			if (!InstanceUtils.isSupported(clz)) {
				logger.debug("{} not present", clz);
				continue;
			}

			if (sourceClass.isAssignableFrom(clz)) {
				list.add(clz);
			} else {
				logger.warn("{} not is assignable from name {}", sourceClass,
						clz);
			}
		}

		for (Class<?> clz : autoConfig.value()) {
			if (sourceClass.isAssignableFrom(clz)) {
				list.add(clz);
			} else {
				logger.warn("{} not is assignable from {}", sourceClass, clz);
			}
		}
		return list;
	}

	private static List<String> getProxyNames(Proxy proxy) {
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
