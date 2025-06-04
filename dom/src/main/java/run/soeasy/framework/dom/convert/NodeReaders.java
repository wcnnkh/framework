package run.soeasy.framework.dom.convert;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class NodeReaders extends ConfigurableServices<NodeReader> implements NodeReader {

	public NodeReaders() {
		setServiceClass(NodeReader.class);
	}

	@Override
	public boolean isReadable(TargetDescriptor targetDescriptor) {
		return anyMatch((e) -> e.isReadable(targetDescriptor));
	}

	@Override
	public Object readFrom(TargetDescriptor targetDescriptor, Node node) throws DOMException {
		for (NodeReader reader : this) {
			if (reader.isReadable(targetDescriptor)) {
				return reader.readFrom(targetDescriptor, node);
			}
		}
		throw new UnsupportedOperationException(targetDescriptor.toString());
	}

}
