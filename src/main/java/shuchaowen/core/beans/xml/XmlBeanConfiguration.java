package shuchaowen.core.beans.xml;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.Bean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.BeanInfoConfiguration;
import shuchaowen.core.beans.ConfigFactory;
import shuchaowen.core.beans.exception.BeansException;
import shuchaowen.core.util.ClassUtils;

public class XmlBeanConfiguration implements BeanInfoConfiguration {
	private static final String BEAN_TAG_NAME = "bean";
	private final Map<String, Bean> beanMap = new HashMap<String, Bean>();
	private final Map<String, String> nameMappingMap = new HashMap<String, String>();
	
	public XmlBeanConfiguration(BeanFactory beanFactory, ConfigFactory configFactory, Node root) throws Exception{
		NodeList nhosts = root.getChildNodes();
		for (int i = 0; i < nhosts.getLength(); i++) {
			Node nRoot = nhosts.item(i);
			if (!BEAN_TAG_NAME.equals(nRoot.getNodeName())) {
				continue;
			}

			Bean bean = new XmlBean(beanFactory, configFactory, nRoot);
			if(beanMap.containsKey(bean.getId())){
				throw new BeansException(bean.getId() + " Already exist");
			}
			beanMap.put(bean.getId(), bean);
			
			if(bean.getNames() != null){
				for(String n : bean.getNames()){
					if(nameMappingMap.containsKey(n)){
						throw new BeansException(n + " Already exist");
					}
					nameMappingMap.put(n, bean.getId());
				}
			}
		}
	}

	public Bean getBean(Class<?> type) {
		return getBean(type.getName());
	}

	public Bean getBean(String name) {
		String realName = ClassUtils.getCGLIBRealClassName(name);
		String v = nameMappingMap.get(realName);
		return v == null? beanMap.get(realName):beanMap.get(v);
	}

	public boolean contains(String name) {
		String realName = ClassUtils.getCGLIBRealClassName(name);
		return nameMappingMap.containsKey(realName) || beanMap.containsKey(realName);
	}

}
