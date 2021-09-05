package io.basc.framework.dom;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.dom.writer.ArrayWriter;
import io.basc.framework.dom.writer.CollectionWriter;
import io.basc.framework.dom.writer.MapWriter;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.NotSupportedException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DocumentTemplate implements Configurable, DocumentReader,
		DocumentWriter {
	private final ConfigurableServices<DocumentReader> readers = new ConfigurableServices<DocumentReader>(
			DocumentReader.class);
	private final ConfigurableServices<DocumentWriter> writers = new ConfigurableServices<DocumentWriter>(
			DocumentWriter.class);

	public DocumentTemplate() {
		writers.addService(new MapWriter(this));
		writers.addService(new CollectionWriter(this));
		writers.addService(new ArrayWriter(this));
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		readers.configure(serviceLoaderFactory);
		writers.configure(serviceLoaderFactory);
	}

	public ConfigurableServices<DocumentReader> getReaders() {
		return readers;
	}

	public ConfigurableServices<DocumentWriter> getWriters() {
		return writers;
	}

	@Override
	public boolean canWrite(TypeDescriptor sourceTypeDescriptor) {
		for (DocumentWriter writer : getWriters()) {
			if (writer.canWrite(sourceTypeDescriptor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void write(Document document, Node parentNode, String nodeName,
			Object source, TypeDescriptor sourceTypeDescriptor) {
		for (DocumentWriter writer : getWriters()) {
			if (writer.canWrite(sourceTypeDescriptor)) {
				writer.write(document, parentNode, nodeName, source,
						sourceTypeDescriptor);
				return;
			}
		}
		Element element = document.createElement(nodeName);
		element.setTextContent(String.valueOf(source));
		parentNode.appendChild(element);
	}

	@Override
	public boolean canReader(Resource resource) {
		for (DocumentReader reader : getReaders()) {
			if (reader.canReader(resource)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Document read(Resource resource) {
		for (DocumentReader reader : getReaders()) {
			if (reader.canReader(resource)) {
				return reader.read(resource);
			}
		}
		throw new NotSupportedException(resource.getDescription());
	}
}
