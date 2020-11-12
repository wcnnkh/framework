package scw.beans.xml;

import java.util.Arrays;
import java.util.Collection;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanDefinition;
import scw.beans.DefaultBeanFactory;
import scw.core.utils.StringUtils;
import scw.io.ResourceUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.value.property.PropertyFactory;
import scw.xml.XMLUtils;

public class XmlBeanFactory extends DefaultBeanFactory {
	private static Logger logger = LoggerUtils.getLogger(XmlBeanFactory.class);
	private static final String TAG_NAME = "bean";
	private NodeList nodeList;

	public XmlBeanFactory(PropertyFactory propertyFactory, String xml) {
		super(propertyFactory);
		if (StringUtils.isNotEmpty(xml)) {
			if (ResourceUtils.getResourceOperations().isExist(xml)) {
				Node root = XmlBeanUtils.getRootNode(xml);
				this.nodeList = XMLUtils.getChildNodes(root, true);
			} else {
				logger.info("Not used {}", xml);
			}
		}
	}

	public NodeList getNodeList() {
		return nodeList == null ? XMLUtils.EMPTY_NODE_LIST : nodeList;
	}

	@Override
	public void beforeInit() throws Throwable {
		if (nodeList == null) {
			return;
		}

		getPropertyFactory().addLastBasePropertyFactory(new XmlPropertyFactory(getPropertyFactory(), nodeList));
		addXmlBeanNameMapping(nodeList);

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nRoot = nodeList.item(i);
			if (TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())) {
				BeanDefinition beanDefinition = new XmlBeanDefinition(this, getPropertyFactory(), nRoot);
				addBeanDefinition(beanDefinition, true);
			}
		}
		super.beforeInit();
	}

	private void addXmlBeanNameMapping(NodeList nodeList) {
		if (nodeList == null) {
			return;
		}

		for (int i = 0, len = nodeList.getLength(); i < len; i++) {
			Node node = nodeList.item(i);
			if (node == null) {
				continue;
			}

			if ("mapping".equalsIgnoreCase(node.getNodeName())) {
				Collection<String> names = Arrays.asList(
						StringUtils.commonSplit(XMLUtils.getRequireNodeAttributeValue(getPropertyFactory(), node, "name")));
				String id = XMLUtils.getRequireNodeAttributeValueOrNodeContent(getPropertyFactory(), node, "id");
				addBeanNameMapping(names, id, false);
			}
		}
	}
}
