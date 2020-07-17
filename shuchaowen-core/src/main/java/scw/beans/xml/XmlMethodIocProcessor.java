package scw.beans.xml;

import java.lang.reflect.Method;
import java.util.List;

import org.w3c.dom.Node;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.BeansException;
import scw.beans.ioc.MethodIocProcessor;
import scw.json.JSONUtils;
import scw.value.property.PropertyFactory;

public class XmlMethodIocProcessor extends MethodIocProcessor {
	private Method method;
	private XmlBeanParameter[] beanMethodParameters;

	public XmlMethodIocProcessor(Class<?> type, Node node) throws Exception {
		if (node.getAttributes() == null) {
			throw new BeansException("not found method name");
		}

		Node nameNode = node.getAttributes().getNamedItem("name");
		if (nameNode == null) {
			throw new BeansException("not found method name");
		}

		String name = nameNode.getNodeValue();
		List<XmlBeanParameter> xmlBeanParameters = XmlBeanUtils.parseBeanParameterList(node);
		XmlBeanParameter[] parametetrs = xmlBeanParameters.toArray(new XmlBeanParameter[xmlBeanParameters.size()]);
		Class<?> tempClz = type;
		while (tempClz != null) {
			for (Method method : tempClz.getDeclaredMethods()) {
				if (method.getParameterCount() != parametetrs.length) {
					continue;
				}

				if (!method.getName().equals(name)) {
					continue;
				}

				XmlBeanParameter[] beanMethodParameters = BeanUtils.sortParameters(method, parametetrs);
				if (beanMethodParameters != null) {
					this.beanMethodParameters = beanMethodParameters;
					method.setAccessible(true);
					this.method = method;
				}
			}
			tempClz = tempClz.getSuperclass();
		}

		if (this.method == null) {
			throw new BeansException(type.getName() + " not found method [" + name + "] parameterTypes "
					+ JSONUtils.toJSONString(xmlBeanParameters));
		}

		checkMethod();
	}

	public void process(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception {
		if (method.getParameterCount() == 0) {
			method.invoke(bean);
		} else {
			Object[] args = BeanUtils.getBeanMethodParameterArgs(beanMethodParameters, beanFactory, propertyFactory);
			method.invoke(bean, args);
		}
	}

	public Method getMethod() {
		return method;
	}
}
