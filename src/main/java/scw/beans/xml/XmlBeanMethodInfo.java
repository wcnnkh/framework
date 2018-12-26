package scw.beans.xml;

import java.lang.reflect.Method;
import java.util.List;

import org.w3c.dom.Node;

import scw.beans.BeanFactory;
import scw.beans.BeanMethod;
import scw.beans.BeanUtils;
import scw.beans.property.PropertiesFactory;
import scw.common.exception.BeansException;

public class XmlBeanMethodInfo implements BeanMethod {
	private static final String NAME_KEY = "name";
	private Method method;
	private XmlBeanParameter[] beanMethodParameters;

	public XmlBeanMethodInfo(Class<?> type, Node node) throws Exception {
		if (node.getAttributes() == null) {
			throw new BeansException("not found method name");
		}

		Node nameNode = node.getAttributes().getNamedItem(NAME_KEY);
		if (nameNode == null) {
			throw new BeansException("not found method name");
		}

		String name = nameNode.getNodeValue();
		List<XmlBeanParameter> xmlBeanParameters = XmlBeanUtils.parseBeanParameterList(node);
		Class<?> tempClz = type;
		while (tempClz != null) {
			for (Method method : tempClz.getDeclaredMethods()) {
				if (method.getParameterCount() != xmlBeanParameters.size()) {
					continue;
				}

				if (!method.getName().equals(name)) {
					continue;
				}

				XmlBeanParameter[] beanMethodParameters = BeanUtils.sortParameters(method, xmlBeanParameters);
				if (beanMethodParameters != null) {
					this.beanMethodParameters = beanMethodParameters;
					method.setAccessible(true);
					this.method = method;
				}
			}
			tempClz = tempClz.getSuperclass();
		}

		if (this.method == null) {
			throw new BeansException("not found method [" + name + "]");
		}
	}

	public Object invoke(Object bean, BeanFactory beanFactory, PropertiesFactory propertiesFactory) throws Exception {
		if (method.getParameterCount() == 0) {
			return method.invoke(bean);
		} else {
			Object[] args = BeanUtils.getBeanMethodParameterArgs(beanMethodParameters, beanFactory, propertiesFactory);
			return method.invoke(bean, args);
		}
	}

	public Method getMethod() {
		return method;
	}
}
