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

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.dom.writer.ArrayWriter;
import io.basc.framework.dom.writer.CollectionWriter;
import io.basc.framework.dom.writer.MapWriter;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.exchange.Receipt;
import io.basc.framework.util.function.Consumer;
import io.basc.framework.util.function.Function;
import io.basc.framework.util.io.Resource;
import io.basc.framework.util.io.load.ResourceLoader;
import io.basc.framework.util.spi.Configurable;
import io.basc.framework.util.spi.ServiceLoaderDiscovery;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class DocumentTemplate implements Configurable, DocumentParser, DocumentWriter, DocumentTransformer {
	protected final DocumentParserRegistry parserRegistry = new DocumentParserRegistry();
	protected final DocumentTransformerRegistry transformerRegistry = new DocumentTransformerRegistry();
	protected final DocumentWriterRegistry writerRegistry = new DocumentWriterRegistry();

	public DocumentTemplate() {
		writerRegistry.register(new MapWriter(this));
		writerRegistry.register(new CollectionWriter(this));
		writerRegistry.register(new ArrayWriter(this));
	}

	@Override
	public boolean canParse(Resource resource) {
		return parserRegistry.canParse(resource);
	}

	@Override
	public boolean canTransform(Document document) {
		return transformerRegistry.canTransform(document);
	}

	@Override
	public boolean canWrite(TypeDescriptor sourceTypeDescriptor) {
		return writerRegistry.canWrite(sourceTypeDescriptor);
	}

	private ArrayNodeList converIncludeNodeList(ResourceLoader resourceLoader, NodeList nodeList,
			HashSet<String> includeHashSet) throws DomException, IOException, RuntimeException {
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

	@Override
	public Receipt doConfigure(@NonNull ServiceLoaderDiscovery discovery) {
		transformerRegistry.doConfigure(discovery);
		writerRegistry.doConfigure(discovery);
		parserRegistry.doConfigure(discovery);
		return Receipt.SUCCESS;
	}

	public NodeList getChildNodes(Node node, ResourceLoader resourceLoader)
			throws DomException, IOException, RuntimeException {
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

	private ArrayNodeList getIncludeNodeList(ResourceLoader resourceLoader, HashSet<String> includeHashSet,
			Node includeNode) throws DomException, IOException, RuntimeException {
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

	@Override
	public <T, E extends Throwable> T parse(Resource resource,
			Function<? super Document, ? extends T, ? extends E> processor) throws DomException, IOException, E {
		return parserRegistry.parse(resource, processor);
	}

	public <E extends Throwable> void read(Resource resource, Consumer<? super Document, ? extends E> processor)
			throws DomException, IOException, E {
		parse(resource, (document) -> {
			processor.accept(document);
			return null;
		});
	}

	@Override
	public String toString(Document document) throws DomException, IOException {
		StringWriter writer = new StringWriter();
		transform(document, writer);
		return writer.toString();
	}

	@Override
	public void transform(Document document, OutputStream output) throws DomException, IOException {
		transformerRegistry.transform(document, output);
	}

	@Override
	public void transform(Document document, Writer writer) throws DomException, IOException {
		transformerRegistry.transform(document, writer);
	}

	@Override
	public void write(Document document, Node parentNode, String nodeName, Object source,
			TypeDescriptor sourceTypeDescriptor) {
		writerRegistry.write(document, parentNode, nodeName, source, sourceTypeDescriptor);
		Element element = document.createElement(nodeName);
		element.setTextContent(String.valueOf(source));
		parentNode.appendChild(element);
	}
}
