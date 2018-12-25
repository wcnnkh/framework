package scw.beans.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.AbstractBeanFactory;
import scw.beans.Bean;
import scw.beans.BeanFactory;
import scw.beans.property.PropertiesFactory;
import scw.common.exception.AlreadyExistsException;
import scw.common.utils.StringUtils;

public class XmlBeanFactory extends AbstractBeanFactory {
	private static final String BEANS_ANNOTATION = "packages";
	private static final String BEAN_TAG_NAME = "bean";
	private static final String BEAN_FACTORY_TAG_NAME = "factory";

	private String packageNames;
	private List<String> beanFactoryList;
	private List<String> filterNameList;

	public XmlBeanFactory(BeanFactory beanFactory, PropertiesFactory propertiesFactory, String beanXml)
			throws Exception {
		Node root = XmlBeanUtils.getRootNode(beanXml);
		if (root.getAttributes() != null) {
			Node annotationNode = root.getAttributes().getNamedItem(BEANS_ANNOTATION);
			if (annotationNode != null) {
				this.packageNames = annotationNode.getNodeValue();
			}

			String filterNames = XmlBeanUtils.getNodeAttributeValue(root, "filters");
			if (!StringUtils.isNull(filterNames)) {
				filterNameList = Arrays.asList(StringUtils.commonSplit(filterNames));
			}
		}

		NodeList nhosts = root.getChildNodes();
		for (int i = 0; i < nhosts.getLength(); i++) {
			Node nRoot = nhosts.item(i);
			if (BEAN_TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())) {
				Bean bean = new XmlBean(beanFactory, propertiesFactory, nRoot, filterNameList);
				putBean(bean.getId(), bean);

				if (bean.getNames() != null) {
					for (String n : bean.getNames()) {
						if (!registerNameMapping(n, bean.getId())) {
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

	public List<String> getFilterNameList() {
		return filterNameList;
	}
}
