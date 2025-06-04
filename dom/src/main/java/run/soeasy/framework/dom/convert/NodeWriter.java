package run.soeasy.framework.dom.convert;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TypedValue;

public interface NodeWriter {
	boolean isWriteable(SourceDescriptor sourceDescriptor);

	void writeTo(TypedValue source, Node node) throws DOMException;
}
