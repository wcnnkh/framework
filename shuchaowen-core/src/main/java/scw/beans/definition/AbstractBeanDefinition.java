package scw.beans.definition;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import scw.beans.BeanFactory;
import scw.beans.annotation.Bean;
import scw.beans.definition.builder.BeanBuilder;
import scw.beans.metadata.BeanLifeCycle;
import scw.core.utils.StringUtils;
import scw.util.value.property.PropertyFactory;

public abstract class AbstractBeanDefinition implements BeanDefinition {
	protected final BeanFactory beanFactory;
	protected final PropertyFactory propertyFactory;
	protected final Class<?> targetClass;

	public AbstractBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass) {
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
		this.targetClass = targetClass;
	}

	protected abstract BeanBuilder getBeanBuiler();

	protected BeanLifeCycle getBeanLifeCycle() {
		return beanFactory.getInstance(BeanLifeCycle.class);
	}

	public void init(Object instance) throws Exception {
		getBeanLifeCycle().initBefore(beanFactory, propertyFactory, this,
				instance);
		getBeanBuiler().init(instance);
		getBeanLifeCycle().initAfter(beanFactory, propertyFactory, this,
				instance);
	}

	public void destroy(Object instance) throws Exception {
		getBeanLifeCycle().destroyBefore(beanFactory, propertyFactory, this,
				instance);
		getBeanBuiler().destroy(instance);
		getBeanLifeCycle().destroyAfter(beanFactory, propertyFactory, this,
				instance);
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

	public Object create(Class<?>[] parameterTypes, Object... params)
			throws Exception {
		return getBeanBuiler().create(parameterTypes, params);
	}

	public String getId() {
		Bean bean = getAnnotatedElement().getAnnotation(Bean.class);
		if (bean == null) {
			return targetClass.getName();
		}

		return StringUtils.isEmpty(bean.id()) ? targetClass.getName() : bean
				.id();
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
