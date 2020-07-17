package scw.beans;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import scw.aop.Filter;
import scw.aop.Proxy;
import scw.beans.annotation.Bean;
import scw.beans.builder.LoaderContext;
import scw.beans.event.BeanLifeCycleEvent;
import scw.beans.event.BeanLifeCycleEvent.Step;
import scw.beans.ioc.Ioc;
import scw.core.instance.AbstractInstanceBuilder;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.value.property.PropertyFactory;

public abstract class AbstractBeanDefinition extends AbstractInstanceBuilder<Object> implements BeanDefinition, Cloneable{
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	protected final BeanFactory beanFactory;
	protected final PropertyFactory propertyFactory;
	protected Ioc ioc = new Ioc();
	protected List<Filter> filters = new ArrayList<Filter>(4);
	private boolean isNew = true;

	public AbstractBeanDefinition(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass) {
		super(targetClass);
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
	}

	public AbstractBeanDefinition(LoaderContext loaderContext) {
		this(loaderContext.getBeanFactory(), loaderContext.getPropertyFactory(), loaderContext.getTargetClass());
	}

	public void dependence(Object instance) throws Exception {
		beanFactory.getEventDispatcher().publishEvent(
				new BeanLifeCycleEvent(this, instance, beanFactory, propertyFactory, Step.BEFORE_DEPENDENCE));
		if (instance != null) {
			Ioc ioc = Ioc.forClass(instance.getClass());
			ioc.getDependence().process(this, instance, beanFactory, propertyFactory, false);
			ioc.getDependence().process(this, instance, beanFactory, propertyFactory, false);
		}
		beanFactory.getEventDispatcher().publishEvent(
				new BeanLifeCycleEvent(this, instance, beanFactory, propertyFactory, Step.AFTER_DEPENDENCE));
	}

	public void init(Object instance) throws Exception {
		beanFactory.getEventDispatcher()
				.publishEvent(new BeanLifeCycleEvent(this, instance, beanFactory, propertyFactory, Step.BEFORE_INIT));
		if (instance != null) {
			ioc.getInit().process(this, instance, beanFactory, propertyFactory, false);

			Ioc ioc = Ioc.forClass(instance.getClass());
			ioc.getInit().process(this, instance, beanFactory, propertyFactory, false);
			BeanUtils.init(instance);
		}
		beanFactory.getEventDispatcher()
				.publishEvent(new BeanLifeCycleEvent(this, instance, beanFactory, propertyFactory, Step.AFTER_INIT));
	}

	public void destroy(Object instance) throws Exception {
		beanFactory.getEventDispatcher().publishEvent(
				new BeanLifeCycleEvent(this, instance, beanFactory, propertyFactory, Step.BEFORE_DESTROY));
		if (instance != null) {
			Ioc ioc = Ioc.forClass(instance.getClass());
			ioc.getDestroy().process(this, instance, beanFactory, propertyFactory, false);
			BeanUtils.destroy(instance);

			ioc.getDestroy().process(this, instance, beanFactory, propertyFactory, false);
		}
		beanFactory.getEventDispatcher()
				.publishEvent(new BeanLifeCycleEvent(this, instance, beanFactory, propertyFactory, Step.AFTER_DESTROY));
	}

	public String getId() {
		Bean bean = getAnnotatedElement().getAnnotation(Bean.class);
		if (bean == null) {
			return getTargetClass().getName();
		}

		return StringUtils.isEmpty(bean.value()) ? getTargetClass().getName() : bean.value();
	}

	public Collection<String> getNames() {
		Bean bean = getAnnotatedElement().getAnnotation(Bean.class);
		if (bean == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(bean.names());
	}

	protected boolean isProxy() {
		return BeanUtils.isAopEnable(getTargetClass(), getTargetClass());
	}

	public boolean isSingleton() {
		Bean bean = getAnnotatedElement().getAnnotation(Bean.class);
		if (bean == null) {
			return true;
		}
		return bean.singleton();
	}

	public AnnotatedElement getAnnotatedElement() {
		return getTargetClass();
	}

	@Override
	protected Object createInternal(Class<?> targetClass, Constructor<? extends Object> constructor, Object[] params)
			throws Exception {
		if (isProxy()) {
			return createProxyInstance(targetClass, constructor.getParameterTypes(), params);
		}
		return super.createInternal(targetClass, constructor, params);
	}

	protected Proxy createProxy(Class<?> targetClass, Class<?>[] interfaces) {
		return beanFactory.getAop().getProxy(targetClass, interfaces, filters);
	}

	protected Proxy createInstanceProxy(Object instance, Class<?> targetClass, Class<?>[] interfaces) {
		return beanFactory.getAop().getProxyInstance(targetClass, instance, interfaces, filters);
	}

	protected Object createProxyInstance(Class<?> targetClass, Class<?>[] parameterTypes, Object[] args) {
		if (getTargetClass().isInterface() && filters.isEmpty()) {
			logger.warn("empty filter: {}", getTargetClass().getName());
		}

		Proxy proxy = createProxy(targetClass, null);
		return proxy.create(parameterTypes, args);
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	@Override
	public BeanDefinition clone() {
		try {
			AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) super.clone();
			beanDefinition.setNew(false);
			beanDefinition.filters = Arrays.asList(filters.toArray(new Filter[0]));
			beanDefinition.ioc.readyOnly();
			return beanDefinition;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
