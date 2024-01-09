package io.basc.framework.dom;

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Parameter;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.util.element.Elements;

public class NodeListAccess implements ObjectAccess {
	private final NodeList nodeList;
	private final TypeDescriptor typeDescriptor;

	public NodeListAccess(NodeList nodeList, TypeDescriptor typeDescriptor) {
		this.nodeList = nodeList;
		this.typeDescriptor = typeDescriptor;
	}

	@Override
	public Elements<String> keys() {
		return Elements.of(() -> new NodeNameEnumeration(nodeList));
	}

	@Override
	public Parameter get(String name) {
		Node node = DomUtils.findNode(nodeList, (e) -> e.getNodeName().equals(name));
		if (node == null) {
			return null;
		}

		return new Parameter(name, node);
	}

	@Override
	public void set(Parameter parameter) {
		throw new UnsupportedOperationException(String.valueOf(parameter));
	}

	private static final class NodeNameEnumeration implements Iterator<String> {
		private final NodeList nodeList;
		private int i = 0;
		private int len;

		public NodeNameEnumeration(NodeList nodeList) {
			this.nodeList = nodeList;
			this.len = nodeList.getLength();
		}

		@Override
		public boolean hasNext() {
			return i < len;
		}

		@Override
		public String next() {
			Node node = nodeList.item(i++);
			return node.getNodeName();
		}

	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor;
	}

}
