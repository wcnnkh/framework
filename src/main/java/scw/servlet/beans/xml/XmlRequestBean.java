package scw.servlet.beans.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.cglib.proxy.Enhancer;
import scw.beans.AnnotationBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanMethod;
import scw.beans.BeanUtils;
import scw.beans.property.PropertiesFactory;
import scw.beans.xml.XmlBeanMethodInfo;
import scw.beans.xml.XmlBeanParameter;
import scw.beans.xml.XmlBeanUtils;
import scw.core.exception.BeansException;
import scw.core.exception.NotFoundException;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.StringUtils;
import scw.servlet.beans.RequestBean;
import scw.servlet.beans.RequestBeanUtils;

public final class XmlRequestBean implements RequestBean {

	private final BeanFactory beanFactory;
	private final PropertiesFactory propertiesFactory;
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
	private final FieldDefinition[] autoWriteFields;

	public XmlRequestBean(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Node beanNode,
			String[] filterNames) throws Exception {
		this.beanFactory = beanFactory;
		this.propertiesFactory = propertiesFactory;

		Node classNode = beanNode.getAttributes().getNamedItem("class");
		Node nameNode = beanNode.getAttributes().getNamedItem("name");
		if (nameNode != null) {
			this.names = StringUtils.commonSplit(nameNode.getNodeValue());
		}

		String className = classNode == null ? null : classNode.getNodeValue();
		if (StringUtils.isNull(className)) {
			throw new BeansException("not found attribute [class]");
		}

		this.type = Class.forName(className);
		Node idNode = beanNode.getAttributes().getNamedItem("id");
		if (idNode == null) {
			this.id = type.getName();
		} else {
			String v = idNode.getNodeValue();
			this.id = StringUtils.isNull(v) ? type.getName() : v;
		}

		Node filtersNode = beanNode.getAttributes().getNamedItem("filters");
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
			if ("constructor".equalsIgnoreCase(n.getNodeName())) {// Constructor
				List<XmlBeanParameter> list = XmlBeanUtils.parseBeanParameterList(n);
				if (list != null) {
					constructorParameterList.addAll(list);
				}
			} else if ("properties".equalsIgnoreCase(n.getNodeName())) {// Properties
				List<XmlBeanParameter> list = XmlBeanUtils.parseBeanParameterList(n);
				if (list != null) {
					propertiesList.addAll(list);
				}
			} else if ("init".equalsIgnoreCase(n.getNodeName())) {// InitMethod
				XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(type, n);
				initMethodList.add(xmlBeanMethodInfo);
			} else if ("destroy".equalsIgnoreCase(n.getNodeName())) {// DestroyMethod
				XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(type, n);
				destroyMethodList.add(xmlBeanMethodInfo);
			} else if ("factory-method".equalsIgnoreCase(n.getNodeName())) {
				if (factoryMethodInfo != null) {
					throw new BeansException("只能有一个factory-method");
				}
				this.factoryMethodInfo = new XmlBeanMethodInfo(type, n);
			}
		}

		this.constructorParameters = constructorParameterList
				.toArray(new XmlBeanParameter[constructorParameterList.size()]);
		this.properties = propertiesList.toArray(new XmlBeanParameter[propertiesList.size()]);
		initMethodList.addAll(AnnotationBeanDefinition.getInitMethodList(type));
		this.initMethods = initMethodList.toArray(new BeanMethod[initMethodList.size()]);

		destroyMethodList.addAll(AnnotationBeanDefinition.getDestroyMethdoList(type));
		this.destroyMethods = destroyMethodList.toArray(new BeanMethod[destroyMethodList.size()]);

		this.proxy = BeanUtils.checkProxy(type, this.filterNames);
		this.constructor = getConstructor();
		if (constructor == null) {
			throw new NotFoundException(type.getName());
		}
		this.constructorParameterTypes = constructor.getParameterTypes();
		this.autoWriteFields = BeanUtils.getAutowriteFieldDefinitionList(type, false).toArray(new FieldDefinition[0]);
	}

	private Constructor<?> getConstructor() {
		if (constructorParameters.length == 0) {
			return ReflectUtils.getConstructor(type, false);
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

	private Object createProxyInstance(ServletRequest request) throws Exception {
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
			Field field = ReflectUtils.getField(type, beanProperties.getName(), true);
			if (field == null) {
				continue;
			}

			ReflectUtils.setFieldValue(type, field, bean,
					beanProperties.parseValue(beanFactory, propertiesFactory, field.getType()));
		}
	}

	private Object createInstance(ServletRequest request) throws Exception {
		if (constructorParameterTypes == null || constructorParameterTypes.length == 0) {
			return constructor.newInstance();
		} else {
			Object[] args = RequestBeanUtils.getBeanMethodParameterArgs(request, constructorParameterTypes,
					beanMethodParameters, beanFactory, propertiesFactory);
			return constructor.newInstance(args);
		}
	}

	public void autowrite(Object bean) throws Exception {
		for (FieldDefinition fieldDefinition : autoWriteFields) {
			BeanUtils.autoWrite(type, beanFactory, propertiesFactory, bean, fieldDefinition);
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
	public <T> T newInstance(ServletRequest request) {
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
