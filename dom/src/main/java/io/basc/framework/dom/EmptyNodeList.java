package io.basc.framework.dom;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EmptyNodeList implements NodeList {
	public static final NodeList EMPTY = new EmptyNodeList();

	@Override
	public Node item(int index) {
		return null;
	}

	@Override
	public int getLength() {
		return 0;
	}
}
