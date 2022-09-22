package io.basc.framework.factory.support;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeanDefinitionFactory;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.BeanPostProcessor;
import io.basc.framework.factory.BeanlifeCycleEvent;
import io.basc.framework.factory.BeanlifeCycleEvent.Step;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.Destroy;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.factory.SingletonRegistry;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.DefaultStatus;
import io.basc.framework.util.Status;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.stream.CallableProcessor;

public class DefaultSingletonRegistry extends DefaultBeanLifeCycleManager
		implements SingletonRegistry, BeanPostProcessor, Destroy {
	private static class SingletonObject<T> implements Supplier<T> {
		private Step step = Step.BEFORE_DEPENDENCE;
		private final T instance;

		public SingletonObject(T instance) {
			this.instance = instance;
		}

		@Override
		public T get() {
			return instance;
		}

		public void setStep(Step step) {
			this.step = step;
		}
	}

	private static Logger logger = LoggerFactory.getLogger(DefaultSingletonRegistry.class);
	private final ConfigurableServices<BeanPostProcessor> beanPostProcessors = new ConfigurableServices<BeanPostProcessor>(
			BeanPostProcessor.class);
	private volatile Map<String, SingletonObject<?>> singletionMap = new LinkedHashMap<String, SingletonObject<?>>();

	public DefaultSingletonRegistry() {
		this(null);
	}

	public DefaultSingletonRegistry(BeanFactory beanFactory) {
		this((BeanDefinitionFactory) beanFactory);
		getBeanResolver().setDefaultResolver(beanFactory.getBeanResolver());
	}

	public DefaultSingletonRegistry(@Nullable BeanDefinitionFactory parentBeanDefinitionFactory) {
		super(parentBeanDefinitionFactory);
	}

	public void registerSingleton(String beanName, Object singletonObject) {
		synchronized (singletionMap) {
			Object old = singletionMap.get(beanName);
			if (old != null) {
				throw new IllegalStateException("Could not register object [" + singletonObject + "] under bean name '"
						+ beanName + "': there is already object [" + old + "] bound");
			}

			BeanDefinition definition;
			if (containsDefinition(beanName) || (getParentBeanDefinitionFactory() != null
					&& getParentBeanDefinitionFactory().containsDefinition(beanName))) {
				// 如果定义已经存在
				definition = getDefinition(beanName);
			} else {
				// 如果定义不存在，创建一个空定义
				definition = new EmptyBeanDefinition(TypeDescriptor.forObject(singletonObject), beanName);
				registerDefinition(definition);
			}

			SingletonObject<Object> singleton = new SingletonObject<Object>(singletonObject);
			old = singletionMap.get(definition.getId());
			if (old != null) {
				throw new IllegalStateException("Could not register object [" + singletonObject + "] under bean name '"
						+ beanName + "': there is already object [" + old + "] bound");
			}
			singletionMap.put(definition.getId(), singleton);
		}
	}

	@Override
	public BeanDefinition registerDefinition(String name, BeanDefinition beanDefinition) {
		BeanDefinition definition = getDefinition(name);
		if (definition != null && definition instanceof EmptyBeanDefinition) {
			removeDefinition(name);
		} else if (containsSingleton(name)) {
			// 单例已经存在，不可以再注册定义了
			throw new FactoryException("Single instance[" + name + "] already exists, unable to register definition["
					+ beanDefinition + "]");
		}
		return super.registerDefinition(name, beanDefinition);
	}

	public Object getSingleton(String beanName) {
		SingletonObject<?> instance = singletionMap.get(beanName);
		if (instance != null) {
			return instance.get();
		}

		BeanDefinition definition = getDefinition(beanName);
		if (definition == null) {
			return null;
		}

		SingletonObject<?> singletonObject = singletionMap.get(definition.getId());
		if (singletonObject == null) {
			return null;
		}
		return singletonObject.get();
	}

	public boolean containsSingleton(String beanName) {
		if (singletionMap.containsKey(beanName)) {
			return true;
		}

		BeanDefinition definition = getDefinition(beanName);
		if (definition == null) {
			return false;
		}

		return singletionMap.containsKey(definition.getId());
	}

	public String[] getSingletonNames() {
		synchronized (singletionMap) {
			return StringUtils.toStringArray(singletionMap.keySet());
		}
	}

	public void removeSingleton(String name) {
		synchronized (singletionMap) {
			BeanDefinition definition = getDefinition(name);
			if (definition != null) {
				Object instance = getSingleton(definition.getId());
				if (instance != null) {
					destroy(instance, definition);
				}
			}
			singletionMap.remove(name);
		}
	}

	public Object getSingletonMutex() {
		return singletionMap;
	}

	@Override
	public <T, E extends Throwable> Status<T> getSingleton(String name, CallableProcessor<T, E> creater) throws E {
		return getSingleton(name, creater, true);
	}

	@SuppressWarnings("unchecked")
	public <T, E extends Throwable> Status<T> getSingleton(String name, CallableProcessor<T, E> creater,
			boolean postProcessBean) throws E {
		Object object = singletionMap.get(name);
		boolean created = false;
		if (object == null) {
			synchronized (singletionMap) {
				object = singletionMap.get(name);
				if (object == null) {
					object = creater.process();
					registerSingleton(name, object);
					created = true;
				}
			}
		}

		if (created && postProcessBean) {
			processPostBean(object, getDefinition(name));
		}
		return new DefaultStatus<T>(created, (T) object);
	}

	@Override
	public void processPostBean(Object bean, BeanDefinition definition) throws FactoryException {
		dependence(bean, definition);
		for (BeanPostProcessor processor : beanPostProcessors) {
			processor.processPostBean(bean, definition);
		}
		init(bean, definition);
	}

	@Override
	public void destroy() {
		String[] names = getSingletonNames();
		for (int i = names.length - 1; i >= 0; i--) {
			removeSingleton(names[i]);
		}
	}

	@Override
	public EventRegistration registerListener(EventListener<BeanlifeCycleEvent> eventListener) {
		EventRegistration eventRegistration = super.registerListener(eventListener);
		for (Entry<String, SingletonObject<?>> entry : singletionMap.entrySet()) {
			BeanDefinition definition = getDefinition(entry.getKey());
			if (definition == null) {
				continue;
			}

			SingletonObject<?> singletonObject = entry.getValue();
			for (Step step : Step.values()) {
				if (step.getOrder() >= singletonObject.step.getOrder()) {
					continue;
				}

				try {
					eventListener.onEvent(new BeanlifeCycleEvent(definition, singletonObject.get(), step));
				} catch (Throwable e) {
					logger.error(e, "Register listener after on bean[{}]", definition.getId());
				}
			}
		}
		return eventRegistration;
	}

	@Override
	public final void dependence(Object instance, BeanDefinition definition) throws FactoryException {
		if (definition == null) {
			return;
		}

		if (definition.isSingleton()) {
			super.dependence(instance, definition);
			return;
		}

		synchronized (singletionMap) {
			SingletonObject<?> singletonObject = singletionMap.get(definition.getId());
			if (singletonObject != null && singletonObject.step.getOrder() >= Step.AFTER_DEPENDENCE.getOrder()) {
				return;
			}

			if (singletonObject != null) {
				singletonObject.setStep(Step.BEFORE_DEPENDENCE);
			}

			try {
				super.dependence(instance, definition);
			} finally {
				if (singletonObject != null) {
					singletonObject.setStep(Step.AFTER_DEPENDENCE);
				}
			}
		}
	}

	@Override
	public final void init(Object instance, BeanDefinition definition) throws FactoryException {
		if (definition == null) {
			return;
		}

		if (definition.isSingleton()) {
			super.init(instance, definition);
			return;
		}

		synchronized (singletionMap) {
			SingletonObject<?> singletonObject = singletionMap.get(definition.getId());
			if (singletonObject != null && singletonObject.step.getOrder() >= Step.AFTER_INIT.getOrder()) {
				return;
			}

			if (singletonObject != null) {
				singletonObject.setStep(Step.BEFORE_INIT);
			}

			try {
				super.init(instance, definition);
			} finally {
				if (singletonObject != null) {
					singletonObject.setStep(Step.AFTER_INIT);
				}
			}
		}
	}

	@Override
	public final void destroy(Object instance, BeanDefinition definition) throws FactoryException {
		if (definition == null) {
			return;
		}

		if (definition.isSingleton()) {
			super.destroy(instance, definition);
			return;
		}

		synchronized (singletionMap) {
			SingletonObject<?> singletonObject = singletionMap.get(definition.getId());
			if (singletonObject != null && singletonObject.step.getOrder() >= Step.AFTER_DESTROY.getOrder()) {
				return;
			}

			if (singletonObject != null) {
				singletonObject.setStep(Step.BEFORE_DESTROY);
			}

			try {
				super.destroy(instance, definition);
			} finally {
				if (singletonObject != null) {
					singletonObject.setStep(Step.AFTER_DESTROY);
				}
			}
		}
	}
}
