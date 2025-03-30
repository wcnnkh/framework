package run.soeasy.framework.dom.convert;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import run.soeasy.framework.core.convert.TargetDescriptor;

public interface NodeReader {
	boolean isReadable(TargetDescriptor targetDescriptor);

	Object readFrom(TargetDescriptor targetDescriptor, Node node) throws DOMException;
}
