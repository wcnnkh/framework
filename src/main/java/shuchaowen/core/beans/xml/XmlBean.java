package shuchaowen.core.beans.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.cglib.proxy.Enhancer;
import shuchaowen.core.beans.Bean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.BeanFilter;
import shuchaowen.core.beans.BeanMethodInterceptor;
import shuchaowen.core.beans.BeanMethodParameter;
import shuchaowen.core.beans.BeanProperties;
import shuchaowen.core.beans.BeanUtils;
import shuchaowen.core.beans.PropertiesFactory;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.beans.annotaion.Transaction;
import shuchaowen.core.beans.exception.BeansException;
import shuchaowen.core.http.server.annotation.Controller;
import shuchaowen.core.util.ClassInfo;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.FieldInfo;
import shuchaowen.core.util.StringUtils;

public class XmlBean implements Bean {
	private static final String CLASS_ATTRIBUTE_KEY = "class";
	private static final String ID_ATTRIBUTE_KEY = "id";
	private static final String SINGLETON_ATTRIBUTE_KEY = "singleton";
	private static final String FILTERS_ATTRIBUTE_KEY = "filters";
	private static final String NAME_ATTRIBUTE_KEY = "name";// 别名

	private static final String CONSTRUCTOR_TAG_NAME = "constructor";
	private static final String PROPERTIES_TAG_NAME = "properties";
	private static final String INIT_METHOD_TAG_NAME = "init";
	private static final String DESTROY_METHOD_TAG_NAME = "destroy";
	private static final String FACTORY_METHOD_TAG_NAME = "factory-method";

	private final BeanFactory beanFactory;
	private final PropertiesFactory propertiesFactory;
	private final Class<?> type;
	private String[] names;
	private final String id;
	private final boolean singleton;
	private final List<Class<? extends BeanFilter>> beanFilters = new ArrayList<Class<? extends BeanFilter>>();
	// 构造函数的参数
	private final List<BeanMethodParameter> constructorList = new ArrayList<BeanMethodParameter>();
	private final List<BeanProperties> propertiesList = new ArrayList<BeanProperties>();
	private final List<XmlBeanMethodInfo> initMethodList = new ArrayList<XmlBeanMethodInfo>();
	private final List<XmlBeanMethodInfo> destroyMethodList = new ArrayList<XmlBeanMethodInfo>();
	private XmlBeanMethodInfo factoryMethodInfo;
	private final boolean proxy;

	private final Constructor<?> constructor;
	private final Class<?>[] constructorParameterTypes;
	private BeanMethodParameter[] beanMethodParameters;

	@SuppressWarnings("unchecked")
	public XmlBean(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Node beanNode) throws Exception {
		this.beanFactory = beanFactory;
		this.propertiesFactory = propertiesFactory;

		Node classNode = beanNode.getAttributes().getNamedItem(CLASS_ATTRIBUTE_KEY);
		Node nameNode = beanNode.getAttributes().getNamedItem(NAME_ATTRIBUTE_KEY);
		if (nameNode != null) {
			this.names = StringUtils.commonSplit(nameNode.getNodeValue());
		}

		String className = classNode == null ? null : classNode.getNodeValue();
		if (StringUtils.isNull(className)) {
			throw new BeansException("not found attribute [" + CLASS_ATTRIBUTE_KEY + "]");
		}

		this.type = Class.forName(className);

		Node singletonNode = beanNode.getAttributes().getNamedItem(SINGLETON_ATTRIBUTE_KEY);
		if (singletonNode != null) {
			String v = singletonNode.getNodeValue();
			this.singleton = StringUtils.isNull(v) ? true : Boolean.parseBoolean(v);
		} else {
			this.singleton = true;
		}

		Node idNode = beanNode.getAttributes().getNamedItem(ID_ATTRIBUTE_KEY);
		if (idNode == null) {
			this.id = ClassUtils.getCGLIBRealClassName(type);
		} else {
			String v = idNode.getNodeValue();
			this.id = StringUtils.isNull(v) ? ClassUtils.getCGLIBRealClassName(type) : v;
		}

		Node filtersNode = beanNode.getAttributes().getNamedItem(FILTERS_ATTRIBUTE_KEY);
		String[] filters = null;
		if (filtersNode != null) {
			filters = StringUtils.commonSplit(filtersNode.getNodeValue());
		}

		if (filters != null) {
			for (String f : filters) {
				beanFilters.add((Class<? extends BeanFilter>) Class.forName(f));
			}
		}

		// constructor
		NodeList nodeList = beanNode.getChildNodes();
		for (int a = 0; a < nodeList.getLength(); a++) {
			Node n = nodeList.item(a);
			if (CONSTRUCTOR_TAG_NAME.equalsIgnoreCase(n.getNodeName())) {// Constructor
				XmlBeanParameters xmlBeanParameters = new XmlBeanParameters(n);
				List<BeanMethodParameter> list = xmlBeanParameters.getParameters();
				if (list != null) {
					constructorList.addAll(list);
				}
			} else if (PROPERTIES_TAG_NAME.equalsIgnoreCase(n.getNodeName())) {// Properties
				XmlBeanProperties xmlBeanParameters = new XmlBeanProperties(n);
				List<BeanProperties> list = xmlBeanParameters.getProperties();
				if (list != null) {
					propertiesList.addAll(list);
				}
			} else if (INIT_METHOD_TAG_NAME.equalsIgnoreCase(n.getNodeName())) {// InitMethod
				XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(type, n);
				initMethodList.add(xmlBeanMethodInfo);
			} else if (DESTROY_METHOD_TAG_NAME.equalsIgnoreCase(n.getNodeName())) {// DestroyMethod
				XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(type, n);
				destroyMethodList.add(xmlBeanMethodInfo);
			} else if (FACTORY_METHOD_TAG_NAME.equalsIgnoreCase(n.getNodeName())) {
				if (factoryMethodInfo != null) {
					throw new BeansException("只能有一个factory-method");
				}
				this.factoryMethodInfo = new XmlBeanMethodInfo(type, n);
			}
		}

		this.proxy = checkProxy();
		this.constructor = getConstructor();
		this.constructorParameterTypes = constructor.getParameterTypes();
	}

