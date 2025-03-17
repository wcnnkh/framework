package run.soeasy.framework.dom;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.w3c.dom.Document;

public interface DocumentTransformer {
	boolean canTransform(Document document);

	default String toString(Document document) throws IOException {
		StringWriter writer = new StringWriter();
		transform(document, writer);
		return writer.toString();
	}

	void transform(Document document, OutputStream output) throws IOException, DomException;

	void transform(Document document, Writer writer) throws IOException, DomException;
}
