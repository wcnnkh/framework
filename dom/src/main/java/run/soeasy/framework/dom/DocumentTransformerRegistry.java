package run.soeasy.framework.dom;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.w3c.dom.Document;

import run.soeasy.framework.util.spi.ConfigurableServices;

public class DocumentTransformerRegistry extends ConfigurableServices<DocumentTransformer> implements DocumentTransformer {

	public DocumentTransformerRegistry() {
		setServiceClass(DocumentTransformer.class);
	}

	@Override
	public boolean canTransform(Document document) {
		return anyMatch((e) -> e.canTransform(document));
	}

	@Override
	public void transform(Document document, OutputStream output) throws IOException, DomException {
		for (DocumentTransformer transformer : this) {
			if (transformer.canTransform(document)) {
				transformer.transform(document, output);
				return;
			}
		}
	}

	@Override
	public void transform(Document document, Writer writer) throws IOException, DomException {
		for (DocumentTransformer transformer : this) {
			if (transformer.canTransform(document)) {
				transformer.transform(document, writer);
				return;
			}
		}
	}

}
