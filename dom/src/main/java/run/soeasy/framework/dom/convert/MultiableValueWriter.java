package run.soeasy.framework.dom.convert;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.SourceDescriptor;
import run.soeasy.framework.util.collection.Elements;

@RequiredArgsConstructor
public class MultiableValueWriter extends AbstractNodeWriter {

	@Override
	public boolean isWriteable(SourceDescriptor sourceDescriptor) {
		return sourceDescriptor.getTypeDescriptor().isArray() || sourceDescriptor.getTypeDescriptor().isCollection();
	}

	@Override
	public void writeTo(Source source, Node node) throws DOMException {
		Elements<? extends Source> elements = source.getAsElements();
		for (Source element : elements) {
			if (getNodeWriter().isWriteable(element)) {
				getNodeWriter().writeTo(element, node);
			}
		}
	}

}
