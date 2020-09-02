package scw.beans;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import scw.aop.MethodInterceptor;
import scw.aop.Proxy;
import scw.beans.annotation.Bean;
import scw.beans.builder.LoaderContext;
import scw.beans.event.BeanLifeCycleEvent;
import scw.beans.event.BeanLifeCycleEvent.Step;
import scw.beans.ioc.Ioc;
import scw.core.instance.DefaultInstanceBuilder;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.value.property.PropertyFactory;

public class DefaultBeanDefinition extends DefaultInstanceBuilder<Object> implements BeanDefinition, Cloneable{
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	protected final BeanFactory beanFactory;
	protected final PropertyFactory propertyFactory;
	protected Ioc ioc = new Ioc();
	private boolean isNew = true;

	public DefaultBeanDefinition(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass) {
		super(beanFactory, propertyFactory, targetClass);
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
	}

	public DefaultBeanDefinition(LoaderContext loaderContext) {
		this(loaderContext.getBeanFactory(), loaderContext.getPropertyFactory(), loaderContext.getTargetClass());
	}

	public void dependence(Object instance) throws Exception {
		beanFactory.getEventDispatcher().publishEvent(
				new BeanLifeCycleEvent(this, instance, beanFactory, propertyFactory, Step.BEFORE_DEPENDENCE));
		if (instance != null) {
			Ioc.forClass(instance.getClass()).getDependence().process(this, instance, beanFactory, propertyFactory);
			ioc.getDependence().process(this, instance, beanFactory, propertyFactory);
			BeanUtils.aware(instance, beanFactory, this);
		}
		beanFactory.getEventDispatcher().publishEvent(
				new BeanLifeCycleEvent(this, instance, beanFactory, propertyFactory, Step.AFTER_DEPENDENCE));
	}

	public void init(Object instance) throws Exception {
		beanFactory.getEventDispatcher()
				.publishEvent(new BeanLifeCycleEvent(this, instance, beanFactory, propertyFactory, Step.BEFORE_INIT));
		if (instance != null) {
			Ioc.forClass(instance.getClass()).getInit().process(this, instance, beanFactory, propertyFactory);
			ioc.getInit().process(this, instance, beanFactory, propertyFactory);
			BeanUtils.init(instance);
		}
		beanFactory.getEventDispatcher()
				.publishEvent(new BeanLifeCycleEvent(this, instance, beanFactory, propertyFactory, Step.AFTER_INIT));
	}

	public void destroy(Object instance) throws Exception {
		beanFactory.getEventDispatcher().publishEvent(
				new BeanLifeCycleEvent(this, instance, beanFactory, propertyFactory, Step.BEFORE_DESTROY));
		if (instance != null) {
			BeanUtils.destroy(instance);
			ioc.getDestroy().process(this, instance, beanFactory, propertyFactory);
			Ioc.forClass(instance.getClass()).getDestroy().process(this, instance, beanFactory, propertyFactory);
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
		return BeanUtils.isSingleton(getTargetClass(), getAnnotatedElement());
	}

	public AnnotatedElement getAnnotatedElement() {
		return getTargetClass();
	}
	
	public Iterable<? extends MethodInterceptor> getFilters(){
		return null;
	}
	
	@Override
	public boolean isInstance() {
		return super.isInstance(isProxy() && !CollectionUtils.isEmpty(getFilters()));
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
		return beanFactory.getAop().getProxy(targetClass, interfaces, getFilters());
	}

	protected Proxy createInstanceProxy(Object instance, Class<?> targetClass, Class<?>[] interfaces) {
		return beanFactory.getAop().getProxyInstance(targetClass, instance, interfaces, getFilters());
	}

	protected Object createProxyInstance(Class<?> targetClass, Class<?>[] parameterTypes, Object[] args) {
		if (getTargetClass().isInterface() && CollectionUtils.isEmpty(getFilters())) {
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
			DefaultBeanDefinition beanDefinition = (DefaultBeanDefinition) super.clone();
			beanDefinition.setNew(false);
			beanDefinition.ioc.readyOnly();
			return beanDefinition;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}