package io.basc.framework.factory.support;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanPostProcessor;
import io.basc.framework.beans.SingletonFactory;
import io.basc.framework.beans.SingletonRegistry;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.event.EventListener;
import io.basc.framework.factory.BeanLifecycleEvent;
import io.basc.framework.factory.BeanLifecycleEvent.Step;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.Destroy;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Creator;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Return;
import io.basc.framework.util.StandardReturn;
import io.basc.framework.util.StringUtils;

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

		@Override
		public String toString() {
			return "setp=" + step + ", bean=" + instance;
		}
	}

	private static Logger logger = LoggerFactory.getLogger(DefaultSingletonRegistry.class);
	private final ConfigurableServices<BeanPostProcessor> beanPostProcessors = new ConfigurableServices<BeanPostProcessor>(
			BeanPostProcessor.class);
	private volatile Map<String, SingletonObject<?>> singletionMap = new LinkedHashMap<String, SingletonObject<?>>();
	private SingletonFactory parentSingletonFactory;

	@Nullable
	public SingletonFactory getParentSingletonFactory() {
		return parentSingletonFactory;
	}

	public void setParentSingletonFactory(SingletonFactory parentSingletonFactory) {
		this.parentSingletonFactory = parentSingletonFactory;
	}

	public void registerSingleton(String beanName, Object singletonObject) {
		synchronized (singletionMap) {
			Object old = singletionMap.get(beanName);
			if (old != null) {
				throw new IllegalStateException("Could not register object [" + singletonObject + "] under bean name '"
						+ beanName + "': there is already object [" + old + "] bound");
			}

			BeanDefinition definition;
			SingletonObject<Object> singleton;
			if (containsDefinition(beanName)) {
				// 如果定义已经存在
				definition = getDefinition(beanName);
				singleton = new SingletonObject<Object>(singletonObject);
				;
			} else {
				// 如果定义不存在，创建一个空定义
				definition = new EmptyBeanDefinition(TypeDescriptor.forObject(singletonObject), beanName);
				registerDefinition(definition);
				singleton = new SingletonObject<Object>(singletonObject);
				singleton.setStep(Step.AFTER_INIT);
			}

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
		if (containsSingleton(name)) {
			// 单例已经存在，不可以再注册定义了
			throw new FactoryException("Single instance[" + name + "] already exists, unable to register definition["
					+ beanDefinition + "]");
		}
		return super.registerDefinition(name, beanDefinition);
	}

	public Object getSingleton(String beanName) {
		return getSingleton(beanName, getParentSingletonFactory());
	}

	public Object getSingleton(String beanName, SingletonFactory parent) {
		SingletonObject<?> instance = singletionMap.get(beanName);
		if (instance != null) {
			return instance.get();
		}

		BeanDefinition definition = getDefinition(beanName);
		if (definition == null) {
			return parent == null ? null : parent.getSingleton(beanName);
		}

		SingletonObject<?> singletonObject = singletionMap.get(definition.getId());
		if (singletonObject == null) {
			return parent == null ? null : parent.getSingleton(beanName);
		}
		return singletonObject.get();
	}

	public boolean containsSingleton(String beanName) {
		if (singletionMap.containsKey(beanName)) {
			return true;
		}

		// 存在单例必然存在定义
		if (!containsDefinition(beanName)) {
			return false;
		}

		BeanDefinition definition = getDefinition(beanName);
		if (definition == null) {
			return false;
		}

		return singletionMap.containsKey(definition.getId());
	}

	public boolean containsSingleton(String beanName, SingletonFactory parent) {
		if (parent != null && parent.containsSingleton(beanName)) {
			return true;
		}

		if (singletionMap.containsKey(beanName)) {
			return true;
		}

		// 存在单例必然存在定义
		if (!containsDefinition(beanName)) {
			return false;
		}

		BeanDefinition definition = getDefinition(beanName);
		if (definition == null) {
			return false;
		}

		return singletionMap.containsKey(definition.getId());
	}

	public String[] getSingletonNames() {
		return getSingletonNames(getParentSingletonFactory());
	}

	public String[] getSingletonNames(SingletonFactory parent) {
		synchronized (singletionMap) {
			String[] array = StringUtils.toStringArray(singletionMap.keySet());
			return (array != null || parent == null) ? array
					: ArrayUtils.merge(parentSingletonFactory.getSingletonNames(), array);
		}
	}

	public Object getSingletonMutex() {
		return singletionMap;
	}

	@Override
	public <T, E extends Throwable> Return<T> getSingleton(String name, Creator<? extends T, ? extends E> creater)
			throws E {
		return getSingleton(name, creater, true);
	}

	@SuppressWarnings("unchecked")
	public <T, E extends Throwable> Return<T> getSingleton(String name, Creator<? extends T, ? extends E> creater,
			boolean postProcessBean) throws E {
		Object object = getSingleton(name);
		boolean created = false;
		if (object == null) {
			synchronized (singletionMap) {
				object = getSingleton(name);
				if (object == null) {
					object = creater.create();
					registerSingleton(name, object);
					created = true;
				}
			}
		}

		if (created && postProcessBean) {
			processPostBean(object, getDefinition(name));
		}
		return new StandardReturn<T>(created, 0, null, (T) object);
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
		synchronized (singletionMap) {
			String[] names = getSingletonNames();
			for (int i = names.length - 1; i >= 0; i--) {
				BeanDefinition definition = getDefinition(names[i]);
				if (definition != null) {
					Object instance = getSingleton(definition.getId());
					if (instance != null) {
						destroy(instance, definition);
					}
				}
				singletionMap.remove(names[i]);
			}
		}
	}

	@Override
	public Registration registerListener(EventListener<BeanLifecycleEvent> eventListener) {
		Registration eventRegistration = super.registerListener(eventListener);
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
					eventListener.onEvent(new BeanLifecycleEvent(definition, singletonObject.get(), step));
				} catch (Throwable e) {
					logger.error(e, "Register listener after on bean[{}]", definition.getId());
				} finally {
					singletonObject.step = step;
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

		if (!definition.isSingleton()) {
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

		if (!definition.isSingleton()) {
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

		if (!definition.isSingleton()) {
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
