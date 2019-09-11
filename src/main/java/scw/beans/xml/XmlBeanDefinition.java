package scw.beans.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanMethod;
import scw.beans.BeanUtils;
import scw.beans.property.ValueWiredManager;
import scw.core.Destroy;
import scw.core.Init;
import scw.core.PropertyFactory;
import scw.core.aop.Filter;
import scw.core.cglib.proxy.Enhancer;
import scw.core.exception.BeansException;
import scw.core.exception.NotFoundException;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public final class XmlBeanDefinition implements BeanDefinition {
	private Logger logger = LoggerFactory.getLogger(XmlBeanDefinition.class);

	private final BeanFactory beanFactory;
	private final PropertyFactory propertyFactory;
	private String[] names;
	private final String id;
	private final boolean singleton;
	private final String[] filterNames;
	// 构造函数的参数
	private final XmlBeanParameter[] constructorParameters;
	private final XmlBeanParameter[] properties;
	private final BeanMethod[] initMethods;
	private final BeanMethod[] destroyMethods;
	private final boolean proxy;

	private XmlBeanParameter[] beanMethodParameters;
	private final FieldDefinition[] autowriteFields;
	private final Class<?> type;
	private final ValueWiredManager valueWiredManager;
	private final String proxyName;

	// 如果是一个接口就是空的
	private Constructor<?> constructor;
	private Class<?>[] constructorParameterTypes;

	public XmlBeanDefinition(ValueWiredManager valueWiredManager,
			BeanFactory beanFactory, PropertyFactory propertyFactory,
			Node beanNode, String[] filterNames) throws Exception {
		this.valueWiredManager = valueWiredManager;
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
		this.type = XmlBeanUtils.getClass(beanNode);
		this.names = XmlBeanUtils.getNames(beanNode);
		this.id = XmlBeanUtils.getId(beanNode);
		this.singleton = XmlBeanUtils.isSingleton(beanNode);
		this.filterNames = XmlBeanUtils.getFilters(beanNode, filterNames);
		NodeList nodeList = beanNode.getChildNodes();
		this.initMethods = XmlBeanUtils.getInitMethodList(type, nodeList);
		this.destroyMethods = XmlBeanUtils.getDestroyMethodList(type, nodeList);
		this.properties = XmlBeanUtils.getBeanProperties(nodeList);
		this.proxyName = XmlBeanUtils.getProxyName(propertyFactory, beanNode);
		this.proxy = StringUtils.isEmpty(proxyName) ? BeanUtils.checkProxy(
				type, this.filterNames) : true;
		this.autowriteFields = BeanUtils.getAutowriteFieldDefinitionList(type,
				false).toArray(new FieldDefinition[0]);
		this.constructorParameters = XmlBeanUtils
				.getConstructorParameters(nodeList);

		if (!type.isInterface()) {
			this.constructor = getConstructor();
			if (constructor == null) {
				throw new NotFoundException(type.getName() + "找不到对应的构造函数");
			}
			this.constructorParameterTypes = constructor.getParameterTypes();
		}
	}

	private Constructor<?> getConstructor() {
		if (ArrayUtils.isEmpty(constructorParameters)) {
			return ReflectUtils.getConstructor(type, false);
		} else {
			for (Constructor<?> constructor : type.getDeclaredConstructors()) {
				XmlBeanParameter[] beanMethodParameters = BeanUtils
						.sortParameters(constructor, constructorParameters);
				if (beanMethodParameters != null) {
					this.beanMethodParameters = beanMethodParameters;
					constructor.setAccessible(true);
					return constructor;
				}
			}
		}
		return null;
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

	private Enhancer getProxyEnhancer() {
		return BeanUtils.createEnhancer(
				type,
				beanFactory,
				filterNames,
				StringUtils.isEmpty(proxyName) ? null : (Filter) beanFactory
						.getInstance(proxyName));
	}

	private Object createProxyInstance() throws Exception {
		if (type.isInterface()) {
			if (StringUtils.isEmpty(proxyName)) {
				logger.warn("{} is an interface, but there is no proxy.", type);
			}
			
			return BeanUtils.proxyInterface(beanFactory, getType(),
					filterNames, StringUtils.isEmpty(proxyName) ? null
							: (Filter) beanFactory.getInstance(proxyName));
		}

		Enhancer enhancer = getProxyEnhancer();
		if (ArrayUtils.isEmpty(constructorParameters)) {
			return enhancer.create();
		} else {
			Object[] args = BeanUtils.getBeanMethodParameterArgs(
					beanMethodParameters, beanFactory, propertyFactory);
			return enhancer.create(constructorParameterTypes, args);
		}
	}

	private void setProperties(Object bean) throws Exception {
		if (ArrayUtils.isEmpty(properties)) {
			return;
		}

		for (XmlBeanParameter beanProperties : properties) {
			Field field = ReflectUtils.getField(type, beanProperties.getName(),
					true);
			if (field == null) {
				continue;
			}

			ReflectUtils.setFieldValue(type, field, bean, beanProperties
					.parseValue(beanFactory, propertyFactory,
							field.getGenericType()));
		}
	}

	private Object createInstance() throws Exception {
		if (ArrayUtils.isEmpty(constructorParameterTypes)) {
			return constructor.newInstance();
		} else {
			Object[] args = BeanUtils.getBeanMethodParameterArgs(
					beanMethodParameters, beanFactory, propertyFactory);
			return constructor.newInstance(args);
		}
	}

	public void autowrite(Object bean) throws Exception {
		BeanUtils.autoWrite(valueWiredManager, beanFactory, propertyFactory,
				type, bean, Arrays.asList(autowriteFields));
		setProperties(bean);
	}

	public void init(Object bean) throws Exception {
		if (!ArrayUtils.isEmpty(initMethods)) {
			for (BeanMethod method : initMethods) {
				method.invoke(bean, beanFactory, propertyFactory);
			}
		}

		if (bean instanceof Init) {
			((Init) bean).init();
		}
	}

	public void destroy(Object bean) throws Exception {
		valueWiredManager.cancel(bean);
		if (!ArrayUtils.isEmpty(destroyMethods)) {
			for (BeanMethod method : destroyMethods) {
				method.invoke(bean, beanFactory, propertyFactory);
			}
		}

		if (bean instanceof Destroy) {
			((Destroy) bean).destroy();
		}
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
		Constructor<T> constructor = (Constructor<T>) ReflectUtils
				.findConstructorByParameters(getType(), false, params);
		if (constructor == null) {
			throw new NotFoundException(getId() + "找不到指定的构造方法");
		}

		Object bean;
		try {
			if (isProxy()) {
				Enhancer enhancer = getProxyEnhancer();
				bean = enhancer.create(constructor.getParameterTypes(), params);
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
		Constructor<?> constructor = ReflectUtils.findConstructor(getType(),
				false, parameterTypes);
		if (constructor == null) {
			throw new NotFoundException(getId() + "找不到指定的构造方法");
		}

		Object bean;
		try {
			if (isProxy()) {
				Enhancer enhancer = getProxyEnhancer();
				bean = enhancer.create(constructor.getParameterTypes(), params);
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
