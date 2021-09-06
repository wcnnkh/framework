package io.basc.framework.dom;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.w3c.dom.Document;

public interface DocumentTransformer {
	boolean canTransform(Document document);

	/**
	 * @see #canTransform(Document)
	 * @param document
	 * @return
	 */
	default String toString(Document document) throws IOException, DomException {
		StringWriter writer = new StringWriter();
		transform(document, writer);
		return writer.toString();
	}

	/**
	 * @see #canTransform(Document)
	 * @param document
	 * @param output
	 */
	void transform(Document document, OutputStream output) throws IOException, DomException;

	/**
	 * @see #canTransform(Document)
	 * @param document
	 * @param writer
	 */
	void transform(Document document, Writer writer) throws IOException, DomException;
}
