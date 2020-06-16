package scw.beans;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import scw.beans.annotation.Bean;
import scw.beans.builder.BeanBuilder;
import scw.beans.event.BeanLifeCycleEvent;
import scw.beans.event.BeanLifeCycleEvent.Step;
import scw.beans.ioc.Ioc;
import scw.core.utils.StringUtils;
import scw.value.property.PropertyFactory;

public abstract class AbstractBeanDefinition implements BeanDefinition {
	protected final BeanFactory beanFactory;
	protected final PropertyFactory propertyFactory;
	protected final Class<?> targetClass;

	public AbstractBeanDefinition(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass) {
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
		this.targetClass = targetClass;
	}

	protected abstract BeanBuilder getBeanBuiler();

	public void dependence(Object instance) throws Exception {
		beanFactory.getEventDispatcher()
				.publishEvent(new BeanLifeCycleEvent(instance, beanFactory, propertyFactory, Step.BEFORE_DEPENDENCE));
		if (instance != null) {
			Ioc ioc = Ioc.forClass(instance.getClass());
			ioc.getDependence().process(instance, beanFactory, propertyFactory, false);
		}
		getBeanBuiler().dependence(instance);
		beanFactory.getEventDispatcher()
				.publishEvent(new BeanLifeCycleEvent(instance, beanFactory, propertyFactory, Step.AFTER_DEPENDENCE));
	}

	public void init(Object instance) throws Exception {
		beanFactory.getEventDispatcher()
				.publishEvent(new BeanLifeCycleEvent(instance, beanFactory, propertyFactory, Step.BEFORE_INIT));
		if (instance != null) {
			Ioc ioc = Ioc.forClass(instance.getClass());
			ioc.getInit().process(instance, beanFactory, propertyFactory, false);
		}
		getBeanBuiler().init(instance);
		beanFactory.getEventDispatcher()
				.publishEvent(new BeanLifeCycleEvent(instance, beanFactory, propertyFactory, Step.AFTER_INIT));
	}

	public void destroy(Object instance) throws Exception {
		beanFactory.getEventDispatcher()
				.publishEvent(new BeanLifeCycleEvent(instance, beanFactory, propertyFactory, Step.BEFORE_DESTROY));
		if (instance != null) {
			Ioc ioc = Ioc.forClass(instance.getClass());
			ioc.getDestroy().process(instance, beanFactory, propertyFactory, false);
		}

		getBeanBuiler().destroy(instance);
		beanFactory.getEventDispatcher()
				.publishEvent(new BeanLifeCycleEvent(instance, beanFactory, propertyFactory, Step.AFTER_DESTROY));
	}

	public Class<? extends Object> getTargetClass() {
		return getBeanBuiler().getTargetClass();
	}

	public boolean isInstance() {
		return getBeanBuiler().isInstance();
	}

	public Object create() throws Exception {
		return getBeanBuiler().create();
	}

	public Object create(Object... params) throws Exception {
		return getBeanBuiler().create(params);
	}

	public Object create(Class<?>[] parameterTypes, Object... params) throws Exception {
		return getBeanBuiler().create(parameterTypes, params);
	}

	public String getId() {
		Bean bean = getAnnotatedElement().getAnnotation(Bean.class);
		if (bean == null) {
			return targetClass.getName();
		}

		return StringUtils.isEmpty(bean.id()) ? targetClass.getName() : bean.id();
	}

	public Collection<String> getNames() {
		Bean bean = getAnnotatedElement().getAnnotation(Bean.class);
		if (bean == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(bean.value());
	}

	public boolean isSingleton() {
		Bean bean = getAnnotatedElement().getAnnotation(Bean.class);
		if (bean == null) {
			return true;
		}
		return bean.singleton();
	}

	public AnnotatedElement getAnnotatedElement() {
		return targetClass;
	}

}
