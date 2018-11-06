package shuchaowen.core.beans.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.AbstractBeanFactory;
import shuchaowen.core.beans.Bean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.PropertiesFactory;
import shuchaowen.core.exception.AlreadyExistsException;
import shuchaowen.core.exception.BeansException;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.StringUtils;

public class XmlBeanFactory extends AbstractBeanFactory {
	private static final String BEANS_TAG_NAME = "beans";
	private static final String BEANS_ANNOTATION = "packages";
	private static final String BEAN_TAG_NAME = "bean";
	private static final String PROPERTIES_TAG_NAME = "properties";
	private static final String BEAN_FACTORY_TAG_NAME = "factory";

	private String packageNames;
	private PropertiesFactory propertiesFactory;
	private List<String> beanFactoryList;

	public XmlBeanFactory(BeanFactory beanFactory, String beanXml) throws Exception {
		File xml = ConfigUtils.getFile(beanXml);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
		Document document = builder.parse(xml);
		Element root = document.getDocumentElement();
		if (!BEANS_TAG_NAME.equals(root.getTagName())) {
			throw new BeansException("root tag name error [" + root.getTagName() + "]");
		}

		if (root.getAttributes() != null) {
			Node annotationNode = root.getAttributes().getNamedItem(BEANS_ANNOTATION);
			if (annotationNode != null) {
				this.packageNames = annotationNode.getNodeValue();
			}
		}

		NodeList nhosts = root.getChildNodes();
		List<XmlProperties> xmlPropertiesList = new ArrayList<XmlProperties>();
		for (int i = 0; i < nhosts.getLength(); i++) {
			Node nRoot = nhosts.item(i);
			if (PROPERTIES_TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())) {
				XmlProperties xmlProperties = new XmlProperties(nRoot);
				xmlPropertiesList.add(xmlProperties);
			}
		}
		this.propertiesFactory = new XmlPropertiesFactory(beanFactory, xmlPropertiesList);

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
