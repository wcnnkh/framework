package scw.beans.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanMethod;
import scw.beans.BeanUtils;
import scw.beans.BeansException;
import scw.beans.annotation.Proxy;
import scw.beans.auto.AutoBeanUtils;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.instance.AutoInstanceConfig;
import scw.core.instance.InstanceConfig;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.XUtils;
import scw.lang.NotFoundException;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public final class XmlBeanDefinition implements BeanDefinition {
	private Logger logger = LoggerFactory.getLogger(XmlBeanDefinition.class);

	private final BeanFactory beanFactory;
	private final PropertyFactory propertyFactory;
	private String[] names;
	private final String id;
	private final boolean singleton;
	private final Collection<String> filterNames;
	private final XmlBeanParameter[] properties;
	private final BeanMethod[] initMethods;
	private final BeanMethod[] destroyMethods;
	private final boolean proxy;

	private final FieldDefinition[] autowriteFields;
	private final Class<?> type;
	private final ValueWiredManager valueWiredManager;
	private InstanceConfig instanceConfig;

	public XmlBeanDefinition(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Node beanNode) throws Exception {
		this.valueWiredManager = valueWiredManager;
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
		this.type = XmlBeanUtils.getClass(beanNode);
		this.names = XmlBeanUtils.getNames(beanNode);
		this.id = XmlBeanUtils.getId(beanNode);
		this.singleton = XmlBeanUtils.isSingleton(beanNode);
		this.filterNames = new LinkedList<String>(XmlBeanUtils.getFilters(beanNode));
		Proxy proxy = type.getAnnotation(Proxy.class);
		if (proxy != null) {
			filterNames.addAll(AutoBeanUtils.getProxyNames(proxy));
		}

		this.proxy = CollectionUtils.isEmpty(filterNames) ? BeanUtils.checkProxy(type) : true;

		NodeList nodeList = beanNode.getChildNodes();
		this.initMethods = XmlBeanUtils.getInitMethodList(type, nodeList);
		this.destroyMethods = XmlBeanUtils.getDestroyMethodList(type, nodeList);
		this.properties = XmlBeanUtils.getBeanProperties(nodeList);
		this.autowriteFields = BeanUtils.getAutowriteFieldDefinitionList(type, false).toArray(new FieldDefinition[0]);

		if (!type.isInterface()) {// 可能只是映射
			XmlBeanParameter[] constructorParameters = XmlBeanUtils.getConstructorParameters(nodeList);
			this.instanceConfig = new XmlInstanceConfig(beanFactory, propertyFactory, type, constructorParameters);
			if (instanceConfig.getConstructor() == null && ArrayUtils.isEmpty(constructorParameters)) {
				instanceConfig = new AutoInstanceConfig(beanFactory, propertyFactory, type);
			}

			if (instanceConfig.getConstructor() == null) {
				throw new NotFoundException(type.getName() + "找不到对应的构造函数");
			}
		}
	}

	public String getId() {
		return this.id;
	}

	public Class<?> getType() {
		return this.type;
	}

	public boolean isSingleton() {
		return this.singleton;
	}

	public boolean isProxy() {
		return this.proxy;
	}

	private scw.aop.Proxy getProxy() {
		return BeanUtils.createProxy(beanFactory, type, filterNames, null);
	}

	private Object createProxyInstance() throws Exception {
		if (getType().isInterface()) {
			if (CollectionUtils.isEmpty(filterNames)) {
				logger.warn("{} is an interface, but there is no proxy.", type);
			}
			return getProxy().create();
		}

		scw.aop.Proxy proxy = getProxy();
		return proxy.create(instanceConfig.getConstructor().getParameterTypes(), instanceConfig.getArgs());
	}

	private void setProperties(Object bean) throws Exception {
		if (ArrayUtils.isEmpty(properties)) {
			return;
		}

		for (XmlBeanParameter beanProperties : properties) {
			Field field = ReflectionUtils.getField(type, beanProperties.getName(), true);
			if (field == null) {
				continue;
			}

			ReflectionUtils.setFieldValue(type, field, bean,
					beanProperties.parseValue(beanFactory, propertyFactory, field.getGenericType()));
		}
	}

	private Object createInstance() throws Exception {
		return instanceConfig.getConstructor().newInstance(instanceConfig.getArgs());
	}

	public void init(Object bean) throws Exception {
		BeanUtils.autowired(valueWiredManager, beanFactory, propertyFactory, type, bean,
				Arrays.asList(autowriteFields));
		setProperties(bean);
		
		if (!ArrayUtils.isEmpty(initMethods)) {
			for (BeanMethod method : initMethods) {
				method.invoke(bean, beanFactory, propertyFactory);
			}
		}

		XUtils.init(bean);
		XUtils.start(bean);
	}

	public void destroy(Object bean) throws Exception {
		valueWiredManager.cancel(bean);
		if (!ArrayUtils.isEmpty(destroyMethods)) {
			for (BeanMethod method : destroyMethods) {
				method.invoke(bean, beanFactory, propertyFactory);
			}
		}

		XUtils.destroy(bean);
	}

	@SuppressWarnings("unchecked")
	public final <T> T create() {
		Object bean = null;
		try {
			if (isProxy()) {
				bean = createProxyInstance();
			} else {
				bean = createInstance();
			}

			return (T) bean;
		} catch (Exception e) {
			throw new BeansException(type.getName(), e);
		}
	}

	public String[] getNames() {
		return names;
	}

	@SuppressWarnings("unchecked")
	public final <T> T create(Object... params) {
		Constructor<T> constructor = (Constructor<T>) ReflectionUtils.findConstructorByParameters(getType(), false,
				params);
		if (constructor == null) {
			throw new NotFoundException(getId() + "找不到指定的构造方法");
		}

		Object bean;
		try {
			if (isProxy()) {
				scw.aop.Proxy proxy = getProxy();
				bean = proxy.create(constructor.getParameterTypes(), params);
			} else {
				bean = constructor.newInstance(params);
			}

			return (T) bean;
		} catch (Throwable e) {
			throw new BeansException(getId(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public final <T> T create(Class<?>[] parameterTypes, Object... params) {
		Constructor<?> constructor = ReflectionUtils.findConstructor(getType(), false, parameterTypes);
		if (constructor == null) {
			throw new NotFoundException(getId() + "找不到指定的构造方法");
		}

		Object bean;
		try {
			if (isProxy()) {
				scw.aop.Proxy proxy = getProxy();
				bean = proxy.create(constructor.getParameterTypes(), params);
			} else {
				bean = constructor.newInstance(params);
			}

			return (T) bean;
		} catch (Throwable e) {
			throw new BeansException(getId(), e);
		}
	}

	public boolean isInstance() {
		return true;
	}
}
