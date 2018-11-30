package shuchaowen.core.beans.xml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.AnnotationBean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.BeanLifeCycle;
import shuchaowen.core.beans.BeanMethod;
import shuchaowen.core.beans.BeanUtils;
import shuchaowen.core.beans.property.PropertiesFactory;
import shuchaowen.core.exception.BeansException;
import shuchaowen.core.util.ClassInfo;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.FieldInfo;
import shuchaowen.core.util.StringUtils;

public class XmlBeanLifeCycle implements BeanLifeCycle{
	private static final String CLASS_ATTRIBUTE_KEY = "class";
	
	private static final String PROPERTIES_TAG_NAME = "properties";
	private static final String INIT_METHOD_TAG_NAME = "init";
	private static final String DESTROY_METHOD_TAG_NAME = "destroy";

	private final BeanFactory beanFactory;
	private final PropertiesFactory propertiesFactory;
	private final ClassInfo classInfo;
	private final Class<?> type;
	private final List<XmlBeanParameter> propertiesList = new ArrayList<XmlBeanParameter>();
	private final List<BeanMethod> initMethodList = new ArrayList<BeanMethod>();
	private final List<BeanMethod> destroyMethodList = new ArrayList<BeanMethod>();

	public XmlBeanLifeCycle(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Node beanNode) throws Exception {
		this.beanFactory = beanFactory;
		this.propertiesFactory = propertiesFactory;

		Node classNode = beanNode.getAttributes().getNamedItem(CLASS_ATTRIBUTE_KEY);
		String className = classNode == null ? null : classNode.getNodeValue();
		if (StringUtils.isNull(className)) {
			throw new BeansException("not found attribute [" + CLASS_ATTRIBUTE_KEY + "]");
		}

		this.classInfo = ClassUtils.getClassInfo(className);
		this.type = classInfo.getClz();
		// constructor
		NodeList nodeList = beanNode.getChildNodes();
		for (int a = 0; a < nodeList.getLength(); a++) {
			Node n = nodeList.item(a);
			if (PROPERTIES_TAG_NAME.equalsIgnoreCase(n.getNodeName())) {// Properties
				List<XmlBeanParameter> list = XmlBeanUtils.parseBeanParameterList(n);
				if (list != null) {
					propertiesList.addAll(list);
				}
			} else if (INIT_METHOD_TAG_NAME.equalsIgnoreCase(n.getNodeName())) {// InitMethod
				XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(classInfo.getClz(), n);
				initMethodList.add(xmlBeanMethodInfo);
			} else if (DESTROY_METHOD_TAG_NAME.equalsIgnoreCase(n.getNodeName())) {// DestroyMethod
				XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(classInfo.getClz(), n);
				destroyMethodList.add(xmlBeanMethodInfo);
			}
		}

		this.initMethodList.addAll(AnnotationBean.getInitMethodList(type));
		this.destroyMethodList.addAll(AnnotationBean.getDestroyMethdoList(type));
	}

	private void setProperties(Object bean) throws Exception {
		if (propertiesList == null || propertiesList.isEmpty()) {
			return;
		}

		for (XmlBeanParameter beanProperties : propertiesList) {
			FieldInfo fieldInfo = classInfo.getFieldInfo(beanProperties.getName());
			if (fieldInfo != null) {
				Object value = beanProperties.parseValue(beanFactory, propertiesFactory, fieldInfo.getType());
				fieldInfo.set(bean, value);
			}
		}
	}

	public void autowrite(Object bean) throws Exception {
		for (Field field : classInfo.getClz().getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			FieldInfo fieldInfo = classInfo.getFieldInfo(field.getName());
			BeanUtils.setBean(beanFactory, type, bean, fieldInfo);
			BeanUtils.setProxy(beanFactory, type, bean, fieldInfo);
			BeanUtils.setConfig(beanFactory, type, bean, fieldInfo);
			BeanUtils.setProperties(beanFactory, propertiesFactory, type, bean, fieldInfo);
		}
		setProperties(bean);
	}

	public void init(Object bean) throws Exception {
		if (initMethodList != null && !initMethodList.isEmpty()) {
			for (BeanMethod method : initMethodList) {
				method.invoke(bean, beanFactory, propertiesFactory);
			}
		}
	}

	public void destroy(Object bean) throws Exception {
		if (destroyMethodList != null && !destroyMethodList.isEmpty()) {
			for (BeanMethod method : destroyMethodList) {
				method.invoke(bean, beanFactory, propertiesFactory);
			}
		}
	}
}
