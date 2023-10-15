package io.basc.framework.web.track;

import java.io.IOException;

import io.basc.framework.lang.Nullable;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

public interface HttpServiceLinkTracking {
	@Nullable
	String getTrackId(ServerHttpRequest request);

	String enter(ServerHttpRequest request);

	void out(ServerHttpRequest request, ServerHttpResponse response) throws IOException;
}
