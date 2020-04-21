package scw.beans.xml;

import org.w3c.dom.NodeList;

import scw.beans.configuration.AbstractBeanFactoryLifeCycle;

public abstract class XmlBeanFactoryLifeCycle extends
		AbstractBeanFactoryLifeCycle {

	private NodeList nodeList;

	public NodeList getNodeList() {
		return nodeList;
	}

	public void setNodeList(NodeList nodeList) {
		this.nodeList = nodeList;
	}
}
