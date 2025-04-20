package run.soeasy.framework.dom.resource;

import java.io.IOException;

import org.w3c.dom.Node;

import run.soeasy.framework.core.exe.Function;
import run.soeasy.framework.core.io.Resource;
import run.soeasy.framework.core.spi.ServiceProvider;
import run.soeasy.framework.dom.DomException;

public class ResourceParsers extends ServiceProvider<ResourceParser, DomException> implements ResourceParser {
	public ResourceParsers() {
		setServiceClass(ResourceParser.class);
	}

	@Override
	public boolean canParse(Resource resource) {
		return optional().filter((e) -> e.canParse(resource)).isPresent();
	}

	@Override
	public <T, E extends Throwable> T parse(Resource resource,
			Function<? super Node, ? extends T, ? extends E> processor) throws IOException, E {
		ResourceParser resourceParser = optional().filter((e) -> e.canParse(resource)).orElse(null);
		if (resourceParser == null) {
			throw new UnsupportedOperationException(resource.toString());
		}
		return resourceParser.parse(resource, processor);
	}
}
