package scw.beans.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.cglib.proxy.Enhancer;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanMethod;
import scw.beans.BeanUtils;
import scw.core.Destroy;
import scw.core.PropertiesFactory;
import scw.core.exception.BeansException;
import scw.core.exception.NotFoundException;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;

public class XmlBeanDefinition implements BeanDefinition {
	private final BeanFactory beanFactory;
	private final PropertiesFactory propertiesFactory;
	private String[] names;
	private final String id;
	private final boolean singleton;
	private final String[] filterNames;
	// 构造函数的参数
	private XmlBeanParameter[] constructorParameters;
	private final XmlBeanParameter[] properties;
	private final BeanMethod[] initMethods;
	private final BeanMethod[] destroyMethods;
	private final BeanMethod[] factoryMethods;
	private final boolean proxy;

	private XmlBeanParameter[] beanMethodParameters;
	private Enhancer enhancer;
	private final FieldDefinition[] autowriteFields;
	private final Class<?> type;
	private final String refId;

	// 如果是引用的就会为空
	private Constructor<?> constructor;
	private Class<?>[] constructorParameterTypes;

	public XmlBeanDefinition(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Node beanNode,
			String[] filterNames) throws Exception {
		this.beanFactory = beanFactory;
		this.propertiesFactory = propertiesFactory;
		this.type = XmlBeanUtils.getClass(beanNode);
		this.names = XmlBeanUtils.getNames(beanNode);
		this.refId = XMLUtils.getNodeAttributeValue(beanNode, "ref");
		this.id = XmlBeanUtils.getId(beanNode);
		this.singleton = XmlBeanUtils.isSingleton(beanNode);
		this.filterNames = XmlBeanUtils.getFilters(beanNode, filterNames);
		NodeList nodeList = beanNode.getChildNodes();
		this.factoryMethods = XmlBeanUtils.getFactoryMethodList(type, nodeList);
		this.initMethods = XmlBeanUtils.getInitMethodList(type, nodeList);
		this.destroyMethods = XmlBeanUtils.getDestroyMethodList(type, nodeList);
		this.properties = XmlBeanUtils.getBeanProperties(nodeList);
		this.proxy = BeanUtils.checkProxy(type, this.filterNames);
		this.autowriteFields = BeanUtils.getAutowriteFieldDefinitionList(type, false).toArray(new FieldDefinition[0]);

		if (StringUtils.isEmpty(refId)) {
			this.constructorParameters = XmlBeanUtils.getConstructorParameters(nodeList);
			this.constructor = getConstructor();
			if (constructor == null) {
				throw new NotFoundException(type.getName() + "找不到对应的构造函数");
			}
			this.constructorParameterTypes = constructor.getParameterTypes();
		}
	}

	private Constructor<?> getConstructor() {
		if (ArrayUtils.isEmpty(constructorParameters)) {
			return getConstructorByParameterTypes();
		} else {
			for (Constructor<?> constructor : type.getDeclaredConstructors()) {
				XmlBeanParameter[] beanMethodParameters = BeanUtils.sortParameters(constructor, constructorParameters);
				if (beanMethodParameters != null) {
					this.beanMethodParameters = beanMethodParameters;
					constructor.setAccessible(true);
					return constructor;
				}
			}
		}
		return null;
	}

