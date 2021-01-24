package scw.beans.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import scw.aop.MethodInterceptor;
import scw.aop.MethodInterceptors;
import scw.aop.Proxy;
import scw.beans.AopEnableSpi;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanLifeCycleEvent;
import scw.beans.BeanLifeCycleEvent.Step;
import scw.beans.BeanUtils;
import scw.beans.RuntimeBean;
import scw.beans.annotation.Bean;
import scw.beans.annotation.ConfigurationProperties;
import scw.beans.ioc.Ioc;
import scw.configure.support.ConfigureUtils;
import scw.configure.support.PropertyFactoryConfigure;
import scw.context.support.LifecycleAuxiliary;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.instance.support.DefaultInstanceBuilder;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mapper.Field;
import scw.mapper.FieldFilter;
import scw.mapper.MapperUtils;
import scw.value.factory.PropertyFactory;

public class DefaultBeanDefinition extends DefaultInstanceBuilder<Object>
		implements BeanDefinition, Cloneable, AopEnableSpi {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	protected final BeanFactory beanFactory;
	protected Ioc ioc = new Ioc();
	private boolean isNew = true;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DefaultBeanDefinition(BeanFactory beanFactory, Class sourceClass) {
		super(beanFactory, beanFactory.getEnvironment(), sourceClass);
		this.beanFactory = beanFactory;
	}

	public void dependence(Object instance) throws Throwable {
		scw.beans.RuntimeBean runtimeBean = BeanUtils.getRuntimeBean(instance);
		if (runtimeBean != null && !runtimeBean._dependence()) {
			return;
		}
		beanFactory.getBeanLifeCycleEventDispatcher().publishEvent(
				new BeanLifeCycleEvent(this, instance, beanFactory, Step.BEFORE_DEPENDENCE));
		if (instance != null) {
			for (Ioc ioc : Ioc.forClass(instance.getClass())) {
				ioc.getDependence().process(this, instance, beanFactory);
			}
			ioc.getDependence().process(this, instance, beanFactory);

			// @ConfigurationProperties
			ConfigurationProperties configurationProperties = instance
					.getClass().getAnnotation(ConfigurationProperties.class);
			if (configurationProperties != null) {
				configurationProperties(configurationProperties, instance);
			}

			configurationProperties = getAnnotatedElement().getAnnotation(
					ConfigurationProperties.class);
			if (configurationProperties != null) {
				configurationProperties(configurationProperties, instance);
			}

			BeanUtils.aware(instance, beanFactory, this);
		}
		beanFactory.getBeanLifeCycleEventDispatcher().publishEvent(
				new BeanLifeCycleEvent(this, instance, beanFactory, Step.AFTER_DEPENDENCE));
	}

	protected void configurationProperties(
			ConfigurationProperties configurationProperties, Object instance) {
		PropertyFactoryConfigure configure = new PropertyFactoryConfigure(
				ConfigureUtils.getConversionServiceFactory());
		configure.setStrict(false);
		String prefix = configurationProperties.prefix();
		if (StringUtils.isEmpty(prefix)) {
			prefix = configurationProperties.value();
		}
		configure.setPrefix(prefix);
		configure.getFieldFilters().add(new FieldFilter() {
			
			public boolean accept(Field field) {
				//如果字段上存在beans下的注解应该忽略此字段
				for(Annotation annotation: field.getAnnotatedElement().getAnnotations()){
					if(annotation.annotationType().getName().startsWith("scw.beans.")){
						return false;
					}
				}
				return true;
			}
		});
		configure.configuration(beanFactory.getEnvironment(), PropertyFactory.class,
				instance, beanFactory.getAop()
						.getUserClass(instance.getClass()));
	}

	public void init(Object instance) throws Throwable {
		scw.beans.RuntimeBean runtimeBean = BeanUtils.getRuntimeBean(instance);
		if (runtimeBean != null && !runtimeBean._init()) {
			return;
		}
		beanFactory.getBeanLifeCycleEventDispatcher().publishEvent(
				new BeanLifeCycleEvent(this, instance, beanFactory, Step.BEFORE_INIT));
		if (instance != null) {
			for (Ioc ioc : Ioc.forClass(instance.getClass())) {
				ioc.getInit().process(this, instance, beanFactory);
			}
			ioc.getInit().process(this, instance, beanFactory);
			LifecycleAuxiliary.init(instance);
		}
		beanFactory.getBeanLifeCycleEventDispatcher().publishEvent(
				new BeanLifeCycleEvent(this, instance, beanFactory, Step.AFTER_INIT));
	}

	public void destroy(Object instance) throws Throwable {
		scw.beans.RuntimeBean runtimeBean = BeanUtils.getRuntimeBean(instance);
		if (runtimeBean != null && !runtimeBean._destroy()) {
			return;
		}

		beanFactory.getBeanLifeCycleEventDispatcher().publishEvent(
				new BeanLifeCycleEvent(this, instance, beanFactory, Step.BEFORE_DESTROY));
		if (instance != null) {
			LifecycleAuxiliary.destroy(instance);
			ioc.getDestroy().process(this, instance, beanFactory);
			for (Ioc ioc : Ioc.forClass(instance.getClass())) {
				ioc.getDestroy().process(this, instance, beanFactory);
			}
		}
		beanFactory.getBeanLifeCycleEventDispatcher().publishEvent(
				new BeanLifeCycleEvent(this, instance, beanFactory, Step.AFTER_DESTROY));
	}

	public String getId() {
		Bean bean = getAnnotatedElement().getAnnotation(Bean.class);
		if (bean == null) {
			return getTargetClass().getName();
		}

		return StringUtils.isEmpty(bean.value()) ? getTargetClass().getName()
				: bean.value();
	}

	public Collection<String> getNames() {
		Bean bean = getAnnotatedElement().getAnnotation(Bean.class);
		if (bean == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(bean.names());
	}

	public boolean isAopEnable() {
		return isAopEnable(getTargetClass(), getAnnotatedElement());
	}

	public boolean isAopEnable(Class<?> clazz, AnnotatedElement annotatedElement) {
		return BeanUtils.isAopEnable(clazz, annotatedElement);
	}

	public boolean isSingleton() {
		return BeanUtils.isSingleton(getTargetClass(), getAnnotatedElement());
	}

	public AnnotatedElement getAnnotatedElement() {
		return getTargetClass();
	}

	public Iterable<? extends MethodInterceptor> getFilters() {
		return null;
	}

	@Override
	public boolean isInstance() {
		return super.isInstance(isAopEnable()
				&& !CollectionUtils.isEmpty(getFilters()));
	}
	
	@Override
	protected Object createInternal(Class<?> targetClass,
			Constructor<? extends Object> constructor, Object[] params)
			throws Exception {
		if (isAopEnable(targetClass, getAnnotatedElement())) {
			return createProxyInstance(targetClass,
					constructor.getParameterTypes(), params);
		}
		return super.createInternal(targetClass, constructor, params);
	}

	protected Proxy createProxy(Class<?> targetClass, Class<?>[] interfaces) {
		Class<?>[] interfacesToUse = interfaces;
		if (ArrayUtils.isEmpty(interfacesToUse)) {
			interfacesToUse = scw.beans.RuntimeBean.PROXY_INTERFACES;
		} else {
			interfacesToUse = ArrayUtils.merge(interfacesToUse,
					scw.beans.RuntimeBean.PROXY_INTERFACES);
		}

		MethodInterceptors methodInterceptors = new MethodInterceptors();
		methodInterceptors
				.addLast(new RuntimeBean.RuntimeBeanMethodInterceptor(this));
		methodInterceptors.addLast(getFilters());
		return beanFactory.getAop().getProxy(targetClass, interfacesToUse,
				methodInterceptors);
	}

	protected Proxy createInstanceProxy(Object instance, Class<?> targetClass,
			Class<?>[] interfaces) {
		Class<?>[] interfacesToUse = interfaces;
		if (ArrayUtils.isEmpty(interfacesToUse)) {
			interfacesToUse = RuntimeBean.PROXY_INTERFACES;
		} else {
			interfacesToUse = ArrayUtils.merge(interfacesToUse,
					RuntimeBean.PROXY_INTERFACES);
		}

		MethodInterceptors methodInterceptors = new MethodInterceptors();
		methodInterceptors
				.addLast(new RuntimeBean.RuntimeBeanMethodInterceptor(this));
		methodInterceptors.addLast(getFilters());
		return beanFactory.getAop().getProxyInstance(targetClass, instance,
				interfaces, methodInterceptors);
	}

	protected Object createProxyInstance(Class<?> targetClass,
			Class<?>[] parameterTypes, Object[] args) {
		if (getTargetClass().isInterface()
				&& CollectionUtils.isEmpty(getFilters())) {
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
			DefaultBeanDefinition beanDefinition = (DefaultBeanDefinition) super
					.clone();
			beanDefinition.setNew(false);
			beanDefinition.ioc.readyOnly();
			return beanDefinition;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String toString() {
		return MapperUtils.getMapper().getFieldValueMap(this).toString();
	}
}
