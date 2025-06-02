package run.soeasy.framework.dom;

import java.io.IOException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.core.io.Resource;
import run.soeasy.framework.core.io.StringBufferResource;
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
			ThrowingFunction<? super Node, ? extends T, ? extends E> processor) throws IOException, E {
		return parsers.parse(resource, processor);
	}

	@Override
	public boolean isWriteable(SourceDescriptor sourceDescriptor) {
		return writers.isWriteable(sourceDescriptor);
	}

	@Override
	public void writeTo(TypedValue source, Node node) throws DOMException {
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

	public String toString(Node node) throws IOException {
		StringBufferResource resource = new StringBufferResource();
		transform(node, resource);
		return resource.readAllCharacters();
	}
}
