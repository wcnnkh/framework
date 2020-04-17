package scw.beans;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;

import scw.core.Init;
import scw.core.reflect.FieldDefinition;
import scw.util.value.property.PropertyFactory;

public abstract class AbstractBeanDefinition implements BeanDefinition, Init {
	protected final BeanFactory beanFactory;
	private final Class<?> targetClass;
	private final String id;
	private NoArgumentBeanMethod[] initMethods;
	private NoArgumentBeanMethod[] destroyMethods;
	protected boolean proxy;
	protected final PropertyFactory propertyFactory;
	protected boolean singleton;
	private FieldDefinition[] autowriteFieldDefinition;

	public AbstractBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass) {
		this.beanFactory = beanFactory;
		this.targetClass = targetClass;
		this.propertyFactory = propertyFactory;
		this.id = targetClass.getName();
	}

	public void init() {
		this.initMethods = BeanUtils.getInitMethodList(getTargetClass())
				.toArray(new NoArgumentBeanMethod[0]);
		this.destroyMethods = BeanUtils.getDestroyMethdoList(getTargetClass())
				.toArray(new NoArgumentBeanMethod[0]);
		this.proxy = BeanUtils.checkProxy(getTargetClass());
		scw.beans.annotation.Bean bean = getTargetClass().getAnnotation(
				scw.beans.annotation.Bean.class);
		this.singleton = bean == null ? true : bean.singleton();
		this.autowriteFieldDefinition = BeanUtils
				.getAutowriteFieldDefinitionList(getTargetClass()).toArray(
						new FieldDefinition[0]);
	}

	public boolean isSingleton() {
		return singleton;
	}

	public boolean isProxy() {
		return this.proxy;
	}

	public String getId() {
		return this.id;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public void init(Object bean) throws Exception {
		BeanUtils.autowired(beanFactory, propertyFactory, getTargetClass(),
				bean, Arrays.asList(autowriteFieldDefinition));

		if (initMethods != null && initMethods.length != 0) {
			for (NoArgumentBeanMethod method : initMethods) {
				method.noArgumentInvoke(bean);
			}
		}
		
		BeanUtils.init(bean);
	}

	public void destroy(Object bean) throws Exception {
		if (destroyMethods != null && destroyMethods.length != 0) {
			for (NoArgumentBeanMethod method : destroyMethods) {
				method.invoke(bean);
			}
		}

		BeanUtils.destroy(bean);
	}
	
	public AnnotatedElement getAnnotatedElement() {
		return getTargetClass();
	}
}
