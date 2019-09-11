package scw.beans;

import java.util.Arrays;

import scw.beans.property.ValueWiredManager;
import scw.core.Init;
import scw.core.PropertyFactory;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectUtils;

public abstract class AbstractBeanDefinition implements BeanDefinition {
	protected final BeanFactory beanFactory;
	protected final Class<?> type;
	private final String id;
	private final NoArgumentBeanMethod[] initMethods;
	private final NoArgumentBeanMethod[] destroyMethods;
	private final boolean proxy;
	protected final PropertyFactory propertyFactory;
	protected final String[] filterNames;
	private final boolean singleton;
	private final FieldDefinition[] autowriteFieldDefinition;
	private final String[] names;
	protected final ValueWiredManager valueWiredManager;
	private final boolean instance;

	public AbstractBeanDefinition(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> type, String[] filterNames) {
		this.valueWiredManager = valueWiredManager;
		this.beanFactory = beanFactory;
		this.type = type;
		this.propertyFactory = propertyFactory;
		this.id = type.getName();
		this.names = BeanUtils.getServiceNames(type);
		this.initMethods = BeanUtils.getInitMethodList(type).toArray(new NoArgumentBeanMethod[0]);
		this.destroyMethods = BeanUtils.getDestroyMethdoList(type).toArray(new NoArgumentBeanMethod[0]);
		this.filterNames = filterNames;
		this.proxy = BeanUtils.checkProxy(type, filterNames);
		scw.beans.annotation.Bean bean = type.getAnnotation(scw.beans.annotation.Bean.class);
		this.singleton = bean == null ? true : bean.singleton();
		this.autowriteFieldDefinition = BeanUtils.getAutowriteFieldDefinitionList(type, false)
				.toArray(new FieldDefinition[0]);
		this.instance = ReflectUtils.isInstance(type, true);
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
		BeanUtils.autoWrite(valueWiredManager, beanFactory, propertyFactory, type, bean,
				Arrays.asList(autowriteFieldDefinition));
	}

	public void init(Object bean) throws Exception {
		if (initMethods != null && initMethods.length != 0) {
			for (NoArgumentBeanMethod method : initMethods) {
				method.noArgumentInvoke(bean);
			}
		}

		if (bean instanceof Init) {
			((Init) bean).init();
		}
	}

	public void destroy(Object bean) throws Exception {
		valueWiredManager.cancel(bean);
		if (destroyMethods != null && destroyMethods.length != 0) {
			for (NoArgumentBeanMethod method : destroyMethods) {
				method.invoke(bean);
			}
		}

		if (bean instanceof scw.core.Destroy) {
			((scw.core.Destroy) bean).destroy();
		}
	}

	public String[] getNames() {
		return names;
	}

	public boolean isInstance() {
		return instance;
	}
}
