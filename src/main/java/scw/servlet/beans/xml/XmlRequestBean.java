package scw.servlet.beans.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.AnnotationBean;
import scw.beans.BeanFactory;
import scw.beans.BeanMethod;
import scw.beans.BeanUtils;
import scw.beans.property.PropertiesFactory;
import scw.beans.xml.XmlBeanMethodInfo;
import scw.beans.xml.XmlBeanParameter;
import scw.beans.xml.XmlBeanUtils;
import scw.core.ClassInfo;
import scw.core.FieldInfo;
import scw.core.exception.BeansException;
import scw.core.exception.NotFoundException;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.servlet.Request;
import scw.servlet.beans.RequestBean;
import scw.servlet.beans.RequestBeanUtils;

public final class XmlRequestBean implements RequestBean {
	private static final String CLASS_ATTRIBUTE_KEY = "class";
	private static final String ID_ATTRIBUTE_KEY = "id";
	private static final String FILTERS_ATTRIBUTE_KEY = "filters";
	private static final String NAME_ATTRIBUTE_KEY = "name";// 别名

	private static final String CONSTRUCTOR_TAG_NAME = "constructor";
	private static final String PROPERTIES_TAG_NAME = "properties";
	private static final String INIT_METHOD_TAG_NAME = "init";
	private static final String DESTROY_METHOD_TAG_NAME = "destroy";
	private static final String FACTORY_METHOD_TAG_NAME = "factory-method";

	private final BeanFactory beanFactory;
	private final PropertiesFactory propertiesFactory;
	private final ClassInfo classInfo;
	private final Class<?> type;
	private String[] names;
	private final String id;
	private final String[] filterNames;
	// 构造函数的参数
	private final XmlBeanParameter[] constructorParameters;
	private final XmlBeanParameter[] properties;
	private final BeanMethod[] initMethods;
	private final BeanMethod[] destroyMethods;
	private BeanMethod factoryMethodInfo;
	private final boolean proxy;

	private final Constructor<?> constructor;
	private final Class<?>[] constructorParameterTypes;
	private XmlBeanParameter[] beanMethodParameters;
	private Enhancer enhancer;

	public XmlRequestBean(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Node beanNode,
			String[] filterNames) throws Exception {
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

		this.classInfo = ClassUtils.getClassInfo(className);
		this.type = classInfo.getClz();

		Node idNode = beanNode.getAttributes().getNamedItem(ID_ATTRIBUTE_KEY);
		if (idNode == null) {
			this.id = classInfo.getName();
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
			for (String n : filterNames) {
				beanFilters.add(n);
			}
		}

		if (filters != null) {
			for (String f : filters) {
				beanFilters.add(f);
			}
		}
		this.filterNames = beanFilters.toArray(new String[beanFilters.size()]);

		List<BeanMethod> initMethodList = new ArrayList<BeanMethod>();
		List<BeanMethod> destroyMethodList = new ArrayList<BeanMethod>();

		List<XmlBeanParameter> propertiesList = new ArrayList<XmlBeanParameter>();
		List<XmlBeanParameter> constructorParameterList = new ArrayList<XmlBeanParameter>();
		// constructor
		NodeList nodeList = beanNode.getChildNodes();
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

		this.constructorParameters = constructorParameterList
				.toArray(new XmlBeanParameter[constructorParameterList.size()]);
		this.properties = propertiesList.toArray(new XmlBeanParameter[propertiesList.size()]);
		initMethodList.addAll(AnnotationBean.getInitMethodList(type));
		this.initMethods = initMethodList.toArray(new BeanMethod[initMethodList.size()]);

		destroyMethodList.addAll(AnnotationBean.getDestroyMethdoList(type));
		this.destroyMethods = destroyMethodList.toArray(new BeanMethod[destroyMethodList.size()]);

		this.proxy = BeanUtils.checkProxy(type, this.filterNames);
		this.constructor = getConstructor();
		if(constructor == null){
			throw new NotFoundException(type.getName());
		}
		this.constructorParameterTypes = constructor.getParameterTypes();
	}

	private Constructor<?> getConstructor() {
		if (constructorParameters.length == 0) {
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

	private Enhancer getProxyEnhancer() {
		if (enhancer == null) {
			enhancer = BeanUtils.createEnhancer(type, beanFactory, filterNames);
		}
		return enhancer;
	}

	private Object createProxyInstance(Request request) throws Exception {
		Enhancer enhancer = getProxyEnhancer();
		if (constructorParameters.length == 0) {
			return enhancer.create();
		} else {
			Object[] args = RequestBeanUtils.getBeanMethodParameterArgs(request, constructorParameterTypes,
					beanMethodParameters, beanFactory, propertiesFactory);
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
				if (value != null) {
					fieldInfo.set(bean, value);
				}
			}
		}
	}

	private Object createInstance(Request request) throws Exception {
		if (constructorParameterTypes == null || constructorParameterTypes.length == 0) {
			return constructor.newInstance();
		} else {
			Object[] args = RequestBeanUtils.getBeanMethodParameterArgs(request, constructorParameterTypes,
					beanMethodParameters, beanFactory, propertiesFactory);
			return constructor.newInstance(args);
		}
	}

	public void autowrite(Object bean) throws Exception {
		for (Field field : type.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			BeanUtils.autoWrite(classInfo.getClz(), beanFactory, propertiesFactory, bean,
					classInfo.getFieldInfo(field.getName()));
		}
		setProperties(bean);
	}

	public void init(Object bean) throws Exception {
		if (initMethods.length != 0) {
			for (BeanMethod method : initMethods) {
				method.invoke(bean, beanFactory, propertiesFactory);
			}
		}
	}

	public void destroy(Object bean) throws Exception {
		if (destroyMethods.length != 0) {
			for (BeanMethod method : destroyMethods) {
				method.invoke(bean, beanFactory, propertiesFactory);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance(Request request) {
		Object bean;
		try {
			if (proxy) {
				bean = createProxyInstance(request);
			} else {
				bean = createInstance(request);
			}

			if (factoryMethodInfo == null) {
				return (T) bean;
			} else {
				return (T) factoryMethodInfo.invoke(bean, beanFactory, propertiesFactory);
			}
		} catch (Exception e) {
			throw new BeansException(type.getName(), e);
		}
	}

	public String[] getNames() {
		return names;
	}
}
