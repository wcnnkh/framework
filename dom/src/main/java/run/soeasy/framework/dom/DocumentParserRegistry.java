package run.soeasy.framework.dom;

import java.io.IOException;

import org.w3c.dom.Document;

import run.soeasy.framework.util.function.Function;
import run.soeasy.framework.util.io.Resource;
import run.soeasy.framework.util.spi.ConfigurableServices;

public class DocumentParserRegistry extends ConfigurableServices<DocumentParser> implements DocumentParser {
	public DocumentParserRegistry() {
		setServiceClass(DocumentParser.class);
	}

	@Override
	public boolean canParse(Resource resource) {
		return anyMatch((e) -> e.canParse(resource));
	}

	@Override
	public <T, E extends Throwable> T parse(Resource resource,
			Function<? super Document, ? extends T, ? extends E> processor) throws IOException, DomException, E {
		if (resource == null || !resource.exists()) {
			return null;
		}

		for (DocumentParser parser : this) {
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
		throw new UnsupportedOperationException(resource.getDescription());
	}
}
