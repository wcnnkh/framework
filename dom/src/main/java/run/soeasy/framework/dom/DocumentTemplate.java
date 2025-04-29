package run.soeasy.framework.dom;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.SourceDescriptor;
import run.soeasy.framework.core.convert.TargetDescriptor;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.convert.value.ValueAccessor;
import run.soeasy.framework.core.invoke.Function;
import run.soeasy.framework.core.io.Resource;
import run.soeasy.framework.core.io.StringBufferResource;
import run.soeasy.framework.core.io.load.ResourceLoader;
import run.soeasy.framework.dom.convert.NodeReader;
import run.soeasy.framework.dom.convert.NodeReaders;
import run.soeasy.framework.dom.convert.NodeWriter;
import run.soeasy.framework.dom.convert.NodeWriters;
import run.soeasy.framework.dom.resource.ResourceParser;
import run.soeasy.framework.dom.resource.ResourceParsers;
import run.soeasy.framework.dom.resource.ResourceTransformer;
import run.soeasy.framework.dom.resource.ResourceTransformers;

@Getter
@Setter
public class DocumentTemplate implements NodeReader, NodeWriter, ResourceParser, ResourceTransformer {
	private final NodeReaders readers = new NodeReaders();
	private final NodeWriters writers = new NodeWriters();
	private final ResourceParsers parsers = new ResourceParsers();
	private final ResourceTransformers transformers = new ResourceTransformers();
	@NonNull
	private ConversionService conversionService = SystemConversionService.getInstance();

	@Override
	public boolean canTransform(Node node) {
		return transformers.canTransform(node);
	}

	@Override
	public void transform(Node node, Resource resource) throws IOException {
		transformers.transform(node, resource);
	}

	@Override
	public boolean canParse(Resource resource) {
		return parsers.canParse(resource);
	}

	@Override
	public <T, E extends Throwable> T parse(Resource resource,
			Function<? super Node, ? extends T, ? extends E> processor) throws IOException, E {
		return parsers.parse(resource, processor);
	}

	@Override
	public boolean isWriteable(SourceDescriptor sourceDescriptor) {
		return writers.isWriteable(sourceDescriptor);
	}

	@Override
	public void writeTo(ValueAccessor source, Node node) throws DOMException {
		writers.writeTo(source, node);
	}

	@Override
	public boolean isReadable(TargetDescriptor targetDescriptor) {
		return readers.isReadable(targetDescriptor);
	}

	@Override
	public Object readFrom(TargetDescriptor targetDescriptor, Node node) throws DOMException {
		return readers.readFrom(targetDescriptor, node);
	}

	public NodeList getChildNodes(Node node, ResourceLoader resourceLoader)
			throws DomException, IOException, RuntimeException {
		if (node == null) {
			return EmptyNodeList.EMPTY;
		}

		NodeList nodeList = resourceLoader != null
				? converIncludeNodeList(resourceLoader, node.getChildNodes(), new HashMap<>())
				: node.getChildNodes();
		if (nodeList == null) {
			return EmptyNodeList.EMPTY;
		}
		return nodeList;
	}

	private NodeList converIncludeNodeList(ResourceLoader resourceLoader, NodeList nodeList,
			Map<String, NodeList> includeMap) throws DomException, IOException, RuntimeException {
		ArrayNodeList list = new ArrayNodeList();
		if (nodeList != null) {
			for (int i = 0, size = nodeList.getLength(); i < size; i++) {
				Node n = nodeList.item(i);
				if (n == null) {
					continue;
				}

				if (n.getNodeName().equalsIgnoreCase("include")) {
					NodeList n2 = getIncludeNodeList(resourceLoader, includeMap, n);
					list.addNodeList(converIncludeNodeList(resourceLoader, n2, includeMap));
				} else {
					list.add(n);
				}
			}
		}
		return list;
	}

	private NodeList getIncludeNodeList(ResourceLoader resourceLoader, Map<String, NodeList> includeMap,
			Node includeNode) throws DomException, IOException, RuntimeException {
		String resourceName = DomUtils.getNodeAttributeValueOrNodeContent(includeNode, "resource");
		if (StringUtils.isEmpty(resourceName)) {
			return new ArrayNodeList();
		}

		resourceName = StringUtils.cleanPath(resourceName);
		NodeList result = includeMap.get(resourceName);
		if (result == null) {
			Resource resource = resourceLoader.getResource(resourceName);
			result = parse(resource, (root) -> {
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
			includeMap.put(resourceName, result);
		}
		return result;
	}

	public String toString(Node node) throws IOException {
		StringBufferResource resource = new StringBufferResource();
		transform(node, resource);
		return resource.readAllCharacters();
	}
}
