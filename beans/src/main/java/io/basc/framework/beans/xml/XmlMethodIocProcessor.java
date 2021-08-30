package io.basc.framework.beans.xml;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ioc.AbstractIocProcessor;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.parameter.ExecutableParameterDescriptors;
import io.basc.framework.parameter.ParameterDescriptors;
import io.basc.framework.reflect.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;

import org.w3c.dom.Node;

public class XmlMethodIocProcessor extends AbstractIocProcessor {
	private Class<?> type;
	private XmlBeanParameter[] xmlBeanParameters;
	private String name;

	public XmlMethodIocProcessor(Class<?> type, Node node, ClassLoader classLoader) throws Exception {
		if (node.getAttributes() == null) {
			throw new BeansException("not found method name");
		}

		Node nameNode = node.getAttributes().getNamedItem("name");
		if (nameNode == null) {
			throw new BeansException("not found method name");
		}

		this.name = nameNode.getNodeValue();
		this.type = type;
		List<XmlBeanParameter> xmlBeanParameters = XmlBeanUtils.parseBeanParameterList(node, classLoader);
		this.xmlBeanParameters = xmlBeanParameters.toArray(new XmlBeanParameter[xmlBeanParameters.size()]);
	}

	public void process(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory) throws BeansException {
		XmlParametersFactory xmlParameterFactory = new XmlParametersFactory(beanFactory,
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

				ParameterDescriptors parameterDescriptors = new ExecutableParameterDescriptors(type, method);
				if (xmlParameterFactory.isAccept(parameterDescriptors)) {
					Object[] args = xmlParameterFactory.getParameters(parameterDescriptors);
					ReflectionUtils.makeAccessible(method);
					ReflectionUtils.invokeMethod(method, bean, args);
					return;
				}
			}
			tempClz = tempClz.getSuperclass();
		}

		throw new BeansException(type.getName() + " not found method [" + name + "] parameterTypes "
				+ JSONUtils.getJsonSupport().toJSONString(xmlBeanParameters));

	}
}
