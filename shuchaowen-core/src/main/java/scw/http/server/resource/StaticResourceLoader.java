package scw.http.server.resource;

import scw.io.Resource;
import scw.io.ResourceLoader;
import scw.net.MimeType;

public interface StaticResourceLoader extends ResourceLoader {
	MimeType getMimeType(Resource resource);
}
