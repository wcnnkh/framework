package io.basc.framework.factory.support;

import java.util.Collection;

import com.sun.istack.internal.Nullable;

import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.event.support.SimpleEventDispatcher;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeanDefinitionAware;
import io.basc.framework.factory.BeanDefinitionFactory;
import io.basc.framework.factory.BeanLifeCycleManager;
import io.basc.framework.factory.BeanPostProcessor;
import io.basc.framework.factory.BeanlifeCycleEvent;
import io.basc.framework.factory.BeanlifeCycleEvent.Step;
import io.basc.framework.factory.ConfigurableBeanResolver;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.Destroy;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.factory.Init;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

public class DefaultBeanLifeCycleManager extends DefaultBeanDefinitionRegistry implements BeanLifeCycleManager {
	private static Logger logger = LoggerFactory.getLogger(DefaultBeanLifeCycleManager.class);
	private final ConfigurableServices<BeanPostProcessor> dependenceProcessors = new ConfigurableServices<BeanPostProcessor>(
			BeanPostProcessor.class);
	private final ConfigurableServices<BeanPostProcessor> destroyProcessors = new ConfigurableServices<BeanPostProcessor>(
			BeanPostProcessor.class);
	private final EventDispatcher<BeanlifeCycleEvent> eventDispatcher = new SimpleEventDispatcher<>();
	private final ConfigurableServices<BeanPostProcessor> initProcessors = new ConfigurableServices<BeanPostProcessor>(
			BeanPostProcessor.class);
	private final ConfigurableBeanResolver beanResolver = new ConfigurableBeanResolver();

	public DefaultBeanLifeCycleManager() {
		this(null);
	}

	public DefaultBeanLifeCycleManager(@Nullable BeanDefinitionFactory parentBeanDefinitionFactory) {
		super(parentBeanDefinitionFactory);
	}

	protected void _dependence(Object instance, BeanDefinition definition) throws FactoryException {
		if (instance instanceof BeanDefinitionAware) {
			((BeanDefinitionAware) instance).setBeanDefinition(definition);
		}
	}

	protected void _destroy(Object instance, BeanDefinition definition) throws FactoryException {
		if (instance == null) {
			return;
		}

		if (instance instanceof Destroy) {
			try {
				((Destroy) instance).destroy();
			} catch (Throwable e) {
				if (e instanceof FactoryException) {
					throw (FactoryException) e;
				}
				throw new FactoryException(definition.getId(), e);
			}
		}
	}

	protected void _init(Object instance, BeanDefinition definition) throws FactoryException {
		if (instance == null) {
			return;
		}

		if (instance instanceof Init) {
			try {
				((Init) instance).init();
			} catch (Throwable e) {
				if (e instanceof FactoryException) {
					throw (FactoryException) e;
				}
				throw new FactoryException(definition.getId(), e);
			}
		}
	}

	@Override
	public void dependence(Object instance, BeanDefinition definition) throws FactoryException {
		if (definition == null || definition instanceof EmptyBeanDefinition) {
			return;
		}

		RuntimeBean runtimeBean = RuntimeBean.getRuntimeBean(instance);
		if (runtimeBean != null && !runtimeBean._dependence()) {
			return;
		}

		try {
			publishEvent(new BeanlifeCycleEvent(definition, instance, Step.BEFORE_DEPENDENCE));
		} finally {
			if (beanResolver != null) {
				Collection<BeanPostProcessor> processors = beanResolver
						.resolveDependenceProcessors(definition.getTypeDescriptor());
				if (processors != null) {
					for (BeanPostProcessor processor : processors) {
						processor.processPostBean(instance, definition);
					}
				}
			}
			definition.init(instance);
			for (BeanPostProcessor processor : dependenceProcessors) {
				processor.processPostBean(instance, definition);
			}
			_dependence(instance, definition);
			publishEvent(new BeanlifeCycleEvent(definition, instance, Step.AFTER_DEPENDENCE));
		}
	}

	@Override
	public void destroy(Object instance, BeanDefinition definition) throws FactoryException {
		if (definition == null || definition instanceof EmptyBeanDefinition) {
			return;
		}

		RuntimeBean runtimeBean = RuntimeBean.getRuntimeBean(instance);
		if (runtimeBean != null && !runtimeBean._destroy()) {
			return;
		}

		if (logger.isTraceEnabled()) {
			logger.trace("destroy {}", definition);
		}

		try {
			publishEvent(new BeanlifeCycleEvent(definition, instance, Step.BEFORE_DESTROY));
		} finally {
			_destroy(instance, definition);
			for (BeanPostProcessor processor : destroyProcessors) {
				processor.processPostBean(instance, definition);
			}
			definition.destroy(instance);
			if (beanResolver != null) {
				Collection<BeanPostProcessor> processors = beanResolver
						.resolveDestroyProcessors(definition.getTypeDescriptor());
				if (processors != null) {
					for (BeanPostProcessor processor : processors) {
						processor.processPostBean(instance, definition);
					}
				}
			}
			publishEvent(new BeanlifeCycleEvent(definition, instance, Step.AFTER_DESTROY));
		}
	}

	public ConfigurableServices<BeanPostProcessor> getDependenceProcessors() {
		return dependenceProcessors;
	}

	public ConfigurableServices<BeanPostProcessor> getDestroyProcessors() {
		return destroyProcessors;
	}

	public ConfigurableServices<BeanPostProcessor> getInitProcessors() {
		return initProcessors;
	}

	public ConfigurableBeanResolver getBeanResolver() {
		return beanResolver;
	}

	@Override
	public void init(Object instance, BeanDefinition definition) throws FactoryException {
		if (definition == null || definition instanceof EmptyBeanDefinition) {
			return;
		}

		RuntimeBean runtimeBean = RuntimeBean.getRuntimeBean(instance);
		if (runtimeBean != null && !runtimeBean._init()) {
			return;
		}

		try {
			publishEvent(new BeanlifeCycleEvent(definition, instance, Step.BEFORE_INIT));
		} finally {
			if (beanResolver != null) {
				Collection<BeanPostProcessor> processors = beanResolver
						.resolveInitProcessors(definition.getTypeDescriptor());
				if (processors != null) {
					for (BeanPostProcessor processor : processors) {
						processor.processPostBean(instance, definition);
					}
				}
			}
			definition.init(instance);
			for (BeanPostProcessor processor : initProcessors) {
				processor.processPostBean(instance, definition);
			}
			_init(instance, definition);
			publishEvent(new BeanlifeCycleEvent(definition, instance, Step.AFTER_INIT));
		}
	}

	@Override
	public void publishEvent(BeanlifeCycleEvent event) {
		eventDispatcher.publishEvent(event);
	}

	@Override
	public EventRegistration registerListener(EventListener<BeanlifeCycleEvent> eventListener) {
		return eventDispatcher.registerListener(eventListener);
	}
}
