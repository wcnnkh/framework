package scw.beans;

import java.util.LinkedList;

import scw.core.Init;
import scw.core.instance.definition.AbstractInstanceDefinition;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.XUtils;
import scw.util.value.property.PropertyFactory;

public abstract class AbstractBeanDefinition extends AbstractInstanceDefinition
		implements BeanDefinition, Init {
	protected final BeanFactory beanFactory;
	protected final LinkedList<BeanMethod> initMethods = new LinkedList<BeanMethod>();
	protected final LinkedList<BeanMethod> destroyMethods = new LinkedList<BeanMethod>();
	protected boolean proxy;
	protected final PropertyFactory propertyFactory;
	protected boolean singleton;
	protected final LinkedList<FieldDefinition> autowriteFieldDefinition = new LinkedList<FieldDefinition>();
	protected final LinkedList<String> filterNames = new LinkedList<String>();

	public AbstractBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass) {
		super(targetClass, beanFactory);
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
	}

	public void init() {
		initMethods.addAll(BeanUtils.getInitMethodList(getTargetClass()));
		destroyMethods.addAll(BeanUtils.getDestroyMethdoList(getTargetClass()));
		this.proxy = BeanUtils.isProxy(getTargetClass(), getAnnotatedElement());
		this.singleton = BeanUtils.isSingletion(getTargetClass(),
				getAnnotatedElement());
		autowriteFieldDefinition.addAll(BeanUtils
				.getAutowriteFieldDefinitionList(getTargetClass()));
	}

	public boolean isSingleton() {
		return singleton;
	}

	public boolean isProxy() {
		return this.proxy ? true : (!filterNames.isEmpty());
	}

	public void init(Object bean) throws Exception {
		BeanUtils.autowired(beanFactory, propertyFactory, getTargetClass(),
				bean, autowriteFieldDefinition);
		if (initMethods.size() != 0) {
			for (BeanMethod method : initMethods) {
				method.invoke(bean, beanFactory, propertyFactory);
			}
		}

		if (bean instanceof BeanFactoryAware) {
			((BeanFactoryAware) bean).setBeanFactory(beanFactory);
		}

		if (bean instanceof BeanDefinitionAware) {
			((BeanDefinitionAware) bean).setBeanDefinition(this);
		}
		super.init(bean);
	}

	public void destroy(Object bean) throws Exception {
		if (destroyMethods.size() != 0) {
			for (BeanMethod method : destroyMethods) {
				method.invoke(bean, beanFactory, propertyFactory);
			}
		}

		XUtils.destroy(bean);
	}
}
