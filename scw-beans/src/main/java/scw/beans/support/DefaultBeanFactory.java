package scw.beans.support;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;

import scw.aop.Aop;
import scw.aop.DefaultAop;
import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionRegistry;
import scw.beans.BeanFactory;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeanLifeCycleEvent;
import scw.beans.BeanUtils;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.ioc.Ioc;
import scw.context.ClassesLoader;
import scw.context.ConfigurableClassesLoader;
import scw.context.support.DefaultContextLoader;
import scw.context.support.LifecycleAuxiliary;
import scw.core.parameter.ConstructorParameterDescriptorsIterator;
import scw.core.parameter.ParameterDescriptors;
import scw.core.utils.ClassUtils;
import scw.env.Environment;
import scw.env.support.DefaultEnvironment;
import scw.event.BasicEventDispatcher;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.instance.InstanceFactory;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;
import scw.lang.AlreadyExistsException;
import scw.lang.NotSupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultBeanFactory extends LifecycleAuxiliary implements
		ConfigurableBeanFactory {
	private static Logger logger = LoggerUtils
			.getLogger(DefaultBeanFactory.class);
	private final DefaultBasicEventDispatcher<BeanLifeCycleEvent> beanLifeCycleEventDispatcher = new DefaultBasicEventDispatcher<BeanLifeCycleEvent>(
			true);

	private volatile LinkedHashMap<String, Object> singletonMap = new LinkedHashMap<String, Object>();
	private final DefaultEnvironment environment = new DefaultEnvironment(true) {
		public ClassLoader getClassLoader() {
			return DefaultBeanFactory.this.getClassLoader();
		};
	};

	private final DefaultContextLoader contextLoader = new DefaultContextLoader(
			environment, this);
	private final BeanDefinitionRegistry beanDefinitionRegistry = new LazyBeanDefinitionRegsitry(
			this);
	private ClassLoader classLoader;
	
	private final Aop aop = new DefaultAop(this) {
		public boolean isProxy(Object instance) {
			if (instance == null) {
				return false;
			}

			return BeanUtils.getRuntimeBean(instance) != null;
		};
	};

	public DefaultBeanFactory() {
		registerSingletion(BeanFactory.class.getName(), this);
		registerAlias(BeanFactory.class.getName(),
				InstanceFactory.class.getName());
		registerAlias(BeanFactory.class.getName(),
				NoArgsInstanceFactory.class.getName());

		registerSingletion(Environment.class.getName(), getEnvironment());
	}

	public DefaultContextLoader getContextLoader() {
		return contextLoader;
	}

	public ClassLoader getClassLoader() {
		return classLoader == null ? ClassUtils.getDefaultClassLoader()
				: classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public Aop getAop() {
		return aop;
	}

	public BasicEventDispatcher<BeanLifeCycleEvent> getBeanLifeCycleEventDispatcher() {
		return beanLifeCycleEventDispatcher;
	}

	public final boolean containsDefinition(String beanName) {
		return beanDefinitionRegistry.containsDefinition(beanName);
	}

	public final boolean isAlias(String name) {
		return beanDefinitionRegistry.isAlias(name);
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
	
	public Object getRegisterDefinitionMutex() {
		return beanDefinitionRegistry.getRegisterDefinitionMutex();
	}

	public final BeanDefinition registerDefinition(String name,
			BeanDefinition beanDefinition) {
		return beanDefinitionRegistry.registerDefinition(name, beanDefinition);
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

	public final void registerSingletion(String name, Object instance) {
		synchronized (singletonMap) {
			Object registred = singletonMap.putIfAbsent(name, instance);
			if (registred != null) {
				throw new AlreadyExistsException("已经存在此单例了:" + registred);
			}
		}

		SingletionBeanDefinition definition = new SingletionBeanDefinition(
				name, instance);
		registerDefinition(name, definition);
		try {
			definition.dependence(instance);
		} catch (Exception e) {
			logger.error(e, "register singletion error");
		}
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
			Object... params) {
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
		if (singletonMap.containsKey(name)) {
			return true;
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return false;
		}

		return singletonMap.containsKey(definition.getId())
				|| definition.isSingleton();
	}

	public void init(BeanDefinition definition, Object instance) {
		definition.dependence(instance);
		definition.init(instance);
	}

	public <T> T getInstance(Class<T> clazz) {
		return getInstance(clazz.getName());
	}

	public <T> T getInstance(String name) {
		Object object = singletonMap.get(name);
		if (object != null) {
			return (T) object;
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return null;
		}

		boolean created = false;
		if (definition.isSingleton()) {
			object = singletonMap.get(definition.getId());
			if (object == null) {
				synchronized (singletonMap) {
					object = singletonMap.get(definition.getId());
					if (object == null) {
						object = definition.create();
						created = true;
						if (definition.isSingleton()) {
							singletonMap.put(definition.getId(), object);
						}
					}
				}
			}
		} else {
			object = definition.create();
		}
		
		if(created){
			init(definition, object);
		}
		return (T) object;
	}

	public boolean isInstance(String name) {
		if (singletonMap.containsKey(name)) {
			return true;
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return false;
		}

		return singletonMap.containsKey(definition.getId())
				|| definition.isInstance();
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
			Object... params) {
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
	public void beforeInit() throws Throwable {
		postProcessBeanFactory(new MethodBeanFactoryPostProcessor());
		postProcessBeanFactory(new ServiceBeanFactoryPostProcessor());

		environment.loaderServices(this);
		
		for(BeanDefinition definition : getServiceLoader(BeanDefinition.class)){
			if(containsDefinition(definition.getId())){
				logger.debug("ignore definition {}", definition);
				continue;
			}
			registerDefinition(definition.getId(), definition);
		}

		for (BeanFactoryPostProcessor processor : getServiceLoader(BeanFactoryPostProcessor.class)) {
			postProcessBeanFactory(processor);
		}
		super.beforeInit();
		postProcessBeanFactory(new ExecutorBeanFactoryPostProcessor());
	}

	@Override
	public void afterInit() throws Throwable {
		for (Class<?> clazz : getContextClassesLoader()) {
			for (Ioc ioc : Ioc.forClass(clazz)) {
				ioc.getDependence().process(null, null, this);
				ioc.getInit().process(null, null, this);
			}
		}
		super.afterInit();
	}

	@Override
	public void beforeDestroy() throws Throwable {
		for (Class<?> clazz : getContextClassesLoader()) {
			for (Ioc ioc : Ioc.forClass(clazz)) {
				ioc.getDestroy().process(null, null, this);
			}
		}
		super.beforeDestroy();
	}

	@Override
	public void afterDestroy() throws Throwable {
		synchronized (singletonMap) {
			BeanUtils.destroy(this, singletonMap, logger);
			singletonMap.clear();
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
			return getAop().getUserClass(instance.getClass());
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

		public Object create(Class[] parameterTypes, Object... params)
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
