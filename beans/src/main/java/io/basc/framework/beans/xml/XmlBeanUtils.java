package io.basc.framework.beans.xml;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ioc.IocProcessor;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.env.Environment;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class XmlBeanUtils {
	private XmlBeanUtils() {
	};

	public static XmlBeanParameter parseBeanParameter(Node node, ClassLoader classLoader)
			throws ClassNotFoundException {
		String name = DomUtils.getNodeAttributeValue(node, "name");
		String ref = DomUtils.getNodeAttributeValue(node, "ref");
		String value = DomUtils.getNodeAttributeValue(node, "value");
		String type = DomUtils.getNodeAttributeValue(node, "type");
		String property = DomUtils.getNodeAttributeValue(node, "property");

		Class<?> typeClass = StringUtils.isEmpty(type) ? null : ClassUtils
				.forName(type, classLoader);
		if (!StringUtils.isEmpty(ref)) {
			return new XmlBeanParameter(XmlParameterType.ref, typeClass, name,
					ref, node);
		} else if (!StringUtils.isEmpty(property)) {
			return new XmlBeanParameter(XmlParameterType.property, typeClass,
					name, property, node);
		} else {
			if (StringUtils.isEmpty(value)) {
				value = node.getNodeValue();
			}
			return new XmlBeanParameter(XmlParameterType.value, typeClass, name,
					value, node);
		}
	}

	public static Boolean isSingleton(Node node) {
		return DomUtils.getBooleanValue(node, "singleton", null);
	}

	public static boolean getBooleanValue(Environment environment,
			Node node, String name, boolean defaultValue) {
		String value = DomUtils.getNodeAttributeValue(environment, node,
				name);
		return StringUtils.isEmpty(value) ? defaultValue : Boolean
				.parseBoolean(value);
	}

	public static int getIntegerValue(Environment environment,
			Node node, String name, int defaultValue) {
		String value = DomUtils.getNodeAttributeValue(environment, node,
				name);
		return StringUtils.isEmpty(value) ? defaultValue : Integer
				.parseInt(value);
	}

	public static List<XmlBeanParameter> parseBeanParameterList(Node node, ClassLoader classLoader)
			throws ClassNotFoundException {
		List<XmlBeanParameter> xmlBeanParameters = new ArrayList<XmlBeanParameter>();
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nRoot = nodeList.item(i);
			if (nRoot.getNodeName().equals("parameter")) {
				if (nRoot.getAttributes() == null) {
					continue;
				}

				xmlBeanParameters.add(parseBeanParameter(nRoot, classLoader));
			}
		}
		return xmlBeanParameters;
	}

	public static NodeList getRootNodeList(Resource resource, ResourceLoader resourceLoader) {
		Document document = DomUtils.getDomBuilder().parse(resource);
		Node node = document.getDocumentElement();
		if (!"beans".equals(node.getNodeName())) {
			throw new BeansException("root tag name error ["
					+ node.getNodeName() + "]");
		}
		return DomUtils.getChildNodes(node, resourceLoader);
	}

	public static String getPackageName(Environment environment,
			Node node) {
		return DomUtils.getNodeAttributeValue(environment, node, "package");
	}

	public static String getVersion(Environment environment, Node node) {
		return DomUtils.getNodeAttributeValue(environment, node, "version");
	}

	public static String getAddress(Environment environment, Node node) {
		return DomUtils.getRequireNodeAttributeValue(environment, node,
				"address");
	}

	public static String getRef(Environment environment, Node node) {
		return DomUtils.getRequireNodeAttributeValue(environment, node,
				"ref");
	}

	public static String getCharsetName(Environment environment,
			Node node, String defaultValue) {
		String charsetName = DomUtils.getNodeAttributeValue(environment,
				node, "charsetName");
		return StringUtils.isEmpty(charsetName) ? defaultValue : charsetName;
	}

	public static String getCharsetName(Node node, String defaultValue) {
		String charsetName = DomUtils
				.getNodeAttributeValue(node, "charsetName");
		return StringUtils.isEmpty(charsetName) ? defaultValue : charsetName;
	}

	public static List<XmlMethodIocProcessor> getMethodIocProcessos(Class<?> clz,
			NodeList nodeList, String tagName, ClassLoader classLoader) throws Exception {
		List<XmlMethodIocProcessor> list = new ArrayList<XmlMethodIocProcessor>();
		for (int a = 0; a < nodeList.getLength(); a++) {
			Node n = nodeList.item(a);
			if (tagName.equalsIgnoreCase(n.getNodeName())) {
				XmlMethodIocProcessor xmlBeanMethod = new XmlMethodIocProcessor(
						clz, n, classLoader);
				list.add(xmlBeanMethod);
			}
		}
		return list;
	}

	public static List<XmlMethodIocProcessor> getInitMethodIocProcessors(
			Class<?> clz, NodeList nodeList, ClassLoader classLoader) throws Exception {
		return XmlBeanUtils.getMethodIocProcessos(clz, nodeList, "init", classLoader);
	}

	public static List<XmlMethodIocProcessor> getDestroyMethodIocProcessors(
			Class<?> clz, NodeList nodeList, ClassLoader classLoader) throws Exception {
		return XmlBeanUtils.getMethodIocProcessos(clz, nodeList, "destroy", classLoader);
	}

	public static XmlBeanParameter[] getConstructorParameters(NodeList nodeList, ClassLoader classLoader)
			throws Exception {
		List<XmlBeanParameter> constructorParameterList = new ArrayList<XmlBeanParameter>();
		for (int a = 0; a < nodeList.getLength(); a++) {
			Node n = nodeList.item(a);
			if ("constructor".equalsIgnoreCase(n.getNodeName())) {// Constructor
				List<XmlBeanParameter> list = parseBeanParameterList(n, classLoader);
				if (list != null) {
					constructorParameterList.addAll(list);
				}
			}
		}

		return CollectionUtils.isEmpty(constructorParameterList) ? null
				: constructorParameterList
						.toArray(new XmlBeanParameter[constructorParameterList
								.size()]);
	}

	public static Collection<IocProcessor> getBeanPropertiesIocProcessors(
			Class<?> targetClass, NodeList nodeList, ClassLoader classLoader)
			throws ClassNotFoundException {
		List<IocProcessor> iocProcessors = new ArrayList<IocProcessor>();
		for (int a = 0; a < nodeList.getLength(); a++) {
			Node n = nodeList.item(a);
			if ("properties".equalsIgnoreCase(n.getNodeName())) {// Properties
				List<XmlBeanParameter> list = XmlBeanUtils
						.parseBeanParameterList(n, classLoader);
				if (list != null) {
					for (XmlBeanParameter xmlBeanParameter : list) {
						iocProcessors.add(new XmlPropertiesIocProcessor(
								targetClass, xmlBeanParameter));
					}
				}
			}
		}
		return iocProcessors;
	}

	public static TimeUnit getTimeUnit(Node node) {
		String format = DomUtils.getNodeAttributeValue(node, "unit");
		TimeUnit timeUnit = TimeUnit.MINUTES;
		if (StringUtils.isNotEmpty(format)) {
			timeUnit = TimeUnit.valueOf(format.toUpperCase());
		}
		return timeUnit;
	}

	public static String getClassName(Node node, boolean require) {
		return require ? DomUtils.getRequireNodeAttributeValue(node, "class")
				: DomUtils.getNodeAttributeValue(node, "class");
	}

	public static Class<?> getClass(Node node, boolean require, ClassLoader classLoader)
			throws ClassNotFoundException {
		String className = getClassName(node, require);
		if (StringUtils.isEmpty(className)) {
			return null;
		}

		return ClassUtils.forName(className, classLoader);
	}
}
