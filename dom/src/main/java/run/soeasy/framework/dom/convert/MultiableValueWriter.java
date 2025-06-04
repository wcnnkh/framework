package run.soeasy.framework.dom.convert;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TypedValue;

@RequiredArgsConstructor
public class MultiableValueWriter extends AbstractNodeWriter {

	@Override
	public boolean isWriteable(SourceDescriptor sourceDescriptor) {
		return sourceDescriptor.getReturnTypeDescriptor().isArray()
				|| sourceDescriptor.getReturnTypeDescriptor().isCollection();
	}

	@Override
	public void writeTo(TypedValue source, Node node) throws DOMException {
		Elements<? extends TypedValue> elements = source.getAsElements();
		for (TypedValue element : elements) {
			if (getNodeWriter().isWriteable(element)) {
				getNodeWriter().writeTo(element, node);
			}
		}
	}

}
