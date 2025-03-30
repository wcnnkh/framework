package run.soeasy.framework.dom.convert;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.SourceDescriptor;

public interface NodeWriter {
	boolean isWriteable(SourceDescriptor sourceDescriptor);

	void writeTo(Source source, Node node) throws DOMException;
}
