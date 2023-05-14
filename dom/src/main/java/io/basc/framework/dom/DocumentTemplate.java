package io.basc.framework.dom;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.dom.writer.ArrayWriter;
import io.basc.framework.dom.writer.CollectionWriter;
import io.basc.framework.dom.writer.MapWriter;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.lang.Nullable;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Processor;
import io.basc.framework.util.StringUtils;

public class DocumentTemplate implements Configurable, DocumentParser, DocumentWriter, DocumentTransformer {
	protected final ConfigurableServices<DocumentTransformer> transformers = new ConfigurableServices<DocumentTransformer>(
			DocumentTransformer.class);
	protected final ConfigurableServices<DocumentWriter> writers = new ConfigurableServices<DocumentWriter>(
			DocumentWriter.class);
	protected final ConfigurableServices<DocumentParser> parsers = new ConfigurableServices<>(DocumentParser.class);

	public DocumentTemplate() {
		writers.register(new MapWriter(this));
		writers.register(new CollectionWriter(this));
		writers.register(new ArrayWriter(this));
	}

	private boolean configured;

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		transformers.configure(serviceLoaderFactory);
		writers.configure(serviceLoaderFactory);
		parsers.configure(serviceLoaderFactory);
		configured = true;
	}

	public ConfigurableServices<DocumentTransformer> getTransformers() {
		return transformers;
	}

	public ConfigurableServices<DocumentWriter> getWriters() {
		return writers;
	}

	public ConfigurableServices<DocumentParser> getParsers() {
		return parsers;
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
	public void write(Document document, Node parentNode, String nodeName, Object source,
			TypeDescriptor sourceTypeDescriptor) {
		for (DocumentWriter writer : getWriters()) {
			if (writer.canWrite(sourceTypeDescriptor)) {
				writer.write(document, parentNode, nodeName, source, sourceTypeDescriptor);
				return;
			}
		}
		Element element = document.createElement(nodeName);
		element.setTextContent(String.valueOf(source));
		parentNode.appendChild(element);
	}

	@Override
	public boolean canParse(Resource resource) {
		if (resource == null || !resource.exists()) {
			return false;
		}

		for (DocumentParser parser : getParsers()) {
			if (parser.canParse(resource)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <T, E extends Throwable> T parse(Resource resource,
			Processor<? super Document, ? extends T, ? extends E> processor) throws DomException {
		if (resource == null || !resource.exists()) {
			return null;
		}

		for (DocumentParser parser : getParsers()) {
			if (parser.canParse(resource)) {
				try {
					return parser.parse(resource, processor);
				} catch (Throwable e) {
					if (e instanceof DomException) {
						throw (DomException) e;
					}
					throw new DomException(resource.getDescription(), e);
				}
			}
		}
		throw new UnsupportedException(resource.getDescription());
	}

	public <E extends Throwable> void read(Resource resource, ConsumeProcessor<Document, E> processor)
			throws DomException {
		parse(resource, (document) -> {
			processor.process(document);
			return null;
		});
	}

	private ArrayNodeList getIncludeNodeList(ResourceLoader resourceLoader, HashSet<String> includeHashSet,
			Node includeNode) {
		String resourceName = DomUtils.getNodeAttributeValueOrNodeContent(includeNode, "resource");
		if (StringUtils.isEmpty(resourceName)) {
			return new ArrayNodeList();
		}

		resourceName = StringUtils.cleanPath(resourceName);
		if (includeHashSet.contains(resourceName)) {
			throw new RuntimeException(resourceName + "存在循环引用，请检查include地址");
		}

		includeHashSet.add(resourceName);
		Resource resource = resourceLoader.getResource(resourceName);
		return parse(resource, (document) -> {
			Node root = document.getDocumentElement();
			if (root == null) {
				return new ArrayNodeList();
			}

			NodeList nodeList = root.getChildNodes();
			ArrayNodeList list = new ArrayNodeList();
			for (int i = 0, size = nodeList.getLength(); i < size; i++) {
				Node n = nodeList.item(i);
				if (n == null) {
					continue;
				}

				list.add(n);
			}
			return list;
		});
	}

	private ArrayNodeList converIncludeNodeList(ResourceLoader resourceLoader, NodeList nodeList,
			HashSet<String> includeHashSet) {
		ArrayNodeList list = new ArrayNodeList();
		if (nodeList != null) {
			for (int i = 0, size = nodeList.getLength(); i < size; i++) {
				Node n = nodeList.item(i);
				if (n == null) {
					continue;
				}

				if (n.getNodeName().equalsIgnoreCase("include")) {
					ArrayNodeList n2 = getIncludeNodeList(resourceLoader, includeHashSet, n);
					list.addAll(converIncludeNodeList(resourceLoader, n2, includeHashSet));
				} else {
					list.add(n);
				}
			}
		}
		return list;
	}

	public NodeList getChildNodes(Node node, @Nullable ResourceLoader resourceLoader) {
		if (node == null) {
			return EmptyNodeList.EMPTY;
		}

		NodeList nodeList = resourceLoader != null
				? converIncludeNodeList(resourceLoader, node.getChildNodes(), new HashSet<String>())
				: node.getChildNodes();
		if (nodeList == null) {
			return EmptyNodeList.EMPTY;
		}
		return nodeList;
	}

	@Override
	public boolean canTransform(Document document) {
		for (DocumentTransformer transformer : getTransformers()) {
			if (transformer.canTransform(document)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString(Document document) throws DomException {
		StringWriter writer = new StringWriter();
		transform(document, writer);
		return writer.toString();
	}

	@Override
	public void transform(Document document, OutputStream output) throws DomException {
		for (DocumentTransformer transformer : getTransformers()) {
			if (transformer.canTransform(document)) {
				try {
					transformer.transform(document, output);
				} catch (IOException e) {
					throw new DomException(e);
				}
				return;
			}
		}
		throw new UnsupportedException(document.toString());
	}

	@Override
	public void transform(Document document, Writer writer) throws DomException {
		for (DocumentTransformer transformer : getTransformers()) {
			if (transformer.canTransform(document)) {
				try {
					transformer.transform(document, writer);
				} catch (IOException e) {
					throw new DomException(e);
				}
				return;
			}
		}
		throw new UnsupportedException(document.toString());
	}

	@Override
	public boolean isConfigured() {
		return configured;
	}
}
