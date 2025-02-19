package io.basc.framework.web.resource;

import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.FileMimeTypeUitls;
import io.basc.framework.net.MimeType;
import io.basc.framework.util.io.Resource;

/**
 * 静态资源解析
 * 
 * @author wcnnkh
 *
 */
public interface StaticResourceLoader {
	default MimeType getMimeType(Resource resource) {
		return FileMimeTypeUitls.getMimeType(resource);
	}

	@Nullable
	Resource getResource(ServerHttpRequest request);
}
