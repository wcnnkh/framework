package scw.beans.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.AbstractBeanConfigFactory;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.property.PropertiesFactory;
import scw.core.utils.StringUtils;

public class XmlBeanConfigFactory extends AbstractBeanConfigFactory {
	private static final String BEAN_TAG_NAME = "bean";
	private static final String BEAN_FACTORY_TAG_NAME = "factory";

	private List<String> beanFactoryList;

	public XmlBeanConfigFactory(BeanFactory beanFactory, PropertiesFactory propertiesFactory, String beanXml,
			String[] filterNames) throws Exception {
		Node root = XmlBeanUtils.getRootNode(beanXml);
		NodeList nhosts = root.getChildNodes();
		for (int i = 0; i < nhosts.getLength(); i++) {
			Node nRoot = nhosts.item(i);
			if (BEAN_TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())) {
				BeanDefinition beanDefinition = new XmlBeanDefinition(beanFactory, propertiesFactory, nRoot, filterNames);
				addBean(beanDefinition);
			} else if (BEAN_FACTORY_TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())) {
				Node node = nRoot.getAttributes().getNamedItem("value");
				String name = node == null ? null : node.getNodeValue();
				if (name == null) {
					name = nRoot.getNodeValue();
				}

				if (!StringUtils.isNull(name)) {
					if (beanFactoryList == null) {
						beanFactoryList = new ArrayList<String>();
					}
					beanFactoryList.add(name);
				}
			}
		}
	}
}
