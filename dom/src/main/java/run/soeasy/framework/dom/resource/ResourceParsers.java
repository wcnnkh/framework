package run.soeasy.framework.dom.resource;

import java.io.IOException;

import org.w3c.dom.Node;

import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.core.io.Resource;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class ResourceParsers extends ConfigurableServices<ResourceParser> implements ResourceParser {
	public ResourceParsers() {
		setServiceClass(ResourceParser.class);
	}

	@Override
	public boolean canParse(Resource resource) {
		return anyMatch((e) -> e.canParse(resource));
	}

	@Override
	public <T, E extends Throwable> T parse(Resource resource,
			ThrowingFunction<? super Node, ? extends T, ? extends E> processor) throws IOException, E {
		for (ResourceParser resourceParser : this) {
			if (resourceParser.canParse(resource)) {
				return resourceParser.parse(resource, processor);
			}
		}
		throw new UnsupportedOperationException(resource.toString());
	}
}
