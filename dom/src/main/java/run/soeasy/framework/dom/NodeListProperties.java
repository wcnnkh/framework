package run.soeasy.framework.dom;

import java.util.stream.IntStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.property.PropertyAccessor;
import run.soeasy.framework.core.convert.property.PropertyMapping;

@AllArgsConstructor
public class NodeListProperties implements PropertyMapping {
	@NonNull
	private final NodeList nodeList;

	@Override
	public Elements<PropertyAccessor> getElements() {
		return Elements
				.of(() -> IntStream.range(1, nodeList.getLength() + 1).mapToObj((row) -> new NodeListProperty(row)));
	}

	@RequiredArgsConstructor
	@Getter
	private class NodeListProperty implements PropertyAccessor {
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
