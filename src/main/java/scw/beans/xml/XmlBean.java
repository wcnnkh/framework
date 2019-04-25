package scw.beans.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.cglib.proxy.Enhancer;
import scw.beans.AnnotationBean;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanMethod;
import scw.beans.BeanUtils;
import scw.beans.property.PropertiesFactory;
import scw.common.ClassInfo;
import scw.common.FieldInfo;
import scw.common.exception.BeansException;
import scw.common.exception.NotFoundException;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;
import scw.core.reflect.ReflectUtils;

public class XmlBean implements BeanDefinition {
	private static final String CLASS_ATTRIBUTE_KEY = "class";
	private static final String ID_ATTRIBUTE_KEY = "id";
	private static final String SINGLETON_ATTRIBUTE_KEY = "singleton";
	private static final String FILTERS_ATTRIBUTE_KEY = "filters";
	private static final String NAME_ATTRIBUTE_KEY = "name";// 别名

	private static final String CONSTRUCTOR_TAG_NAME = "constructor";
	private static final String PROPERTIES_TAG_NAME = "properties";
	private static final String INIT_METHOD_TAG_NAME = "init";
	private static final String DESTROY_METHOD_TAG_NAME = "destroy";

	private final BeanFactory beanFactory;
	private final PropertiesFactory propertiesFactory;
	private final Class<?> type;
	private final ClassInfo classInfo;
	private String[] names;
	private final String id;
	private final boolean singleton;
	private final String[] filterNames;
	// 构造函数的参数
	private final XmlBeanParameter[] constructorParameters;
	private final XmlBeanParameter[] properties;
	private final BeanMethod[] initMethods;
	private final BeanMethod[] destroyMethods;
	private BeanMethod factoryMethodInfo;
	private final boolean proxy;

	private final Constructor<?> constructor;
	private Class<?>[] constructorParameterTypes;
	private XmlBeanParameter[] beanMethodParameters;
	private Enhancer enhancer;

	public XmlBean(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Node beanNode, String[] filterNames)
			throws Exception {
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

		this.type = ClassUtils.forName(className);
		this.classInfo = ClassUtils.getClassInfo(type);
		Node singletonNode = beanNode.getAttributes().getNamedItem(SINGLETON_ATTRIBUTE_KEY);
		if (singletonNode != null) {
			String v = singletonNode.getNodeValue();
			this.singleton = StringUtils.isNull(v) ? true : Boolean.parseBoolean(v);
		} else {
			this.singleton = true;
		}

		Node idNode = beanNode.getAttributes().getNamedItem(ID_ATTRIBUTE_KEY);
		if (idNode == null) {
			this.id = type.getName();
		} else {
			String v = idNode.getNodeValue();
			this.id = StringUtils.isNull(v) ? classInfo.getName() : v;
		}

		Node filtersNode = beanNode.getAttributes().getNamedItem(FILTERS_ATTRIBUTE_KEY);
		String[] filters = null;
		if (filtersNode != null) {
			filters = StringUtils.commonSplit(filtersNode.getNodeValue());
		}

		List<String> beanFilters = new ArrayList<String>();
		if (filterNames != null) {
			for (String name : filterNames) {
				beanFilters.add(name);
			}
		}

		if (filters != null) {
			for (String f : filters) {
				beanFilters.add(f);
			}
		}
		this.filterNames = beanFilters.toArray(new String[beanFilters.size()]);

		List<XmlBeanParameter> propertiesList = new ArrayList<XmlBeanParameter>();
		List<XmlBeanParameter> constructorParameterList = new ArrayList<XmlBeanParameter>();
		// constructor
		NodeList nodeList = beanNode.getChildNodes();
		List<BeanMethod> initMethodList = XmlBeanUtils.getBeanMethodList(type, nodeList, INIT_METHOD_TAG_NAME);
		List<BeanMethod> destroyMethodList = XmlBeanUtils.getBeanMethodList(type, nodeList, DESTROY_METHOD_TAG_NAME);
		for (int a = 0; a < nodeList.getLength(); a++) {
			Node n = nodeList.item(a);
			if (CONSTRUCTOR_TAG_NAME.equalsIgnoreCase(n.getNodeName())) {// Constructor
				List<XmlBeanParameter> list = XmlBeanUtils.parseBeanParameterList(n);
				if (list != null) {
					constructorParameterList.addAll(list);
				}
			} else if (PROPERTIES_TAG_NAME.equalsIgnoreCase(n.getNodeName())) {// Properties
				List<XmlBeanParameter> list = XmlBeanUtils.parseBeanParameterList(n);
				if (list != null) {
					propertiesList.addAll(list);
				}
			}
		}
		this.properties = propertiesList.toArray(new XmlBeanParameter[propertiesList.size()]);
		this.constructorParameters = constructorParameterList
				.toArray(new XmlBeanParameter[constructorParameterList.size()]);

		initMethodList.addAll(AnnotationBean.getInitMethodList(type));
		destroyMethodList.addAll(AnnotationBean.getDestroyMethdoList(type));
		this.initMethods = initMethodList.toArray(new BeanMethod[initMethodList.size()]);
		this.destroyMethods = destroyMethodList.toArray(new BeanMethod[destroyMethodList.size()]);
		this.proxy = BeanUtils.checkProxy(type, this.filterNames);
		this.constructor = getConstructor();
		if (constructor == null) {
			throw new NotFoundException(type.getName() + "找不到对应的构造函数");
		}
		this.constructorParameterTypes = constructor.getParameterTypes();
	}

