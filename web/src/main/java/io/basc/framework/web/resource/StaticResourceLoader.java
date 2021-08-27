package io.basc.framework.web.resource;

import io.basc.framework.io.Resource;
import io.basc.framework.net.MimeType;

public interface StaticResourceLoader {
	Resource getResource(String location);

	MimeType getMimeType(Resource resource);
}
