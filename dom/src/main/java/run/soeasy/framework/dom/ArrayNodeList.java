package run.soeasy.framework.dom;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ArrayNodeList extends ArrayList<Node> implements NodeList {
	private static final long serialVersionUID = 1L;

	public Node item(int index) {
		return get(index);
	}

	public int getLength() {
		return size();
	}

	public void addNodeList(NodeList nodeList) {
		for (int i = 0, len = nodeList.getLength(); i < len; i++) {
			add(nodeList.item(i));
		}
	}
}