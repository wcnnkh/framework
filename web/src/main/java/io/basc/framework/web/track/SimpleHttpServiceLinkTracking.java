package io.basc.framework.web.track;

import java.io.IOException;

import io.basc.framework.util.XUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleHttpServiceLinkTracking implements HttpServiceLinkTracking {
	private String trackKey;

	public SimpleHttpServiceLinkTracking() {
		this("track_id");
	}

	@Override
	public String getTrackId(ServerHttpRequest request) {
		String trackId = request.getHeaders().getFirst(trackKey);
		if (trackId == null) {
			trackId = (String) request.getAttribute(trackKey);
		}
		return trackId;
	}

	@Override
	public String enter(ServerHttpRequest request) {
		String trackId = getTrackId(request);
		if (trackId == null) {
			trackId = XUtils.getUUID();
			request.setAttribute(trackKey, trackId);
		}
		return trackId;
	}

	@Override
	public void out(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		String trackId = getTrackId(request);
		response.getHeaders().set(trackKey, trackId);
	}
}
