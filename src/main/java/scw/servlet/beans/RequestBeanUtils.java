package scw.servlet.beans;

import javax.servlet.ServletRequest;

import scw.beans.BeanFactory;
import scw.beans.xml.XmlBeanParameter;

public final class RequestBeanUtils {
	private RequestBeanUtils() {
	};

	public static Object[] getBeanMethodParameterArgs(ServletRequest request,
			Class<?>[] constructorParameterTypes, XmlBeanParameter[] beanParameters, BeanFactory beanFactory,
			scw.core.PropertiesFactory propertiesFactory) throws Exception {
		Object[] args = new Object[beanParameters.length];
		for (int i = 0; i < args.length; i++) {
			if (ServletRequest.class.isAssignableFrom(constructorParameterTypes[i])) {
				args[i] = request;
			} else {
				XmlBeanParameter xmlBeanParameter = beanParameters[i];
				args[i] = xmlBeanParameter.parseValue(beanFactory, propertiesFactory);
			}
		}
		return args;
	}
}