	private Constructor<?> getConstructorByParameterTypes(Class<?>... parameterTypes) {
		try {
			return type.getConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			try {
				return type.getDeclaredConstructor(parameterTypes);
			} catch (NoSuchMethodException e1) {
			} catch (SecurityException e1) {
			}
		} catch (SecurityException e) {
			try {
				return type.getDeclaredConstructor(parameterTypes);
			} catch (NoSuchMethodException e1) {
			} catch (SecurityException e1) {
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
		if (enhancer == null) {
			enhancer = BeanUtils.createEnhancer(type, beanFactory, filterNames);
		}
		return enhancer;
	}

	private Object createProxyInstance() throws Exception {
		Enhancer enhancer = getProxyEnhancer();
		if (ArrayUtils.isEmpty(constructorParameters)) {
			return enhancer.create();
		} else {
			Object[] args = BeanUtils.getBeanMethodParameterArgs(beanMethodParameters, beanFactory, propertiesFactory);
			return enhancer.create(constructorParameterTypes, args);
		}
	}

	private void setProperties(Object bean) throws Exception {
		if (ArrayUtils.isEmpty(properties)) {
			return;
		}

		for (XmlBeanParameter beanProperties : properties) {
			Field field = ReflectUtils.getField(type, beanProperties.getName(), true);
			if (field == null) {
				continue;
			}

			ReflectUtils.setFieldValue(type, field, bean,
					beanProperties.parseValue(beanFactory, propertiesFactory, field.getType()));
		}
	}

	private Object createInstance() throws Exception {
		if (ArrayUtils.isEmpty(constructorParameterTypes)) {
			return constructor.newInstance();
		} else {
			Object[] args = BeanUtils.getBeanMethodParameterArgs(beanMethodParameters, beanFactory, propertiesFactory);
			return constructor.newInstance(args);
		}
	}

	public void autowrite(Object bean) throws Exception {
		if (!ArrayUtils.isEmpty(autowriteFields)) {
			for (FieldDefinition fieldDefinition : autowriteFields) {
				BeanUtils.autoWrite(type, beanFactory, propertiesFactory, bean, fieldDefinition);
			}
		}

		setProperties(bean);
	}

	public void init(Object bean) throws Exception {
		if (!ArrayUtils.isEmpty(initMethods)) {
			for (BeanMethod method : initMethods) {
				method.invoke(bean, beanFactory, propertiesFactory);
			}
		}
	}

	public void destroy(Object bean) throws Exception {
		if (!ArrayUtils.isEmpty(destroyMethods)) {
			for (BeanMethod method : destroyMethods) {
				method.invoke(bean, beanFactory, propertiesFactory);
			}
		}

		if (bean instanceof Destroy) {
			((Destroy) bean).destroy();
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		Object bean = null;
		try {
			if (!StringUtils.isEmpty(refId)) {
				bean = beanFactory.get(refId);
			} else {
				if (isProxy()) {
					bean = createProxyInstance();
				} else {
					bean = createInstance();
				}
			}

			return (T) invokeFactory(bean);
		} catch (Exception e) {
			throw new BeansException(type.getName(), e);
		}
	}

	private Object invokeFactory(Object bean) throws Exception {
		if (ArrayUtils.isEmpty(factoryMethods)) {
			return bean;
		}

		Object v = bean;
		for (BeanMethod beanMethod : factoryMethods) {
			Object temp = beanMethod.invoke(v, beanFactory, propertiesFactory);
			if (!Void.class.isAssignableFrom(beanMethod.getMethod().getReturnType())) {
				v = temp;
			}
		}
		return v;
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<?>[] parameterTypes, Object... args) {
		Object bean = null;
		try {
			if (!StringUtils.isEmpty(refId)) {
				bean = beanFactory.get(refId);
			} else {
				if (isProxy()) {
					bean = getProxyEnhancer().create(parameterTypes, args);
				} else {
					bean = type.getConstructor(parameterTypes).newInstance(args);
				}
			}

			return (T) invokeFactory(bean);
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}

	public String[] getNames() {
		return names;
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Object... params) {
		Constructor<T> constructor = (Constructor<T>) ReflectUtils.findConstructorByParameters(getType(), true, params);
		if (constructor == null) {
			throw new NotFoundException(getId() + "找不到指定的构造方法");
		}

		Object bean;
		try {
			if (!StringUtils.isEmpty(refId)) {
				bean = beanFactory.get(refId);
			} else {
				if (isProxy()) {
					Enhancer enhancer = getProxyEnhancer();
					bean = enhancer.create(constructor.getParameterTypes(), params);
				} else {
					bean = constructor.newInstance(params);
				}
			}

			return (T) invokeFactory(bean);
		} catch (Throwable e) {
			throw new BeansException(getId(), e);
		}
	}
}
