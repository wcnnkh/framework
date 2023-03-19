package io.basc.framework.context.xml;

import java.lang.reflect.Method;
import java.util.List;

import org.w3c.dom.Node;

import io.basc.framework.context.Context;
import io.basc.framework.context.ioc.IocProcessor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeansException;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.json.JsonUtils;
import io.basc.framework.mapper.ExecutableParameterDescriptors;
import io.basc.framework.mapper.ParameterDescriptors;

public class XmlMethodIocProcessor extends IocProcessor {
	private Class<?> type;
	private XmlBeanParameter[] xmlBeanParameters;
	private String name;

	public XmlMethodIocProcessor(Context context, Class<?> type, Node node) throws Exception {
		super(context);
		if (node.getAttributes() == null) {
			throw new BeansException("not found method name");
		}

		Node nameNode = node.getAttributes().getNamedItem("name");
		if (nameNode == null) {
			throw new BeansException("not found method name");
		}

		this.name = nameNode.getNodeValue();
		this.type = type;
		List<XmlBeanParameter> xmlBeanParameters = XmlBeanUtils.parseBeanParameterList(node, context.getClassLoader());
		this.xmlBeanParameters = xmlBeanParameters.toArray(new XmlBeanParameter[xmlBeanParameters.size()]);
	}

	@Override
	public void processPostBean(Object bean, BeanDefinition definition) throws FactoryException {
		XmlParametersFactory xmlParameterFactory = new XmlParametersFactory(getContext(), xmlBeanParameters);
		Class<?> tempClz = type;
		while (tempClz != null) {
			for (Method method : tempClz.getDeclaredMethods()) {
				if (method.getParameterCount() != xmlBeanParameters.length) {
					continue;
				}

				if (!method.getName().equals(name)) {
					continue;
				}

				if (!acceptModifiers(definition, bean, method.getModifiers())) {
					continue;
				}

				ParameterDescriptors parameterDescriptors = new ExecutableParameterDescriptors(type, method);
				if (xmlParameterFactory.isAccept(parameterDescriptors)) {
					Object[] args = xmlParameterFactory.getParameters(parameterDescriptors);
					ReflectionUtils.invoke(method, bean, args);
					return;
				}
			}
			tempClz = tempClz.getSuperclass();
		}

		throw new BeansException(type.getName() + " not found method [" + name + "] parameterTypes "
				+ JsonUtils.getSupport().toJsonString(xmlBeanParameters));

	}
}
