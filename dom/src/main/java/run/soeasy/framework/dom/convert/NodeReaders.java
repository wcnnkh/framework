package run.soeasy.framework.dom.convert;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import run.soeasy.framework.core.convert.Writeable;
import run.soeasy.framework.core.spi.ServiceProvider;

public class NodeReaders extends ServiceProvider<NodeReader, DOMException> implements NodeReader {

	public NodeReaders() {
		setServiceClass(NodeReader.class);
	}

	@Override
	public boolean isReadable(Writeable targetDescriptor) {
		return optional().filter((e) -> e.isReadable(targetDescriptor)).isPresent();
	}

	@Override
	public Object readFrom(Writeable targetDescriptor, Node node) throws DOMException {
		NodeReader documentReader = optional().filter((e) -> e.isReadable(targetDescriptor)).orElse(null);
		if (documentReader == null) {
			throw new UnsupportedOperationException(targetDescriptor.toString());
		}
		return documentReader.readFrom(targetDescriptor, node);
	}

}
