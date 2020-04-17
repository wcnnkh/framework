package scw.beans.xml;

import org.w3c.dom.NodeList;

import scw.beans.BeanFactoryLifeCycle;

public abstract class XmlBeanFactoryLifeCycle implements BeanFactoryLifeCycle {
	private NodeList nodeList;

	public NodeList getNodeList() {
		return nodeList;
	}

	public void setNodeList(NodeList nodeList) {
		this.nodeList = nodeList;
	}
}
