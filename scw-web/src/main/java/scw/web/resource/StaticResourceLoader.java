package scw.web.resource;

import scw.io.Resource;
import scw.net.MimeType;

public interface StaticResourceLoader {
	Resource getResource(String location);

	MimeType getMimeType(Resource resource);
}
