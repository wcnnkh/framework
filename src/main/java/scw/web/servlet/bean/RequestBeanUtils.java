package scw.web.servlet.bean;

import javax.servlet.ServletRequest;

import scw.beans.BeanFactory;
import scw.beans.xml.XmlBeanParameter;
import scw.web.servlet.Request;

public final class RequestBeanUtils {
	private RequestBeanUtils(){};
	
	public static Object[] getBeanMethodParameterArgs(Request request, Class<?>[] constructorParameterTypes, XmlBeanParameter[] beanParameters, BeanFactory beanFactory,
			scw.beans.property.PropertiesFactory propertiesFactory) throws Exception {
		Object[] args = new Object[beanParameters.length];
		for (int i = 0; i < args.length; i++) {
			if(ServletRequest.class.isAssignableFrom(constructorParameterTypes[i])){
				args[i] = request;
			}else{
				XmlBeanParameter xmlBeanParameter = beanParameters[i];
				args[i] = xmlBeanParameter.parseValue(beanFactory, propertiesFactory);	
			}
		}
		return args;
	}
}
