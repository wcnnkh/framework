package run.soeasy.framework.dom.convert;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import run.soeasy.framework.core.convert.value.Writeable;

public interface NodeReader {
	boolean isReadable(Writeable targetDescriptor);

	Object readFrom(Writeable targetDescriptor, Node node) throws DOMException;
}