	private Constructor<?> getConstructor() {
		if (constructorList == null) {
			return getConstructorByParameterTypes();
		} else {
			for (Constructor<?> constructor : type.getDeclaredConstructors()) {
				BeanMethodParameter[] beanMethodParameters = BeanUtils.sortParameters(constructor, constructorList);
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

	private boolean checkProxy() {
		if (Modifier.isFinal(type.getModifiers())) {
			return false;
		}

		if (beanFilters != null && !beanFilters.isEmpty()) {
			return true;
		}

		Controller controller = type.getAnnotation(Controller.class);
		if (controller != null) {
			return true;
		}

		Service service = type.getAnnotation(Service.class);
		if (service != null) {
			return true;
		}

		Transaction transaction = type.getAnnotation(Transaction.class);
		if (transaction != null) {
			return true;
		}

		for (Method method : type.getDeclaredMethods()) {
			Transaction t = method.getAnnotation(Transaction.class);
			if (t != null) {
				return true;
			}
		}
		return false;
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
		Enhancer enhancer = new Enhancer();
		List<BeanFilter> list = null;
		if (beanFilters != null && !beanFilters.isEmpty()) {
			list = new ArrayList<BeanFilter>();

			for (Class<? extends BeanFilter> f : beanFilters) {
				list.add(beanFactory.get(f));
			}
		}

		enhancer.setCallback(new BeanMethodInterceptor(type, list));
		enhancer.setSuperclass(type);
		return enhancer;
	}

	private Object createProxyInstance() throws Exception {
		Enhancer enhancer = getProxyEnhancer();
		if (constructorList == null || constructorList.isEmpty()) {
			return enhancer.create();
		} else {
			Object[] args = BeanUtils.getBeanMethodParameterArgs(beanMethodParameters, beanFactory, propertiesFactory);
			return enhancer.create(constructorParameterTypes, args);
		}
	}

	private void setProperties(Object bean)
			throws Exception {
		if (propertiesList == null || propertiesList.isEmpty()) {
			return;
		}

		for (BeanProperties beanProperties : propertiesList) {
			ClassInfo classInfo = new ClassInfo(type);
			FieldInfo fieldInfo = classInfo.getFieldInfo(beanProperties.getName());
			if (fieldInfo != null) {
				Object value = null;
				switch (beanProperties.getType()) {
				case value:
					value = StringUtils.conversion(beanProperties.getValue(), fieldInfo.getType());
					break;
				case ref:
					value = beanFactory.get(beanProperties.getValue());
					break;
				case property:
					value = propertiesFactory.getProperties(beanProperties.getValue(), fieldInfo.getType());
					break;
				default:
					break;
				}
				fieldInfo.set(bean, value);
			}
		}
	}

	private Object createInstance()
			throws Exception {
		if (constructorParameterTypes == null || constructorParameterTypes.length == 0) {
			return constructor.newInstance();
		} else {
			Object[] args = BeanUtils.getBeanMethodParameterArgs(beanMethodParameters, beanFactory, propertiesFactory);
			return constructor.newInstance(args);
		}
	}

	public void autowrite(Object bean) throws Exception {
		setProperties(bean);
		Class<?> tempClz = type;
		while (tempClz != null) {
			for (Field field : tempClz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				BeanUtils.setBean(beanFactory, tempClz, bean, field);
				BeanUtils.setProxy(beanFactory, tempClz, bean, field);
				BeanUtils.setConfig(beanFactory, tempClz, bean, field);
				BeanUtils.setProperties(propertiesFactory, tempClz, bean, field);
			}
			tempClz = tempClz.getSuperclass();
		}
	}

	public void init(Object bean) throws Exception {
		if (initMethodList != null && !initMethodList.isEmpty()) {
			for (XmlBeanMethodInfo method : initMethodList) {
				method.invoke(bean, beanFactory, propertiesFactory);
			}
		}
	}

	public void destroy(Object bean) throws Exception {
		if (destroyMethodList != null && !destroyMethodList.isEmpty()) {
			for (XmlBeanMethodInfo method : destroyMethodList) {
				method.invoke(bean, beanFactory, propertiesFactory);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		Object bean;
		try {
			if (isProxy()) {
				bean = createProxyInstance();
			} else {
				bean = createInstance();
			}

			if (factoryMethodInfo == null) {
				return (T) bean;
			} else {
				return (T) factoryMethodInfo.invoke(bean, beanFactory, propertiesFactory);
			}
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance(Class<?>[] parameterTypes, Object... args) {
		Object bean;
		try {
			if (isProxy()) {
				bean = getProxyEnhancer().create(parameterTypes, args);
			} else {
				bean = type.getConstructor(parameterTypes).newInstance(args);
			}

			if (factoryMethodInfo == null) {
				return (T) bean;
			} else {
				return (T) factoryMethodInfo.invoke(bean, beanFactory, propertiesFactory);
			}
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}

	public String[] getNames() {
		return names;
	}

}
