package run.soeasy.framework.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.spi.ConfigurableServices;

public class DocumentWriterRegistry extends ConfigurableServices<DocumentWriter> implements DocumentWriter {
	public DocumentWriterRegistry() {
		setServiceClass(DocumentWriter.class);
	}

	@Override
	public boolean canWrite(TypeDescriptor typeDescriptor) {
		return anyMatch((e) -> e.canWrite(typeDescriptor));
	}

	@Override
	public void write(Document document, Node parentNode, String nodeName, Object value, TypeDescriptor valueType) {
		for (DocumentWriter writer : this) {
			if (writer.canWrite(valueType)) {
				writer.write(document, parentNode, nodeName, value, valueType);
				return;
			}
		}
	}

}
