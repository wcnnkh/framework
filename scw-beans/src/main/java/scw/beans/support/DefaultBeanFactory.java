package scw.beans.support;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import scw.aop.AopPolicy;
import scw.aop.ConfigurableAop;
import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionRegistry;
import scw.beans.BeanFactory;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeanLifeCycleEvent;
import scw.beans.BeanUtils;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.SingletonBeanRegistry;
import scw.beans.ioc.Ioc;
import scw.context.ClassesLoader;
import scw.context.ConfigurableClassesLoader;
import scw.context.support.DefaultAop;
import scw.context.support.DefaultProviderLoaderFactory;
import scw.context.support.LifecycleAuxiliary;
import scw.core.parameter.ConstructorParameterDescriptorsIterator;
import scw.core.parameter.ParameterDescriptors;
import scw.core.utils.ClassUtils;
import scw.env.Environment;
import scw.env.support.DefaultEnvironment;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.instance.InstanceFactory;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;
import scw.lang.NotSupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.ClassLoaderProvider;
import scw.util.Creator;
import scw.util.Result;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultBeanFactory extends LifecycleAuxiliary implements
		ConfigurableBeanFactory {
	private static Logger logger = LoggerUtils
			.getLogger(DefaultBeanFactory.class);
	private final DefaultBasicEventDispatcher<BeanLifeCycleEvent> beanLifeCycleEventDispatcher = new DefaultBasicEventDispatcher<BeanLifeCycleEvent>(
			true);
	private final DefaultEnvironment environment = new DefaultEnvironment(true) {
		public ClassLoader getClassLoader() {
			return DefaultBeanFactory.this.getClassLoader();
		};
	};

	private final DefaultAop aop = new DefaultAop(environment);

	private final DefaultProviderLoaderFactory contextLoader = new DefaultProviderLoaderFactory(
			environment, this);
	private final BeanDefinitionRegistry beanDefinitionRegistry = new LazyBeanDefinitionRegsitry(
			this);
	private final SingletonBeanRegistry singletonBeanRegistry = new DefaultSingletonBeanRegistry(
			this);
	private ClassLoaderProvider classLoaderProvider;

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	public DefaultBeanFactory() {
		aop.addAopPolicy(new AopPolicy() {

			public boolean isProxy(Object instance) {
				return BeanUtils.getRuntimeBean(instance) != null;
			}
		});
		registerSingleton(BeanFactory.class.getName(), this);
		registerAlias(BeanFactory.class.getName(),
				InstanceFactory.class.getName());
		registerAlias(BeanFactory.class.getName(),
				NoArgsInstanceFactory.class.getName());

		registerSingleton(Environment.class.getName(), getEnvironment());
	}

	public ConfigurableAop getAop() {
		return aop;
	}

	public DefaultProviderLoaderFactory getContextLoader() {
		return contextLoader;
	}

	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}

	public EventRegistration registerListener(
			EventListener<BeanLifeCycleEvent> eventListener) {
		return beanLifeCycleEventDispatcher.registerListener(eventListener);
	}

	public void publishEvent(BeanLifeCycleEvent event) {
		beanLifeCycleEventDispatcher.publishEvent(event);
	}

	public final boolean containsDefinition(String beanName) {
		return beanDefinitionRegistry.containsDefinition(beanName);
	}

	public final boolean isAlias(String name) {
		return beanDefinitionRegistry.isAlias(name);
	}
	
	public boolean hasAlias(String name, String alias) {
		return beanDefinitionRegistry.hasAlias(name, alias);
	}

	public final String[] getAliases(String name) {
		return beanDefinitionRegistry.getAliases(name);
	}

	public BeanDefinition getDefinition(Class clazz) {
		return beanDefinitionRegistry.getDefinition(clazz);
	}

	public final BeanDefinition getDefinition(String name) {
		return beanDefinitionRegistry.getDefinition(name);
	}

	public final String[] getDefinitionIds() {
		return beanDefinitionRegistry.getDefinitionIds();
	}

	public final void registerAlias(String name, String alias) {
		beanDefinitionRegistry.registerAlias(name, alias);
	}

	public Object getDefinitionMutex() {
		return beanDefinitionRegistry.getDefinitionMutex();
	}

	public final BeanDefinition registerDefinition(String name,
			BeanDefinition beanDefinition) {
		return beanDefinitionRegistry.registerDefinition(name, beanDefinition);
	}
	
	public BeanDefinition registerDefinition(BeanDefinition beanDefinition) {
		return beanDefinitionRegistry.registerDefinition(beanDefinition);
	}

	public final void removeAlias(String alias) {
		beanDefinitionRegistry.removeAlias(alias);
	}

	public final ClassesLoader<?> getClassesLoader(String packageName) {
		return contextLoader.getClassesLoader(packageName);
	}

	public final DefaultEnvironment getEnvironment() {
		return environment;
	}

	public final ConfigurableClassesLoader<?> getContextClassesLoader() {
		return contextLoader.getContextClassesLoader();
	}

	public Object getSingletonMutex() {
		return singletonBeanRegistry.getSingletonMutex();
	}

	public boolean containsSingleton(String beanName) {
		return singletonBeanRegistry.containsSingleton(beanName);
	}

	public void registerSingleton(String beanName, Object singletonObject) {
		singletonBeanRegistry.registerSingleton(beanName, singletonObject);
		SingletionBeanDefinition definition = new SingletionBeanDefinition(
				beanName, singletonObject);
		registerDefinition(beanName, definition);
		definition.dependence(singletonObject);
	}

	public boolean isSingleton(Class<?> clazz) {
		return isSingleton(clazz.getName());
	}

	public boolean isInstance(Class<?> clazz) {
		return isInstance(clazz.getName());
	}

	public <T> T getInstance(Class<T> type, Object... params) {
		return getInstance(type.getName(), params);
	}

	public <T> T getInstance(Class<T> type, Class<?>[] parameterTypes,
			Object[] params) {
		return getInstance(type.getName(), parameterTypes, params);
	}

	public boolean isInstance(Class<?> clazz, Object... params) {
		return isInstance(clazz.getName(), params);
	}

	public boolean isInstance(String name, Object... params) {
		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return false;
		}

		return definition.isInstance(params);
	}

	public boolean isInstance(Class<?> clazz, Class<?>[] parameterTypes) {
		return isInstance(clazz.getName(), parameterTypes);
	}

	public boolean isInstance(String name, Class<?>[] parameterTypes) {
		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return false;
		}

		return definition.isInstance(parameterTypes);
	}

	public boolean isSingleton(String name) {
		if (containsSingleton(name)) {
			return true;
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return false;
		}

		return containsSingleton(definition.getId())
				|| definition.isSingleton();
	}

	public void init(BeanDefinition definition, Object instance) {
		definition.dependence(instance);
		definition.init(instance);
	}

	public <T> T getInstance(Class<T> clazz) {
		return getInstance(clazz.getName());
	}

	public Object getSingleton(String beanName) {
		return singletonBeanRegistry.getSingleton(beanName);
	}

	public <T> Result<T> getSingleton(String beanName, Creator<T> creater) {
		return singletonBeanRegistry.getSingleton(beanName, creater);
	}

	public String[] getSingletonNames() {
		return singletonBeanRegistry.getSingletonNames();
	}

	public void removeSingleton(String name) {
		singletonBeanRegistry.removeSingleton(name);
	}

	public <T> T getInstance(String name) {
		Object object = getSingleton(name);
		if (object != null) {
			return (T) object;
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return null;
		}

		Result<Object> result;
		if (definition.isSingleton()) {
			result = singletonBeanRegistry.getSingleton(definition);
		} else {
			result = new Result<Object>(true, definition.create());
		}

		object = result.getResult();
		if (result.isActive()) {
			init(definition, object);
		}
		return (T) object;
	}

	public boolean isInstance(String name) {
		if (containsSingleton(name)) {
			return true;
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return false;
		}

		return containsSingleton(definition.getId()) || definition.isInstance();
	}

	public <T> T getInstance(String name, Object... params) {
		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return null;
		}

		Object instance = definition.create(params);
		init(definition, instance);
		return (T) instance;
	}

	public <T> T getInstance(String name, Class<?>[] parameterTypes,
			Object[] params) {
		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return null;
		}

		Object instance = definition.create(parameterTypes, params);
		init(definition, instance);
		return (T) instance;
	}

	public void postProcessBeanFactory(
			BeanFactoryPostProcessor beanFactoryPostProcessor) {
		beanFactoryPostProcessor.postProcessBeanFactory(this);
	}

	@Override
	protected void beforeInit() throws Throwable {
		postProcessBeanFactory(new MethodBeanFactoryPostProcessor());
		postProcessBeanFactory(new ServiceBeanFactoryPostProcessor());

		environment.loadServices(this);
		aop.loadServices(this);

		for (BeanDefinition definition : getServiceLoader(BeanDefinition.class)) {
			if (containsDefinition(definition.getId())) {
				logger.debug("ignore definition {}", definition);
				continue;
			}
			registerDefinition(definition.getId(), definition);
		}
		
		for (BeanFactoryPostProcessor processor : getServiceLoader(BeanFactoryPostProcessor.class)) {
			postProcessBeanFactory(processor);
		}
		super.beforeInit();
	}

	@Override
	protected void afterInit() throws Throwable {
		postProcessBeanFactory(new ExecutorBeanFactoryPostProcessor());
		// 处理静态依赖
		for (Class<?> clazz : getContextClassesLoader()) {
			for (Ioc ioc : Ioc.forClass(clazz)) {
				ioc.getDependence().process(null, null, this);
				ioc.getInit().process(null, null, this);
			}
		}
		super.afterInit();
	}

	@Override
	protected void beforeDestroy() throws Throwable {
		singletonBeanRegistry.destroyAll();
		super.beforeDestroy();
	}

	@Override
	protected void afterDestroy() throws Throwable {
		for (Class<?> clazz : getContextClassesLoader()) {
			for (Ioc ioc : Ioc.forClass(clazz)) {
				ioc.getDestroy().process(null, null, this);
			}
		}
		super.afterDestroy();
	}

	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		return contextLoader.getServiceLoader(serviceClass);
	}

	private final class SingletionBeanDefinition implements BeanDefinition {
		private final Object instance;
		private final String name;

		public SingletionBeanDefinition(String name, Object instance) {
			this.name = name;
			this.instance = instance;
		}

		public String getId() {
			return name;
		}

		public Collection<String> getNames() {
			return Collections.emptyList();
		}

		public Class getTargetClass() {
			return getEnvironment().getUserClass(instance.getClass());
		}

		public boolean isSingleton() {
			return true;
		}

		public boolean isInstance() {
			return true;
		}

		public Object create() throws BeansException {
			return instance;
		}

		public Object create(Object... params) throws BeansException {
			throw new NotSupportedException(getId());
		}

		public Object create(Class[] parameterTypes, Object[] params)
				throws BeansException {
			throw new NotSupportedException(getId());
		}

		public void dependence(Object instance) throws BeansException {
			if (instance != null) {
				for (Ioc ioc : Ioc.forClass(instance.getClass())) {
					ioc.getDependence().process(this, instance,
							DefaultBeanFactory.this);
				}
			}
		}

		public void init(Object bean) throws BeansException {
		}

		public void destroy(Object bean) throws BeansException {
		}

		public AnnotatedElement getAnnotatedElement() {
			return getTargetClass();
		}

		public Iterator<ParameterDescriptors> iterator() {
			return new ConstructorParameterDescriptorsIterator(getTargetClass());
		}

		public boolean isInstance(Object... params) {
			return false;
		}

		public boolean isInstance(Class[] parameterTypes) {
			return false;
		}
	}
}
