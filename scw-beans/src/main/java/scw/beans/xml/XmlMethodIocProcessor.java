package scw.beans.xml;

import java.lang.reflect.Method;
import java.util.List;

import org.w3c.dom.Node;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.beans.ioc.AbstractIocProcessor;
import scw.core.parameter.ExecutableParameterDescriptors;
import scw.core.parameter.ParameterDescriptors;
import scw.core.reflect.ReflectionUtils;
import scw.json.JSONUtils;

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
