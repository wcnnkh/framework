package scw.beans.xml;

import org.w3c.dom.NodeList;

import scw.beans.configuration.AbstractBeanConfiguration;

public abstract class XmlBeanConfiguration extends AbstractBeanConfiguration {
	private NodeList nodeList;

	public NodeList getNodeList() {
		return nodeList;
	}

	public void setNodeList(NodeList nodeList) {
		this.nodeList = nodeList;
	}
}
