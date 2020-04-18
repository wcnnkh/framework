package scw.beans;

import java.lang.reflect.AnnotatedElement;
import java.util.LinkedList;

import scw.core.Init;
import scw.core.reflect.FieldDefinition;
import scw.util.value.property.PropertyFactory;

public abstract class AbstractBeanDefinition implements BeanDefinition, Init {
	protected final BeanFactory beanFactory;
	private final Class<?> targetClass;
	protected String id;
	protected final LinkedList<BeanMethod> initMethods = new LinkedList<BeanMethod>();
	protected final LinkedList<BeanMethod> destroyMethods = new LinkedList<BeanMethod>();
	protected boolean proxy;
	protected final PropertyFactory propertyFactory;
	protected boolean singleton;
	protected final LinkedList<FieldDefinition> autowriteFieldDefinition = new LinkedList<FieldDefinition>();
	protected final LinkedList<String> filterNames = new LinkedList<String>();

	public AbstractBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass) {
		this.beanFactory = beanFactory;
		this.targetClass = targetClass;
		this.propertyFactory = propertyFactory;
		this.id = targetClass.getName();
	}

	public void init() {
		initMethods.addAll(BeanUtils.getInitMethodList(getTargetClass()));
		destroyMethods.addAll(BeanUtils.getDestroyMethdoList(getTargetClass()));
		this.proxy = BeanUtils.checkProxy(getTargetClass());
		scw.beans.annotation.Bean bean = getTargetClass().getAnnotation(
				scw.beans.annotation.Bean.class);
		this.singleton = bean == null ? true : bean.singleton();
		autowriteFieldDefinition.addAll(BeanUtils
				.getAutowriteFieldDefinitionList(getTargetClass()));
	}

	public boolean isSingleton() {
		return singleton;
	}

	public boolean isProxy() {
		return this.proxy ? true : (!filterNames.isEmpty());
	}

	public String getId() {
		return this.id;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public void init(Object bean) throws Exception {
		BeanUtils.autowired(beanFactory, propertyFactory, getTargetClass(),
				bean, autowriteFieldDefinition);

		if (initMethods.size() != 0) {
			for (BeanMethod method : initMethods) {
				method.invoke(bean, beanFactory, propertyFactory);
			}
		}

		BeanUtils.init(bean);
	}

	public void destroy(Object bean) throws Exception {
		if (destroyMethods.size() != 0) {
			for (BeanMethod method : destroyMethods) {
				method.invoke(bean, beanFactory, propertyFactory);
			}
		}

		BeanUtils.destroy(bean);
	}

	public AnnotatedElement getAnnotatedElement() {
		return getTargetClass();
	}
}
