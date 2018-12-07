package shuchaowen.beans.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.beans.AbstractBeanFactory;
import shuchaowen.beans.Bean;
import shuchaowen.beans.BeanFactory;
import shuchaowen.beans.property.PropertiesFactory;
import shuchaowen.common.exception.AlreadyExistsException;
import shuchaowen.core.util.StringUtils;

public class XmlBeanFactory extends AbstractBeanFactory {
	private static final String BEANS_ANNOTATION = "packages";
	private static final String BEAN_TAG_NAME = "bean";
	private static final String BEAN_FACTORY_TAG_NAME = "factory";

	private String packageNames;
	private List<String> beanFactoryList;

	public XmlBeanFactory(BeanFactory beanFactory, PropertiesFactory propertiesFactory, String beanXml) throws Exception {
		Node root = XmlBeanUtils.getRootNode(beanXml);
		if (root.getAttributes() != null) {
			Node annotationNode = root.getAttributes().getNamedItem(BEANS_ANNOTATION);
			if (annotationNode != null) {
				this.packageNames = annotationNode.getNodeValue();
			}
		}

		NodeList nhosts = root.getChildNodes();
		for (int i = 0; i < nhosts.getLength(); i++) {
			Node nRoot = nhosts.item(i);
			if (BEAN_TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())) {
				Bean bean = new XmlBean(beanFactory, propertiesFactory, nRoot);
				putBean(bean.getId(), bean);

				if (bean.getNames() != null) {
					for (String n : bean.getNames()) {
						if(!registerNameMapping(n, bean.getId())){
							throw new AlreadyExistsException(n);
						}
					}
				}
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

	public String getPackageNames() {
		return packageNames;
	}

	@Override
	protected Bean newBean(String name) {
		return null;
	}
}
