package io.basc.framework.dom;

import java.util.stream.IntStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.transform.stereotype.Properties;
import io.basc.framework.core.convert.transform.stereotype.Property;
import io.basc.framework.util.collections.Elements;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
public class NodeListProperties implements Properties {
	@NonNull
	private final NodeList nodeList;

	@Override
	public Elements<Property> getElements() {
		return Elements
				.of(() -> IntStream.range(1, nodeList.getLength() + 1).mapToObj((row) -> new NodeListProperty(row)));
	}

	@RequiredArgsConstructor
	@Getter
	private class NodeListProperty implements Property {
		private final int row;

		@Override
		public void set(Object value) throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object get() throws ConversionException {
			return nodeList.item(row);
		}

		@Override
		public String getName() {
			Node node = nodeList.item(row);
			return node == null ? null : node.getNodeName();
		}

	}

}
