package io.basc.framework.dom;

import java.util.stream.IntStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.core.convert.Value;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.param.Arg;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.transform.ReadOnlyProperty;
import io.basc.framework.util.collections.Elements;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class NodeListProperties implements Properties {
	@NonNull
	private final NodeList nodeList;

	@Override
	public Elements<Property> getElements() {
		return Elements.of(() -> IntStream.range(1, nodeList.getLength() + 1).mapToObj((i) -> {
			Node node = nodeList.item(i);
			Parameter parameter = new Arg(i, node.getNodeName(), Value.of(node));
			return new ReadOnlyProperty(parameter);
		}));
	}

}