	private Constructor<?> getConstructor() {
		if (constructorParameters == null || constructorParameters.length == 0) {
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
		if (constructorParameters.length == 0) {
			return enhancer.create();
		} else {
			Object[] args = BeanUtils.getBeanMethodParameterArgs(beanMethodParameters, beanFactory, propertiesFactory);
			return enhancer.create(constructorParameterTypes, args);
		}
	}

	private void setProperties(Object bean) throws Exception {
		if (properties.length == 0) {
			return;
		}

		for (XmlBeanParameter beanProperties : properties) {
			FieldInfo fieldInfo = classInfo.getFieldInfo(beanProperties.getName());
			if (fieldInfo != null) {
				Object value = beanProperties.parseValue(beanFactory, propertiesFactory, fieldInfo.getType());
				fieldInfo.set(bean, value);
			}
		}
	}

	private Object createInstance() throws Exception {
		if (constructorParameterTypes == null || constructorParameterTypes.length == 0) {
			return constructor.newInstance();
		} else {
			Object[] args = BeanUtils.getBeanMethodParameterArgs(beanMethodParameters, beanFactory, propertiesFactory);
			return constructor.newInstance(args);
		}
	}

	public void autowrite(Object bean) throws Exception {
		for (Field field : type.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			BeanUtils.autoWrite(type, beanFactory, propertiesFactory, bean, classInfo.getFieldInfo(field.getName()));
		}
		setProperties(bean);
	}

	public void init(Object bean) throws Exception {
		if (initMethods != null && initMethods.length != 0) {
			for (BeanMethod method : initMethods) {
				method.invoke(bean, beanFactory, propertiesFactory);
			}
		}
	}

	public void destroy(Object bean) throws Exception {
		if (destroyMethods != null && destroyMethods.length != 0) {
			for (BeanMethod method : destroyMethods) {
				method.invoke(bean, beanFactory, propertiesFactory);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		Object bean = null;
		try {
			if (factoryMethodInfo == null || !Modifier.isStatic(factoryMethodInfo.getMethod().getModifiers())) {
				if (isProxy()) {
					bean = createProxyInstance();
				} else {
					bean = createInstance();
				}
			}
			return (T) bean;
		} catch (Exception e) {
			throw new BeansException(type.getName(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance(Class<?>[] parameterTypes, Object... args) {
		Object bean = null;
		try {
			if (factoryMethodInfo == null || !Modifier.isStatic(factoryMethodInfo.getMethod().getModifiers())) {
				if (isProxy()) {
					bean = getProxyEnhancer().create(parameterTypes, args);
				} else {
					bean = type.getConstructor(parameterTypes).newInstance(args);
				}
			}

			if (factoryMethodInfo != null) {
				bean = factoryMethodInfo.invoke(bean, beanFactory, propertiesFactory);
			}
			return (T) bean;
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}

	public String[] getNames() {
		return names;
	}

	public boolean isFactory() {
		return factoryMethodInfo != null;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance(Object... params) {
		Constructor<T> constructor = (Constructor<T>) ReflectUtils.findConstructorByParameters(getType(), params);
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
}
