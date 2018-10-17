package shuchaowen.core.beans.xml;

import java.lang.reflect.Method;

import org.w3c.dom.Node;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.BeanMethodParameter;
import shuchaowen.core.beans.BeanUtils;
import shuchaowen.core.beans.PropertiesFactory;
import shuchaowen.core.beans.exception.BeansException;

public class XmlBeanMethodInfo {
	private static final String NAME_KEY = "name";
	private Method method;
	private final XmlBeanParameters parameter;
	private BeanMethodParameter[] beanMethodParameters;
	
	public XmlBeanMethodInfo(Class<?> type, Node node) throws Exception{
		if(node.getAttributes() == null){
			throw new BeansException("not found method name");
		}
		
		Node nameNode = node.getAttributes().getNamedItem(NAME_KEY);
		if(nameNode == null){
			throw new BeansException("not found method name");
		}
		
		String name = nameNode.getNodeValue();
		this.parameter = new XmlBeanParameters(node);
		for(Method method : type.getDeclaredMethods()){
			if(method.getParameterCount() != parameter.getParameters().size()){
				continue;
			}
			
			if(!method.getName().equals(name)){
				continue;
			}
			
			BeanMethodParameter[] beanMethodParameters = BeanUtils.sortParameters(method, parameter.getParameters());
			if(beanMethodParameters != null){
				this.beanMethodParameters = beanMethodParameters;
				method.setAccessible(true);
				this.method = method;
			}
		}
		
		if(this.method == null){
			throw new BeansException("not found method [" + name + "]");
		}
	}
	
	public Object invoke(Object bean, BeanFactory beanFactory, PropertiesFactory propertiesFactory) throws Exception{
		if(method.getParameterCount() == 0){
			return method.invoke(bean);
		}else{
			Object[] args = BeanUtils.getBeanMethodParameterArgs(beanMethodParameters, beanFactory, propertiesFactory);
			return method.invoke(bean, args);
 		}
	}
	
	public Method getMethod() {
		return method;
	}
	
	public XmlBeanParameters getParameter() {
		return parameter;
	}
}
