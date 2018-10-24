package shuchaowen.core.beans.xml;

import java.lang.reflect.Method;
import java.util.List;

import org.w3c.dom.Node;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.BeanMethod;
import shuchaowen.core.beans.BeanParameter;
import shuchaowen.core.beans.BeanUtils;
import shuchaowen.core.beans.PropertiesFactory;
import shuchaowen.core.beans.exception.BeansException;

public class XmlBeanMethodInfo implements BeanMethod{
	private static final String NAME_KEY = "name";
	private Method method;
	private BeanParameter[] beanMethodParameters;
	
	public XmlBeanMethodInfo(Class<?> type, Node node) throws Exception{
		if(node.getAttributes() == null){
			throw new BeansException("not found method name");
		}
		
		Node nameNode = node.getAttributes().getNamedItem(NAME_KEY);
		if(nameNode == null){
			throw new BeansException("not found method name");
		}
		
		String name = nameNode.getNodeValue();
		List<BeanParameter> beanParameters = XmlBeanUtils.parseBeanParameterList(node);
		Class<?> tempClz = type;
		while(tempClz != null){
			for(Method method : tempClz.getDeclaredMethods()){
				if(method.getParameterCount() != beanParameters.size()){
					continue;
				}
				
				if(!method.getName().equals(name)){
					continue;
				}
				
				BeanParameter[] beanMethodParameters = BeanUtils.sortParameters(method, beanParameters);
				if(beanMethodParameters != null){
					this.beanMethodParameters = beanMethodParameters;
					method.setAccessible(true);
					this.method = method;
				}
			}
			tempClz = tempClz.getSuperclass();
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
}
