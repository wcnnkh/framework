package shuchaowen.core.beans.xml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.Bean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.BeanInfoConfiguration;
import shuchaowen.core.beans.ConfigFactory;
import shuchaowen.core.beans.exception.BeansException;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.ConfigUtils;

public class XmlBeanInfoConfiguration implements BeanInfoConfiguration {
	private static final String BEANS_TAG_NAME = "beans";
	private static final String BEAN_TAG_NAME = "bean";
	private volatile Map<String, Bean> beanMap = new HashMap<String, Bean>();

	public XmlBeanInfoConfiguration(BeanFactory beanFactory, ConfigFactory configFactory, String beanXml)
			throws Exception {
		File xml = ConfigUtils.getFile(beanXml);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
		Document document = builder.parse(xml);
		Element root = document.getDocumentElement();
		if (!BEANS_TAG_NAME.equals(root.getTagName())) {
			throw new BeansException("root tag name error [" + root.getTagName() + "]");
		}

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
