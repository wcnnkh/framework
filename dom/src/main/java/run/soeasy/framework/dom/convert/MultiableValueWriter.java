package run.soeasy.framework.dom.convert;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.ValueAccessor;

@RequiredArgsConstructor
public class MultiableValueWriter extends AbstractNodeWriter {

	@Override
	public boolean isWriteable(SourceDescriptor sourceDescriptor) {
		return sourceDescriptor.getTypeDescriptor().isArray() || sourceDescriptor.getTypeDescriptor().isCollection();
	}

	@Override
	public void writeTo(ValueAccessor source, Node node) throws DOMException {
		Elements<? extends ValueAccessor> elements = source.getAsElements();
		for (ValueAccessor element : elements) {
			if (getNodeWriter().isWriteable(element)) {
				getNodeWriter().writeTo(element, node);
			}
		}
	}

}
