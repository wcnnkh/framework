package run.soeasy.framework.dom.resource;

import java.io.IOException;

import org.w3c.dom.Node;

import run.soeasy.framework.dom.DomException;
import run.soeasy.framework.util.io.Resource;
import run.soeasy.framework.util.spi.Providers;

public class ResourceTransformers extends Providers<ResourceTransformer, DomException> implements ResourceTransformer {

	public ResourceTransformers() {
		setServiceClass(ResourceTransformer.class);
	}

	@Override
	public boolean canTransform(Node node) {
		return optional().filter((e) -> e.canTransform(node)).isPresent();
	}

	@Override
	public void transform(Node source, Resource resource) throws IOException {
		ResourceTransformer resourceTransformer = optional().filter((e) -> e.canTransform(source)).orElse(null);
		if (resourceTransformer == null) {
			throw new UnsupportedOperationException(source.toString());
		}
		resourceTransformer.transform(source, resource);
	}
}
