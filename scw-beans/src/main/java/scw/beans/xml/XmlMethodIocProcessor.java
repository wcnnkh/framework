package scw.beans.xml;

import java.lang.reflect.Method;
import java.util.List;

import org.w3c.dom.Node;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.beans.ioc.AbstractIocProcessor;
import scw.core.parameter.MethodParameterDescriptors;
import scw.core.reflect.ReflectionUtils;
import scw.json.JSONUtils;

public class XmlMethodIocProcessor extends AbstractIocProcessor {
	private Class<?> type;
	private XmlBeanParameter[] xmlBeanParameters;
	private String name;

	public XmlMethodIocProcessor(Class<?> type, Node node) throws Exception {
		if (node.getAttributes() == null) {
			throw new BeansException("not found method name");
		}

		Node nameNode = node.getAttributes().getNamedItem("name");
		if (nameNode == null) {
			throw new BeansException("not found method name");
		}

		this.name = nameNode.getNodeValue();
		this.type = type;
		List<XmlBeanParameter> xmlBeanParameters = XmlBeanUtils.parseBeanParameterList(node);
		this.xmlBeanParameters = xmlBeanParameters.toArray(new XmlBeanParameter[xmlBeanParameters.size()]);
	}

	public void process(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory) throws Exception {
		XmlParameterFactory xmlParameterFactory = new XmlParameterFactory(beanFactory,
				xmlBeanParameters);
		Class<?> tempClz = type;
		while (tempClz != null) {
			for (Method method : tempClz.getDeclaredMethods()) {
				if (method.getParameterCount() != xmlBeanParameters.length) {
					continue;
				}

				if (!method.getName().equals(name)) {
					continue;
				}
				
				if(!acceptModifiers(beanDefinition, bean, method.getModifiers())){
					continue;
				}

				MethodParameterDescriptors methodParameterDescriptors = new MethodParameterDescriptors(type, method);
				if (xmlParameterFactory.isAccept(methodParameterDescriptors)) {
					Object[] args = xmlParameterFactory.getParameters(methodParameterDescriptors);
					ReflectionUtils.makeAccessible(method);
					method.invoke(bean, args);
					return;
				}
			}
			tempClz = tempClz.getSuperclass();
		}

		throw new BeansException(type.getName() + " not found method [" + name + "] parameterTypes "
				+ JSONUtils.toJSONString(xmlBeanParameters));

	}
}
