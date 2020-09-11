package scw.http.server.resource;

import scw.beans.annotation.AopEnable;
import scw.io.Resource;
import scw.net.MimeType;

@AopEnable(false)
public interface StaticResourceLoader {
	Resource getResource(String location);

	MimeType getMimeType(Resource resource);
}
