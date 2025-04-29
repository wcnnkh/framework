package run.soeasy.framework.dom.convert;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import run.soeasy.framework.core.convert.SourceDescriptor;
import run.soeasy.framework.core.convert.value.ValueAccessor;

public interface NodeWriter {
	boolean isWriteable(SourceDescriptor sourceDescriptor);

	void writeTo(ValueAccessor source, Node node) throws DOMException;
}
