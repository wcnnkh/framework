package scw.beans;

import java.util.Arrays;
import java.util.LinkedList;

import scw.beans.property.ValueWiredManager;
import scw.core.Init;
import scw.core.PropertyFactory;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.XUtils;

public abstract class AbstractBeanDefinition implements BeanDefinition, Init {
	protected final BeanFactory beanFactory;
	private final Class<?> type;
	private final String id;
	private NoArgumentBeanMethod[] initMethods;
	private NoArgumentBeanMethod[] destroyMethods;
	private boolean proxy;
	protected final PropertyFactory propertyFactory;
	private boolean singleton;
	private FieldDefinition[] autowriteFieldDefinition;
	protected final ValueWiredManager valueWiredManager;
	protected final LinkedList<String> filterList = new LinkedList<String>();

	public AbstractBeanDefinition(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> type) {
		this.valueWiredManager = valueWiredManager;
		this.beanFactory = beanFactory;
		this.type = type;
		this.propertyFactory = propertyFactory;
		this.id = type.getName();
	}

	public void init() {
		this.initMethods = BeanUtils.getInitMethodList(getType()).toArray(new NoArgumentBeanMethod[0]);
		this.destroyMethods = BeanUtils.getDestroyMethdoList(getType()).toArray(new NoArgumentBeanMethod[0]);
		this.proxy = BeanUtils.checkProxy(getType());
		scw.beans.annotation.Bean bean = getType().getAnnotation(scw.beans.annotation.Bean.class);
		this.singleton = bean == null ? true : bean.singleton();
		this.autowriteFieldDefinition = BeanUtils.getAutowriteFieldDefinitionList(getType(), false)
				.toArray(new FieldDefinition[0]);
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

	public Class<?> getType() {
		return this.type;
	}

	public void autowrite(Object bean) throws Exception {
		BeanUtils.autoWrite(valueWiredManager, beanFactory, propertyFactory, getType(), bean,
				Arrays.asList(autowriteFieldDefinition));
	}

	public void init(Object bean) throws Exception {
		if (initMethods != null && initMethods.length != 0) {
			for (NoArgumentBeanMethod method : initMethods) {
				method.noArgumentInvoke(bean);
			}
		}

		XUtils.init(bean);
	}

	public void destroy(Object bean) throws Exception {
		valueWiredManager.cancel(bean);
		if (destroyMethods != null && destroyMethods.length != 0) {
			for (NoArgumentBeanMethod method : destroyMethods) {
				method.invoke(bean);
			}
		}

		XUtils.destroy(bean);
	}
}
