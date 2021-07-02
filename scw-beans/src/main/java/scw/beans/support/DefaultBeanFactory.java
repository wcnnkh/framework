package scw.beans.support;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import scw.aop.ConfigurableAop;
import scw.aop.support.DefaultConfigurableAop;
import scw.aop.support.ProxyUtils;
import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionRegistry;
import scw.beans.BeanFactory;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeanUtils;
import scw.beans.BeanlifeCycleEvent;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.SingletonBeanRegistry;
import scw.beans.ioc.Ioc;
import scw.context.Destroy;
import scw.context.Init;
import scw.context.support.AbstractConfigurableContext;
import scw.core.parameter.ConstructorParameterDescriptorsIterator;
import scw.core.parameter.ParameterDescriptors;
import scw.core.utils.ClassUtils;
import scw.env.Environment;
import scw.env.Sys;
import scw.event.EventDispatcher;
import scw.event.support.DefaultEventDispatcher;
import scw.instance.InstanceFactory;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoaderFactory;
import scw.lang.NotSupportedException;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.ClassLoaderProvider;
import scw.util.Creator;
import scw.util.DefaultClassLoaderProvider;
import scw.util.Result;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultBeanFactory extends AbstractConfigurableContext
		implements ConfigurableBeanFactory, ServiceLoaderFactory, Init, Destroy {
	private static Logger logger = LoggerFactory.getLogger(DefaultBeanFactory.class);
	private final DefaultEventDispatcher<BeanlifeCycleEvent> beanLifeCycleEventDispatcher = new DefaultEventDispatcher<BeanlifeCycleEvent>(
			true);
	private final DefaultConfigurableAop aop = new DefaultConfigurableAop();
	private final BeanDefinitionRegistry beanDefinitionRegistry = new LazyBeanDefinitionRegsitry(this);
	private final SingletonBeanRegistry singletonBeanRegistry = new DefaultSingletonBeanRegistry(this);
	private ClassLoaderProvider classLoaderProvider;
	private volatile boolean initialized;
	private List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<BeanFactoryPostProcessor>(8);

	public DefaultBeanFactory() {
		super(true);
		aop.addAopPolicy((instance) -> BeanUtils.getRuntimeBean(instance) != null);
		getEnvironment().addFactory(Sys.env);

		registerSingleton(BeanFactory.class.getName(), this);
		registerAlias(BeanFactory.class.getName(), InstanceFactory.class.getName());
		registerAlias(BeanFactory.class.getName(), NoArgsInstanceFactory.class.getName());

		registerSingleton(Environment.class.getName(), getEnvironment());
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	public void setClassLoader(ClassLoader classLoader) {
		setClassLoaderProvider(new DefaultClassLoaderProvider(classLoader));
	}

	@Override
	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}

	@Override
	public EventDispatcher<BeanlifeCycleEvent> getLifecycleDispatcher() {
		return beanLifeCycleEventDispatcher;
	}

	public ConfigurableAop getAop() {
		return aop;
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

	public final BeanDefinition registerDefinition(String name, BeanDefinition beanDefinition) {
		return beanDefinitionRegistry.registerDefinition(name, beanDefinition);
	}

	public BeanDefinition registerDefinition(BeanDefinition beanDefinition) {
		return beanDefinitionRegistry.registerDefinition(beanDefinition);
	}

	public final void removeAlias(String alias) {
		beanDefinitionRegistry.removeAlias(alias);
	}

	public Object getSingletonMutex() {
		return singletonBeanRegistry.getSingletonMutex();
	}

	public boolean containsSingleton(String beanName) {
		return singletonBeanRegistry.containsSingleton(beanName);
	}

	public void registerSingleton(String beanName, Object singletonObject) {
		singletonBeanRegistry.registerSingleton(beanName, singletonObject);
		SingletionBeanDefinition definition = new SingletionBeanDefinition(beanName, singletonObject);
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

	public <T> T getInstance(Class<T> type, Class<?>[] parameterTypes, Object[] params) {
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

		return containsSingleton(definition.getId()) || definition.isSingleton();
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

	@Override
	public <T, E extends Throwable> Result<T> getSingleton(String name, Creator<T, E> creater) throws E {
		return singletonBeanRegistry.getSingleton(name, creater);
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

	public <T> T getInstance(String name, Class<?>[] parameterTypes, Object[] params) {
		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return null;
		}

		Object instance = definition.create(parameterTypes, params);
		init(definition, instance);
		return (T) instance;
	}

	public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor) {
		synchronized (this) {
			if (initialized) {
				throwInitializedBeanException();
			}

			beanFactoryPostProcessors.add(beanFactoryPostProcessor);
		}
	}

	protected void throwInitializedBeanException() throws BeansException {
		throw new BeansException("The bean factory has been initialized");
	}

	public void postProcessBeanFactory(BeanFactoryPostProcessor beanFactoryPostProcessor) {
		beanFactoryPostProcessor.postProcessBeanFactory(this);
	}
	
	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		aop.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}

	public void init() throws Throwable {
		synchronized (this) {
			if (initialized) {
				throwInitializedBeanException();
			}

			postProcessBeanFactory(new MethodBeanFactoryPostProcessor());
			postProcessBeanFactory(new ServiceBeanFactoryPostProcessor());
			postProcessBeanFactory(new ExecutorBeanFactoryPostProcessor());

			for (BeanFactoryPostProcessor processor : getServiceLoader(BeanFactoryPostProcessor.class)) {
				postProcessBeanFactory(processor);
			}

			for (BeanFactoryPostProcessor processor : beanFactoryPostProcessors) {
				postProcessBeanFactory(processor);
			}

			for (BeanDefinition definition : getServiceLoader(BeanDefinition.class)) {
				if (containsDefinition(definition.getId())) {
					logger.debug("ignore definition {}", definition);
					continue;
				}
				registerDefinition(definition.getId(), definition);
			}
			//在定义初始完成后就可以认为已经初始化了
			initialized = true;
			
			configure(this);
			
			// TODO 初始化所有单例(原来是想全部懒加载，但是后来出现问题了)
			for (String id : beanDefinitionRegistry.getDefinitionIds()) {
				if (isSingleton(id) && isInstance(id)) {
					getInstance(id);
				}
			}

			// 处理静态依赖
			for (Class<?> clazz : getContextClassesLoader()) {
				for (Ioc ioc : Ioc.forClass(clazz)) {
					ioc.getDependence().process(null, null, this);
					ioc.getInit().process(null, null, this);
				}
			}
		}
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	public void destroy() throws Throwable {
		synchronized (this) {
			singletonBeanRegistry.destroyAll();
			for (Class<?> clazz : getContextClassesLoader()) {
				for (Ioc ioc : Ioc.forClass(clazz)) {
					ioc.getDestroy().process(null, null, this);
				}
			}
			initialized = false;
		}
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
			return ProxyUtils.getFactory().getUserClass(instance.getClass());
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

		public Object create(Class[] parameterTypes, Object[] params) throws BeansException {
			throw new NotSupportedException(getId());
		}

		public void dependence(Object instance) throws BeansException {
			if (instance != null) {
				for (Ioc ioc : Ioc.forClass(instance.getClass())) {
					ioc.getDependence().process(this, instance, DefaultBeanFactory.this);
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

	@Override
	protected final NoArgsInstanceFactory getTargetInstanceFactory() {
		return this;
	}
}
