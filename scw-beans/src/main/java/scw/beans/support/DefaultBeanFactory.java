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
import scw.instance.ServiceLoader;
import scw.instance.factory.InstanceFactory;
import scw.instance.factory.NoArgsInstanceFactory;
import scw.lang.AlreadyExistsException;
import scw.lang.NotSupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class DefaultBeanFactory extends LifecycleAuxiliary implements ConfigurableBeanFactory{
	private static Logger logger = LoggerUtils
			.getLogger(DefaultBeanFactory.class);
	private volatile LinkedHashMap<String, Object> singletonMap = new LinkedHashMap<String, Object>();
	private final DefaultBasicEventDispatcher<BeanLifeCycleEvent> beanLifeCycleEventDispatcher = new DefaultBasicEventDispatcher<BeanLifeCycleEvent>(
			true);
	private ClassLoader classLoader;
	private final DefaultEnvironment environment = new DefaultEnvironment(true){
		public ClassLoader getClassLoader() {
			return DefaultBeanFactory.this.getClassLoader();
		};
	};
	
	private final DefaultContextLoader contextLoader = new DefaultContextLoader(environment, this);
	private final BeanDefinitionRegistry beanDefinitionRegistry = new LazyBeanDefinitionRegsitry(this);
	
	public DefaultBeanFactory() {
		registerSingletion(BeanFactory.class.getName(), this);
		registerAlias(BeanFactory.class.getName(), InstanceFactory.class.getName());
		registerAlias(BeanFactory.class.getName(), NoArgsInstanceFactory.class.getName());
		
		registerSingletion(Environment.class.getName(), getEnvironment());
	}
	
	public DefaultContextLoader getContextLoader() {
		return contextLoader;
	}

	public final boolean containsBeanDefinition(String beanName) {
		return beanDefinitionRegistry.containsBeanDefinition(beanName);
	}
	
	public final boolean isAlias(String name) {
		return beanDefinitionRegistry.isAlias(name);
	}
	
	public final String[] getAliases(String name) {
		return beanDefinitionRegistry.getAliases(name);
	}
	
	public final BeanDefinition getBeanDefinition(Class<?> clazz) {
		return beanDefinitionRegistry.getBeanDefinition(clazz);
	}
	
	public final BeanDefinition getBeanDefinition(String name) {
		return beanDefinitionRegistry.getBeanDefinition(name);
	}
	
	public final String[] getBeanDefinitionNames() {
		return beanDefinitionRegistry.getBeanDefinitionNames();
	}
	
	public final void registerAlias(String name, String alias) {
		beanDefinitionRegistry.registerAlias(name, alias);
	}
	
	public final BeanDefinition registerBeanDefinition(String name,
			BeanDefinition beanDefinition) {
		return beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
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
			if(registred != null){
				throw new AlreadyExistsException("已经存在此单例了:" + registred);
			}
		}
		
		SingletionBeanDefinition definition = new SingletionBeanDefinition(name, instance);
		registerBeanDefinition(name, definition);
		try {
			definition.dependence(instance);
		} catch (Exception e) {
			logger.error(e, "register singletion error");
		}
	}

	public ClassLoader getClassLoader() {
		return classLoader == null ? ClassUtils.getDefaultClassLoader()
				: classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public final BasicEventDispatcher<BeanLifeCycleEvent> getBeanLifeCycleEventDispatcher() {
		return beanLifeCycleEventDispatcher;
	}

	public boolean isSingleton(Class<?> clazz) {
		return isSingleton(clazz.getName());
	}

	public boolean isSingleton(String name) {
		if (singletonMap.containsKey(name)) {
			return true;
		}

		BeanDefinition definition = beanDefinitionRegistry.getBeanDefinition(name);
		if (definition == null) {
			return false;
		}

		return singletonMap.containsKey(definition.getId())
				|| definition.isSingleton();
	}

	public <T> T getInstance(Class<? extends T> clazz) {
		return getInstance(clazz.getName());
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		Object object = singletonMap.get(name);
		if (object != null) {
			return (T) object;
		}

		BeanDefinition definition = beanDefinitionRegistry.getBeanDefinition(name);
		if (definition == null) {
			return null;
		}

		if (definition.isSingleton()) {
			object = singletonMap.get(definition.getId());
			if (object == null) {
				synchronized (singletonMap) {
					object = singletonMap.get(definition.getId());
					if (object == null) {
						object = createInternal(definition);
					}
				}
			}
		} else {
			object = createInternal(definition);
		}
		return (T) object;
	}

	protected void init(long createTime, BeanDefinition definition,
			Object object) throws Throwable {
		if (definition.isSingleton()) {
			singletonMap.put(definition.getId(), object);
		}
		definition.dependence(object);
		definition.init(object);

		if (logger.isDebugEnabled()) {
			logger.debug(
					"create instance [{}] by definition [{}] use time {}ms",
					object, definition.getId(), System.currentTimeMillis()
							- createTime);
		}
	}

	private Object createInternal(BeanDefinition definition) {
		long t = System.currentTimeMillis();
		Object obj;
		try {
			obj = definition.create();
			init(t, definition, obj);
		} catch (Throwable e) {
			throw new BeansException(definition.getId(), e);
		}
		return obj;
	}

	public boolean isInstance(String name) {
		if (singletonMap.containsKey(name)) {
			return true;
		}

		BeanDefinition definition = beanDefinitionRegistry.getBeanDefinition(name);
		if (definition == null) {
			return false;
		}

		return singletonMap.containsKey(definition.getId())
				|| definition.isInstance();
	}

	public boolean isInstance(Class<?> clazz) {
		return isInstance(clazz.getName());
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name, Object... params) {
		Object obj = singletonMap.get(name);
		if (obj != null) {
			return (T) obj;
		}

		BeanDefinition definition = beanDefinitionRegistry.getBeanDefinition(name);
		if (definition == null) {
			return null;
		}

		if (definition.isSingleton()) {
			obj = singletonMap.get(definition.getId());
			if (obj == null) {
				synchronized (singletonMap) {
					obj = singletonMap.get(definition.getId());
					if (obj == null) {
						obj = createInternal(definition, params);
					}
				}
			}
		} else {
			obj = createInternal(definition, params);
		}
		return (T) obj;
	}

	private Object createInternal(BeanDefinition definition, Object... params) {
		long t = System.currentTimeMillis();
		Object obj;
		try {
			obj = definition.create(params);
			init(t, definition, obj);
		} catch (Throwable e) {
			throw new BeansException(definition.getId(), e);
		}
		return obj;
	}

	public <T> T getInstance(Class<? extends T> type, Object... params) {
		return getInstance(type.getName(), params);
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name, Class<?>[] parameterTypes,
			Object... params) {
		Object obj = singletonMap.get(name);
		if (obj != null) {
			return (T) obj;
		}

		BeanDefinition definition = beanDefinitionRegistry.getBeanDefinition(name);
		if (definition == null) {
			return null;
		}

		if (definition.isSingleton()) {
			obj = singletonMap.get(definition.getId());
			if (obj == null) {
				synchronized (singletonMap) {
					obj = singletonMap.get(definition.getId());
					if (obj == null) {
						obj = createInternal(definition, parameterTypes, params);
					}
				}
			}
		} else {
			obj = createInternal(definition, parameterTypes, params);
		}
		return (T) obj;
	}

	private Object createInternal(BeanDefinition definition,
			Class<?>[] parameterTypes, Object... params) {
		long t = System.currentTimeMillis();
		Object obj;
		try {
			obj = definition.create(parameterTypes, params);
			init(t, definition, obj);
		} catch (Throwable e) {
			throw new BeansException(definition.getId(), e);
		}
		return obj;
	}

	public <T> T getInstance(Class<? extends T> type,
			Class<?>[] parameterTypes, Object... params) {
		return getInstance(type.getName(), parameterTypes, params);
	}

	private final Aop aop = new DefaultAop(this) {
		public boolean isProxy(Object instance) {
			if (instance == null) {
				return false;
			}

			return BeanUtils.getRuntimeBean(instance) != null;
		};
	};

	public Aop getAop() {
		return aop;
	}
	
	public void postProcessBeanFactory(BeanFactoryPostProcessor beanFactoryPostProcessor){
		beanFactoryPostProcessor.postProcessBeanFactory(this);
	}
	
	@Override
	public void beforeInit() throws Throwable {
		postProcessBeanFactory(new MethodBeanFactoryPostProcessor());
		postProcessBeanFactory(new ServiceBeanFactoryPostProcessor());
		
		environment.config(this);
		
		for(BeanFactoryPostProcessor processor : getServiceLoader(BeanFactoryPostProcessor.class)){
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

		public Class<?> getTargetClass() {
			return getAop().getUserClass(instance.getClass());
		}

		public boolean isSingleton() {
			return true;
		}

		public boolean isInstance() {
			return true;
		}

		public Object create() throws Exception {
			return instance;
		}

		public Object create(Object... params) throws Exception {
			throw new NotSupportedException(getId());
		}

		public Object create(Class<?>[] parameterTypes, Object... params) throws Exception {
			throw new NotSupportedException(getId());
		}

		public void dependence(Object instance) throws Exception {
			if (instance != null) {
				for (Ioc ioc : Ioc.forClass(instance.getClass())) {
					ioc.getDependence().process(this, instance, DefaultBeanFactory.this);
				}
			}
		}

		public void init(Object bean) throws Exception {
		}

		public void destroy(Object bean) throws Exception {
		}

		public AnnotatedElement getAnnotatedElement() {
			return getTargetClass();
		}

		public Iterator<ParameterDescriptors> iterator() {
			return new ConstructorParameterDescriptorsIterator(getTargetClass());
		}
	}
}
