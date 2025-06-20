package run.soeasy.framework.xml;

import java.io.IOException;
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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.dom.DomException;
import run.soeasy.framework.dom.resource.ResourceTransformer;
import run.soeasy.framework.io.Resource;
import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;

@RequiredArgsConstructor
public class XmlTransformer implements ResourceTransformer {
	private static Logger logger = LogManager.getLogger(XmlTransformer.class);
	private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
	static {
		try {
			TRANSFORMER_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (TransformerConfigurationException e) {
			logger.warn(e, "config transformer factory error!");
		}
	}
 
	@NonNull
	private final TransformerFactory transformerFactory;

	public XmlTransformer() {
		this(TRANSFORMER_FACTORY);
	}

	@Override
	public boolean canTransform(Node node) {
		Document document = node.getOwnerDocument();
		if (document == null) {
			return false;
		}

		DocumentType documentType = document.getDoctype();
		return documentType != null && "xml".equals(documentType.getName());
	}

	public Transformer getTransformer() throws TransformerConfigurationException {
		return transformerFactory.newTransformer();
	}

	@Override
	public void transform(Node source, Resource resource) throws IOException {
		if (resource.isEncoded()) {
			resource.getWriterPipeline().optional().ifPresent((w) -> transform(source, w));
		} else {
			resource.getOutputStreamPipeline().optional().ifPresent((os) -> transform(source, os));
		}
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
