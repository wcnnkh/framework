package io.basc.framework.dom;

import java.util.Enumeration;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.Accept;

public class NodeListAccess<E extends Throwable> implements ObjectAccess<E> {
	private final NodeList nodeList;

	public NodeListAccess(NodeList nodeList) {
		this.nodeList = nodeList;
	}

	@Override
	public Enumeration<String> keys() throws E {
		return new NodeNameEnumeration(nodeList);
	}

	@Override
	public Parameter get(String name) throws E {
		Node node = DomUtils.findNode(nodeList, new Accept<Node>() {

			public boolean accept(Node e) {
				return e.getNodeName().equals(name);
			}
		});
		return node == null ? null : new Parameter(name, node);
	}

	@Override
	public void set(Parameter parameter) throws E {
		throw new UnsupportedOperationException(String.valueOf(parameter));
	}

	private static final class NodeNameEnumeration implements Enumeration<String> {
		private final NodeList nodeList;
		private int i = 0;
		private int len;

		public NodeNameEnumeration(NodeList nodeList) {
			this.nodeList = nodeList;
			this.len = nodeList.getLength();
		}

		public boolean hasMoreElements() {
			return i < len;
		}

		public String nextElement() {
			Node node = nodeList.item(i++);
			return node.getNodeName();
		}

	}

}
