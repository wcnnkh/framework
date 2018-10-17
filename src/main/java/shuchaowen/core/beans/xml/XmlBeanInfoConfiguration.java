package shuchaowen.core.beans.xml;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.Bean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.BeanInfoConfiguration;
import shuchaowen.core.beans.ConfigFactory;
import shuchaowen.core.util.ClassUtils;

public class XmlBeanInfoConfiguration implements BeanInfoConfiguration {
	private static final String BEAN_TAG_NAME = "bean";
	private volatile Map<String, Bean> beanMap = new HashMap<String, Bean>();

	public XmlBeanInfoConfiguration(BeanFactory beanFactory, ConfigFactory configFactory, Node root) throws Exception{
		NodeList nhosts = root.getChildNodes();
		for (int i = 0; i < nhosts.getLength(); i++) {
			Node nRoot = nhosts.item(i);
			if (!BEAN_TAG_NAME.equals(nRoot.getNodeName())) {
				continue;
			}

			Bean bean = new XmlBean(beanFactory, configFactory, nRoot);
			beanMap.put(bean.getType().getName(), bean);
			if(!bean.getId().equals(bean.getType().getName())){
				beanMap.put(bean.getId(), bean);
			}
		}
	}

	public Bean getBean(Class<?> type) {
		return getBean(type.getName());
	}

	public Bean getBean(String name) {
		return beanMap.get(ClassUtils.getCGLIBRealClassName(name));
	}

	public boolean contains(String name) {
		return beanMap.containsKey(ClassUtils.getCGLIBRealClassName(name));
	}

}
