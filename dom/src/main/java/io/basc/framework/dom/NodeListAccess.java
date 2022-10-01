package io.basc.framework.dom;

import java.util.Enumeration;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.convert.Converter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.Parameter;

public class NodeListAccess<E extends Throwable> implements ObjectAccess<E> {
	private final NodeList nodeList;
	private final @Nullable Converter<? super Object, ? super Object, ? extends RuntimeException> converter;

	public NodeListAccess(NodeList nodeList) {
		this(nodeList, null);
	}

	public NodeListAccess(NodeList nodeList,
			@Nullable Converter<? super Object, ? super Object, ? extends RuntimeException> converter) {
		this.nodeList = nodeList;
		this.converter = converter;
	}

	@Override
	public Enumeration<String> keys() throws E {
		return new NodeNameEnumeration(nodeList);
	}

	@Override
	public Parameter get(String name) throws E {
		Node node = DomUtils.findNode(nodeList, (e) -> e.getNodeName().equals(name));
		if (node == null) {
			return null;
		}

		Parameter parameter = new Parameter(name, node);
		if (converter != null) {
			parameter.setConverter(converter);
		}
		return parameter;
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
