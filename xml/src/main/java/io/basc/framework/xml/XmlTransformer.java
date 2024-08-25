package io.basc.framework.xml;

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;

import io.basc.framework.dom.DocumentTransformer;
import io.basc.framework.dom.DomException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;

public class XmlTransformer implements DocumentTransformer {
	private static Logger logger = LoggerFactory.getLogger(XmlTransformer.class);
	private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
	static {
		try {
			TRANSFORMER_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (TransformerConfigurationException e) {
			logger.warn(e, "config transformer factory error!");
		}
	}
	
	private final TransformerFactory transformerFactory;

	public XmlTransformer() {
		this(TRANSFORMER_FACTORY);
	}

	public XmlTransformer(TransformerFactory transformerFactory) {
		Assert.requiredArgument(transformerFactory != null, "transformerFactory");
		this.transformerFactory = transformerFactory;
	}

	@Override
	public boolean canTransform(Document document) {
		DocumentType documentType = document.getDoctype();
		return documentType != null && "xml".equals(documentType.getName());
	}

	public Transformer getTransformer() throws TransformerConfigurationException {
		return transformerFactory.newTransformer();
	}

	@Override
	public final void transform(Document document, OutputStream output) throws DomException {
		transform((Node) document, output);
	}

	@Override
	public final void transform(Document document, Writer writer) throws DomException {
		transform((Node) document, writer);
	}

	public void transform(Node node, OutputStream output) throws DomException {
		StreamResult result = new StreamResult(output);
		DOMSource domSource = new DOMSource(node);
		try {
			getTransformer().transform(domSource, result);
		} catch (TransformerConfigurationException e) {
			throw new DomException(e);
		} catch (TransformerException e) {
			throw new DomException(e);
		}
	}

	public void transform(Node node, Writer writer) throws DomException {
		StreamResult result = new StreamResult(writer);
		DOMSource domSource = new DOMSource(node);
		try {
			getTransformer().transform(domSource, result);
		} catch (TransformerConfigurationException e) {
			throw new DomException(e);
		} catch (TransformerException e) {
			throw new DomException(e);
		}
	}

	public String toString(Node node) throws DomException {
		StringWriter writer = new StringWriter();
		transform(node, writer);
		return writer.toString();
	}
}
